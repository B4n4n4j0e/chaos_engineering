package com.chaos.springboot.controller;

import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.service.AdvancedPriceService;
import com.chaos.springboot.service.ProductCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import java.util.List;

@ConditionalOnProperty(prefix = "spring", name = "controller.resilient", havingValue = "false", matchIfMissing = true)
@RestController
@RequestMapping("/api")
public class ProductRestController {

    @Autowired
    private ProductCacheService productService;

    @Autowired
    private AdvancedPriceService priceService;

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/products")
    public List<ProductDto> products(@RequestParam(value = "page", required = false) @Min(0) Integer page) {
        if (page == null || page < 0) {
            page = 0;
        }
        return productService.getProducts(page);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/products/{id}")
    public ProductDto product(@PathVariable @Min(1) Long id) {
        return productService.getProductById(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/products")
    public ProductDto post(@RequestBody ProductDto newProduct) {
        return productService.createProduct(newProduct);

    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/products")
    public ProductDto update(@RequestBody ProductDto newProduct) {
        return productService.updateProduct(newProduct);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("/products/{id}")
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);

    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/products/rating")
    public ProductDto postRating(@RequestBody ProductDto newProduct) {
        if (newProduct.getRating() != null && (newProduct.getRating() > 5 || newProduct.getRating() < 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating x must be in the format 0 <= x <= 5");
        }
        return productService.submitRating(newProduct);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = {"products/rating/top", "/products/rating/top/{k}"})
    public List<ProductDto> getTopKRating(@PathVariable(required = false) @Min(1) Integer k) {
        if (k == null) {
            k = 10;
        }
        return productService.getTopKRating(k);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = {"products/access/top", "/products/access/top/{size}"})
    public List<ProductDto> getTopKAccess(@PathVariable(required = false) @Min(1) Integer size) {
        if (size == null) {
            size = 10;
        }
        return productService.getTopKAccess(size);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = {"products/price"})
    public ProductDto getPrice(@RequestParam(value = "shop") @Min(1) long shopId, @RequestParam(value = "ean") @Min(1) long ean) {
        Mono<ProductDto> productDtoMono = priceService.getProductByEanAndShopId(shopId, ean);
        return productDtoMono.block();
    }


}
