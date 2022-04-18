package com.chaos.springboot.service;

import com.chaos.springboot.dto.ProductDto;


public interface ProductService {
    // TODO: Implement interface
    ProductDto getProductByEanAndShopId(long ean, long shopId);

    public ProductDto createProduct(ProductDto productDto, long shopId);

    public ProductDto updateProduct(ProductDto productDto, long shopId);

    public void deleteProduct(long ean, long shopId);

    ;


}
