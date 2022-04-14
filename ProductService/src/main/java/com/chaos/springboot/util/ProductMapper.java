package com.chaos.springboot.util;

import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.products.Product;

public class ProductMapper {


    public ProductMapper() {
    }

    /**
     * Converts Product to ProductDTO
     * @param product Product to be converted
     * @return ProductDTO
     */
    public ProductDto convertToDto(Product product) {
        return new ProductDto(product.getName(),product.getEan().toString(), product.getRatingCounter(), product.getCalculatedRating());
    }

    /**
     * Converts ProductDTO to Product
     * @param product ProductDTO to be converted
     * @return Product
     */
    public Product convertToEntity(ProductDto product) {
        return new Product(product.getName(), Long.parseLong(product.getEan()));
    }

}
