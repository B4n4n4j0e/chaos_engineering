package com.chaos.springboot.service;

import com.chaos.springboot.dto.ProductDto;

import java.util.List;


public interface ProductService {

    public List<ProductDto> getProducts(int page);

    public ProductDto getProductById(Long id);

    public ProductDto createProduct(ProductDto newProduct);

    public ProductDto updateProduct(ProductDto product);

    public ProductDto submitRating(ProductDto product);

    public void deleteProduct(Long id);

    public List<ProductDto> getTopKRating(int k);


}
