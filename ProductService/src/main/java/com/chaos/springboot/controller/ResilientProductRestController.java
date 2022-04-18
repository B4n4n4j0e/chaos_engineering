package com.chaos.springboot.controller;

import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.service.AdvancedPriceService;
import com.chaos.springboot.service.AdvancedProductService;
import com.chaos.springboot.service.ProductCacheService;
import io.github.resilience4j.bulkhead.*;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.micrometer.tagged.*;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

@ConditionalOnProperty(prefix = "spring", name = "controller.resilient", havingValue = "true")
@RestController
@RequestMapping("/api")
public class ResilientProductRestController {

    private final ScheduledExecutorService scheduler;
    private final CircuitBreaker circuitBreaker;
    private final Bulkhead bulkhead;
    private final ThreadPoolBulkhead threadPoolBulkhead;
    private final RateLimiter rateLimiter;
    private final TimeLimiter timeLimiter;
    private final MeterRegistry meterRegistry;
    private final Retry retry;
    @Autowired
    private AdvancedProductService advancedProductService;
    @Autowired
    private ProductCacheService productCacheService;
    @Autowired
    private AdvancedPriceService priceService;

    public ResilientProductRestController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.circuitBreaker = initCircuitBreaker();
        this.bulkhead = initBulkhead();
        this.threadPoolBulkhead = initThreadPoolBulkHead();
        this.retry = initRetry();
        this.rateLimiter = initRateLimiter();
        this.timeLimiter = initTimeLimiter();
        this.scheduler = Executors.newScheduledThreadPool(3);

    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/products")
    public List<ProductDto> products(@RequestParam(value = "page", required = false) @Min(0) Integer page) {
        if (page == null || page < 0) {
            page = 0;
        }

        Integer finalPage = page;
        return execute(() -> productCacheService.getProducts(finalPage));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/products/{id}")
    public ProductDto product(@PathVariable @Min(1) Long id) {
        return executeWithFallback(() -> productCacheService.getProductById(id), throwable -> advancedProductService.getProductById(id));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/products")
    public ProductDto post(@RequestBody ProductDto newProduct) {
        return execute(() -> productCacheService.createProduct(newProduct));

    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/products")
    public ProductDto update(@RequestBody ProductDto newProduct) {
        return execute(() -> productCacheService.updateProduct(newProduct));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("/products/{id}")
    public void delete(@PathVariable Long id) {
        executeWithFallback(() -> {
                    productCacheService.deleteProduct(id);
                    return 0;
                },
                (throwable -> {
                    advancedProductService.deleteProduct(id);
                    return 1;
                }));
    }


    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/products/rating")
    public ProductDto postRating(@RequestBody ProductDto newProduct) {
        if (newProduct.getRating() != null && (newProduct.getRating() > 5 || newProduct.getRating() < 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating x must be in the format 0 <= x <= 5");
        }
        return execute(() -> productCacheService.submitRating(newProduct));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = {"products/rating/top", "/products/rating/top/{k}"})
    public List<ProductDto> getTopKRating(@PathVariable(required = false) @Min(1) Integer k) {
        if (k == null) {
            k = 10;
        }
        Integer finalK = k;
        return execute(() -> productCacheService.getTopKRating(finalK));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = {"products/access/top", "/products/access/top/{size}"})
    public List<ProductDto> getTopKAccess(@PathVariable(required = false) @Min(1) Integer size) {
        if (size == null) {
            size = 10;
        }
        Integer finalSize = size;
        return execute(() -> productCacheService.getTopKAccess(finalSize));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = {"products/price"})
    public ProductDto getPrice(@RequestParam(value = "shop") @Min(1) long shopId, @RequestParam(value = "ean") @Min(1) long ean) {
        // Async function
        Mono<ProductDto> productDtoMono = priceService.getProductByEanAndShopId(shopId, ean);
        ProductDto ratedProduct;
        try {
            ratedProduct = this.product(ean);
            // Is catched to prevent a 404 error if product does not exist in db
        } catch (ResponseStatusException e) {
            ratedProduct = new ProductDto();
        }

        ProductDto finalRatedProduct = ratedProduct;
        CompletableFuture<ProductDto> result = executeAsyncWithFallback(() -> {
            productDtoMono
                    .doOnError(throwable -> {
                        if (finalRatedProduct.getName() == null) {
                            // is created to trigger an execution Exception
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
                        }
                    }).doOnSuccess(dto -> {
                        // on success set price
                        finalRatedProduct.setPrice(dto.getPrice());
                        finalRatedProduct.setEan(dto.getEan());
                        // waits for response
                    }).block();
            return finalRatedProduct;
        }, throwable -> {
            // checks whether the product exists locally and raises an exception if it does not exist locally and remotely
            if (finalRatedProduct.getEan() != null) {
                return finalRatedProduct;
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        });
        try {
            return result.get();
        } catch (InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "This service is currently unavailable, please try again later");
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ResponseStatusException) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
            }
        }
        return finalRatedProduct;
    }


    private <T> T execute(Supplier<T> supplier) {
        return Decorators.ofSupplier(supplier)
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .withRetry(retry)
                .get();
    }

    private <T> T executeWithFallback(Supplier<T> supplier, Function<Throwable, T> fallback) {
        return Decorators.ofSupplier(supplier)
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .withRetry(retry)
                .withFallback(fallback)
                .get();
    }

    private <T> CompletableFuture<T> executeAsync(Supplier<T> supplier) {
        return Decorators.ofSupplier(supplier)
                .withThreadPoolBulkhead(threadPoolBulkhead)
                .withTimeLimiter(timeLimiter, scheduler)
                .withCircuitBreaker(circuitBreaker)
                .withRetry(retry, scheduler)
                .get().toCompletableFuture();
    }

    private <T> CompletableFuture<T> executeAsyncWithFallback(Supplier<T> supplier, Function<Throwable, T> fallback) {
        return Decorators.ofSupplier(supplier)
                .withThreadPoolBulkhead(threadPoolBulkhead)
                .withTimeLimiter(timeLimiter, scheduler)
                .withCircuitBreaker(circuitBreaker)
                .withFallback(Arrays.asList(TimeoutException.class, CallNotPermittedException.class, BulkheadFullException.class),
                        fallback)
                .get().toCompletableFuture();
    }


    private Bulkhead initBulkhead() {
        BulkheadRegistry registry = BulkheadRegistry.ofDefaults();
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(3)
                .build();
        Bulkhead bulkhead = registry.bulkhead("default", config);
        TaggedBulkheadMetrics
                .ofBulkheadRegistry(registry)
                .bindTo(meterRegistry);
        return bulkhead;
    }

    private ThreadPoolBulkhead initThreadPoolBulkHead() {
        ThreadPoolBulkheadRegistry registry = ThreadPoolBulkheadRegistry.ofDefaults();
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .queueCapacity(20)
                .build();
        ThreadPoolBulkhead bulkhead = registry.bulkhead("default", config);
        TaggedThreadPoolBulkheadMetrics
                .ofThreadPoolBulkheadRegistry(registry)
                .bindTo(meterRegistry);
        return bulkhead;
    }

    private CircuitBreaker initCircuitBreaker() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .ignoreExceptions(ResponseStatusException.class)
                .build();
        TaggedCircuitBreakerMetrics
                .ofCircuitBreakerRegistry(registry)
                .bindTo(meterRegistry);
        return registry.circuitBreaker("default", config);
    }

    private Retry initRetry() {
        RetryRegistry registry = RetryRegistry.ofDefaults();
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(4)
                .waitDuration(Duration.ofSeconds(1))
                .build();
        TaggedRetryMetrics
                .ofRetryRegistry(registry)
                .bindTo(meterRegistry);
        return registry.retry("default", config);
    }

    private RateLimiter initRateLimiter() {
        RateLimiterRegistry registry = RateLimiterRegistry.ofDefaults();
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMillis(1))
                .limitForPeriod(10)
                .timeoutDuration(Duration.ofMillis(25))
                .build();
        TaggedRateLimiterMetrics
                .ofRateLimiterRegistry(registry)
                .bindTo(meterRegistry);
        return registry.rateLimiter("default", config);
    }

    private TimeLimiter initTimeLimiter() {
        TimeLimiterRegistry registry = TimeLimiterRegistry.ofDefaults();
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .cancelRunningFuture(true)
                .timeoutDuration(Duration.ofMillis(500))
                .build();
        TaggedTimeLimiterMetrics
                .ofTimeLimiterRegistry(registry)
                .bindTo(meterRegistry);
        return registry.timeLimiter("default", config);
    }


}
