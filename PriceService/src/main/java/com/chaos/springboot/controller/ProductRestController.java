package com.chaos.springboot.controller;

import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class ProductRestController {

    @Autowired
    private ProductService productService;


    @GetMapping("/products/{id}")
    public ProductDto product(@PathVariable Long id, @RequestParam(value = "shop") Long shopId) {
        // TODO: Implement
        return null;

    }

    @PostMapping("/products")
    public ProductDto post(@RequestBody ProductDto productDto, @RequestParam(value = "shop") Long shopId) {
        // TODO: Implement
        return null;

    }

    @PutMapping("/products")
    public ProductDto update(@RequestBody ProductDto productDto, @RequestParam(value = "shop") Long shopId) {
        // TODO: Implement
        return null;

    }

    @DeleteMapping("/products/{productId}")
    public void delete(@PathVariable Long productId, @RequestParam(value = "shop") Long shopId) {
        // TODO: Implement
    }

}
