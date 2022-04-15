package com.chaos.springboot.controller;
import com.chaos.springboot.dto.ProductDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductRestController {


    @GetMapping("/products")
    public List<ProductDto> products()  {
        // TODO: Implement method
        return null;
    }

    @GetMapping("/products/{id}")
    public ProductDto product() {
        // TODO: Implement method
        return null;

    }

    @PostMapping("/products")
    public ProductDto post() {
        // TODO: Implement method
        return null;

    }

    @PutMapping("/products")
    public ProductDto update() {
        // TODO: Implement method
        return null;
    }

    @DeleteMapping("/products/{id}")
    public void delete() {
        // TODO: Implement method
    }

    @PostMapping("/products/rating")
    public ProductDto postRating() {
        // TODO: Implement method
        return null;
    }

    @GetMapping(value = {"products/rating/top","/products/rating/top/{k}"})
    public List<ProductDto> getTopKRating() {
        // TODO: Implement method
        return null;

    }

}
