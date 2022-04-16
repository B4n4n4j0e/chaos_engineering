package com.chaos.springboot.service;

import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.products.Product;
import com.chaos.springboot.repository.ProductRepository;
import com.chaos.springboot.util.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductCacheService implements ProductService {

    private final ProductMapper converter = new ProductMapper();
    @Autowired
    AdvancedProductService advancedProductService;
    @Autowired
    RedisUtility redisUtility;
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
        return advancedProductService.getProducts(page);
    }

    /**
     * Returns ProductDto by Id
     *
     * @param id An Long containing the ean of the Product
     * @return ProductDto .
     * @throws ResponseStatusException if entitiy not found
     */
    public ProductDto getProductById(Long id) {
        if (redisUtility.hasKey(id.toString())) {
            return redisUtility.getValue(id.toString());
        }

        Product product = productRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        });
        ProductDto productDto = converter.convertToDto(product);

        redisUtility.setValue(productDto);
        return converter.convertToDto(product);
    }

    /**
     * Creates new ProductDto and adds it to the local list
     *
     * @param newProduct ProductDTO to be created
     * @return new ProductDto
     * @throws ResponseStatusException if entity already exists or wrong ean type
     */
    public ProductDto createProduct(ProductDto newProduct) {
        return advancedProductService.createProduct(newProduct);
    }

    /**
     * Updates ProductDto in local list
     *
     * @param newProduct A ProductDTO
     * @return new ProductDto
     * @throws ResponseStatusException if entitiy not found or wrong ean type
     */
    public ProductDto updateProduct(ProductDto newProduct) {
        try {
            Product product = productRepository.findById(Long.parseLong(newProduct.getEan())).orElseThrow(() -> {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
            });
            if (!product.getName().equals(newProduct.getName())) {
                product.setName(newProduct.getName());
            }
            productRepository.save(product);
            return converter.convertToDto(product);

        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EAN must be a number");
        } finally {
            redisUtility.deleteByKey(newProduct.getEan());
        }
    }

    /**
     * Deletes ProductDto in local list
     *
     * @param id of ProductDTO to be deleted
     * @throws ResponseStatusException if entitiy not found
     */
    public void deleteProduct(Long id) {
        redisUtility.deleteByKey(id.toString());
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
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
        try {
            long id = Long.parseLong(ratedProduct.getEan());
            Product updatedProduct = productRepository.findById(id).orElseThrow(() -> {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
            });
            updatedProduct.incrementRatingCounter();
            updatedProduct.setRating(ratedProduct.getRating() + updatedProduct.getRating());
            productRepository.save(updatedProduct);
            ProductDto newProduct = converter.convertToDto(updatedProduct);
            ratedProduct.setRating(updatedProduct.getCalculatedRating());
            return newProduct;

        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EAN must be a number");
        }
    }

    /**
     * Returns Top K ProductDto list by rating
     *
     * @param k size of list
     * @return list of top k ProductDtos
     */
    @Override
    public List<ProductDto> getTopKRating(int k) {
        return advancedProductService.getTopKRating(k);
    }

    public List<ProductDto> getTopKAccess(int size) {
        return redisUtility.getTopKAccess(size);
    }

}
