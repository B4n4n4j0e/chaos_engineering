package com.chaos.springboot.util;

import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.entities.Product;

public class ProductMapper {


    public ProductMapper() {
    }

    /**
     * Converts Product to ProductDTO
     *
     * @param product Product to be converted
     * @return ProductDTO
     */
    public ProductDto convertToDto(Product product) {
        return new ProductDto(String.valueOf(product.getEan()), product.getPrice());
    }


}
