package com.chaos.springboot.controller;
import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")

public class ProductRestController {

    @Autowired
    private ProductService productService;


    @GetMapping("/products")
    public List<ProductDto> products()  {
        return productService.getProducts();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> product(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDto> post(@RequestBody ProductDto newProduct) {
        ProductDto product = productService.createProduct(newProduct);
        return new ResponseEntity<>(product,HttpStatus.OK);

    }

    @PutMapping("/products")
    public ResponseEntity<ProductDto> update(@RequestBody ProductDto newProduct) {
        ProductDto product = productService.updateProduct(newProduct);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ProductDto> delete(@PathVariable Long id) {
        ProductDto product = productService.deleteProduct(id);
        return new ResponseEntity<>(product,HttpStatus.OK);

    }

}
