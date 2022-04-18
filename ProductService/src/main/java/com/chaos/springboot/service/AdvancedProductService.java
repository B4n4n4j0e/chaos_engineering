package com.chaos.springboot.service;

import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.products.Product;
import com.chaos.springboot.repository.ProductRepository;
import com.chaos.springboot.util.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@Service
public class AdvancedProductService implements ProductService {

    private final ProductMapper converter = new ProductMapper();
    @Autowired
    private ProductRepository productRepository;

    /**
     * Returns current list of Products
     * and the command line arguments.
     *
     * @param page An int containing the requested page
     * @return list of ProductDtos .
     */
    public List<ProductDto> getProducts(int page) {
        // TODO: Implement
        return null;
    }

    /**
     * Returns ProductDto by Id
     *
     * @param id An Long containing the ean of the Product
     * @return ProductDto .
     * @throws ResponseStatusException if entitiy not found
     */
    public ProductDto getProductById(Long id) {
        // TODO: Implement
        return null;
    }

    /**
     * Creates new ProductDto and adds it to the local list
     *
     * @param newProduct ProductDTO to be created
     * @return new ProductDto
     * @throws ResponseStatusException if entity already exists or wrong ean type
     */
    public ProductDto createProduct(ProductDto newProduct) {
        // TODO: Implement
        return null;
    }

    /**
     * Updates ProductDto in local list
     *
     * @param newProduct A ProductDTO
     * @return new ProductDto
     * @throws ResponseStatusException if entitiy not found or wrong ean type
     */
    public ProductDto updateProduct(ProductDto newProduct) {
        // TODO: Implement
        return null;

    }

    /**
     * Deletes ProductDto in local list
     *
     * @param id of ProductDTO to be deleted
     * @throws ResponseStatusException if entitiy not found
     */
    public void deleteProduct(Long id) {
        // TODO: Implement
        return null;
    }

    /**
     * Updates Rating for ProductDto in local list
     *
     * @param ratedProduct ProductDTO to be evaluated
     * @return updated ProductDTO
     * @throws ResponseStatusException if entitiy not found or wrong ean type
     */
    @Override
    public ProductDto submitRating(ProductDto ratedProduct) {
        // TODO: Implement
        return null
    }

    /**
     * Returns Top K ProductDto list by rating
     *
     * @param k size of list
     * @return list of top k ProductDtos
     */
    @Override
    public List<ProductDto> getTopKRating(int k) {
        // TODO: Implement
        return null
    }

}

