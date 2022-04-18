package com.chaos.springboot.service;

import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.entities.Product;
import com.chaos.springboot.entities.Shop;
import com.chaos.springboot.repository.ProductRepository;
import com.chaos.springboot.repository.ShopRepository;
import com.chaos.springboot.util.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdvancedProductService implements ProductService {

    private final ProductMapper converter = new ProductMapper();
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ShopRepository shopRepository;

    @Override
    public ProductDto getProductByEanAndShopId(long ean, long shopId) {
        Product product = productRepository.findByShopIdAndEan(ean, shopId).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        });
        return converter.convertToDto(product);
    }

    @Override
    public ProductDto createProduct(ProductDto productDto, long shopId) {
        try {
            long productEan = Long.parseLong(productDto.getEan());
            if (productRepository.existsByShopIdAndEan(productEan, shopId) == 0) {
                Shop shop = shopRepository.findById(shopId).orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shop does not exist");
                });
                Product product = new Product(productEan, productDto.getPrice(), shop);
                productRepository.save(product);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product already exists");
            }
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EAN must be a number");
        }
        return productDto;

    }

    @Override
    public ProductDto updateProduct(ProductDto productDto, long shopId) {
        try {
            long productEan = Long.parseLong(productDto.getEan());
            Product product = productRepository.findByShopIdAndEan(productEan, shopId).orElseThrow(() -> {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
            });
            product.setPrice(productDto.getPrice());
            productRepository.save(product);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EAN must be a number");
        }
        return productDto;

    }

    @Override
    public void deleteProduct(long ean, long shopId) {
        Product product = productRepository.findByShopIdAndEan(ean, shopId).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        });
        productRepository.delete(product);
    }
}
