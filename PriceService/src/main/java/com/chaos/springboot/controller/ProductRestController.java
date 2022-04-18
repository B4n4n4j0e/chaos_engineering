package com.chaos.springboot.controller;

import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.service.AdvancedProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api")
public class ProductRestController {

    @Autowired
    private AdvancedProductService productService;


    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/products/{id}")
    public ProductDto product(@PathVariable Long id, @RequestParam(value = "shop") Long shopId) {
        return productService.getProductByEanAndShopId(id, shopId);

    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/products")
    public ProductDto post(@RequestBody ProductDto productDto, @RequestParam(value = "shop") Long shopId) {
        if (productDto.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be > 0");
        }
        return productService.createProduct(productDto, shopId);

    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/products")
    public ProductDto update(@RequestBody ProductDto productDto, @RequestParam(value = "shop") Long shopId) {
        if (productDto.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be > 0");
        }
        return productService.updateProduct(productDto, shopId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("/products/{productId}")
    public void delete(@PathVariable Long productId, @RequestParam(value = "shop") Long shopId) {
        productService.deleteProduct(productId, shopId);

    }

}
