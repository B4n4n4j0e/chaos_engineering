package com.chaos.springboot.service;

import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.products.Product;
import com.chaos.springboot.util.ProductMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SimpleProductService implements ProductService {

    private final ProductMapper converter = new ProductMapper();
    private final ConcurrentSkipListMap<Long, Product> products = new ConcurrentSkipListMap<>();


    /**
     * Returns current list of Products
     * and the command line arguments.
     *
     * @param page An int containing the requested page
     * @return list of ProductDtos .
     */
    public List<ProductDto> getProducts(int page) {
        List<ProductDto> result = new ArrayList<>();
        int startPage = (page-1) * 50;
        AtomicInteger i = new AtomicInteger();
        products.forEach((ean, product) -> {
            if (result.size() < 50 && startPage <= i.get()) {
                result.add(converter.convertToDto(product));
            }
            i.getAndIncrement();

        });
        return result;
    }

    /**
     * Returns ProductDto by Id
     *
     * @param id An Long containing the ean of the Product
     * @return ProductDto .
     * @throws ResponseStatusException if entitiy not found
     */
    public ProductDto getProductById(Long id) {
        Product product = products.get(id);
        if (product != null) {
            return converter.convertToDto(product);
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
    }

    /**
     * Creates new ProductDto and adds it to the local list
     *
     * @param newProduct ProductDTO to be created
     * @return new ProductDto
     * @throws ResponseStatusException if entity already exists or wrong ean type
     */
    public ProductDto createProduct(ProductDto newProduct) {
        try {
            if (products.containsKey(Long.parseLong(newProduct.getEan()))) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "entity already exists"
                );
            }
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "EAN must be a number"
            );
        }
        Product product = converter.convertToEntity(newProduct);
        products.put(product.getEan(), product);
        newProduct.setRating(0.0);
        newProduct.setRatingCounter(0);
        return newProduct;

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
            if (products.containsKey(Long.parseLong(newProduct.getEan()))) {
                Product product = products.get(Long.parseLong(newProduct.getEan()));
                product.setName(newProduct.getName());
                return newProduct;
            }
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "EAN must be a number"
            );
        }
    }

    /**
     * Deletes ProductDto in local list
     *
     * @param id of ProductDTO to be deleted
     * @throws ResponseStatusException if entitiy not found
     */
    public void deleteProduct(Long id) {
        Product product = products.get(id);
        if (product != null) {
            products.remove(id);
        } else throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
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
            if (products.containsKey(Long.parseLong(ratedProduct.getEan()))) {
                Product product = products.get(Long.parseLong(ratedProduct.getEan()));
                product.incrementRatingCounter();
                double newValue = product.getRating() + ratedProduct.getRating();
                product.setRating(newValue);
                return converter.convertToDto(product);
            }
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "EAN must be a number"
            );
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
        List<ProductDto> result = new ArrayList<>();
        List<Product> topK = products.values().stream()
                .sorted(Comparator
                        .comparing(Product::getCalculatedRating)
                        .thenComparing(Product::getRatingCounter).reversed())
                .collect(Collectors.toList());
        for (int i = 0; i < topK.size() && result.size() <= k; i++) {
            result.add(converter.convertToDto(topK.get(i)));
        }
        return result;
    }
}

