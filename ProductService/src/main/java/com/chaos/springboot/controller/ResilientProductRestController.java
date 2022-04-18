package com.chaos.springboot.controller;

import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.products.Product;
import com.chaos.springboot.service.*;
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
    private ProductService productCacheService;
    @Autowired
    private PriceService priceService;

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

    @GetMapping("/products")
    public List<ProductDto> products(@RequestParam(value = "page", required = false) Integer page) {
        // TODO: Implement
        return null;
    }

    @GetMapping("/products/{id}")
    public ProductDto product(@PathVariable @Min(1) Long id) {
        // TODO: Implement
        return null;    }

    @PostMapping("/products")
    public ProductDto post(@RequestBody ProductDto newProduct) {
        // TODO: Implement
        return null;
    }

    @PutMapping("/products")
    public ProductDto update(@RequestBody ProductDto newProduct) {
        // TODO: Implement
        return null;    }

    @DeleteMapping("/products/{id}")
    public void delete(@PathVariable Long id) {
        // TODO: Implement
    }


    @PostMapping("/products/rating")
    public ProductDto postRating(@RequestBody ProductDto newProduct) {
        // TODO: Implement
        return null;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = {"products/rating/top", "/products/rating/top/{k}"})
    public List<ProductDto> getTopKRating(@PathVariable(required = false)  Integer k) {
        // TODO: Implement
        return null;

    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = {"products/access/top", "/products/access/top/{size}"})
    public List<ProductDto> getTopKAccess(@PathVariable(required = false)  Integer size) {
        // TODO: Implement
        return null;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = {"products/price"})
    public ProductDto getPrice(@RequestParam(value = "shop") @Min(1) long shopId, @RequestParam(value = "ean") @Min(1) long ean) {
        // TODO: Implement
        return null;
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
