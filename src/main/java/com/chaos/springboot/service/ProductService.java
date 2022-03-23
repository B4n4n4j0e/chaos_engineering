package com.chaos.springboot.service;
import com.chaos.springboot.dto.ProductDto;
import com.chaos.springboot.products.Product;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.chaos.springboot.Main;

@Service
public class ProductService {

    private static ConcurrentHashMap<Long, Product> products = new ConcurrentHashMap<>();


    public List<ProductDto> getProducts() {
        products.put(22L,new Product("ABC",22L));
        List<ProductDto>result = new ArrayList<>();
        products.forEachValue(100, product -> result.add(convertToDto(product)));
        return result;
    }

    public ProductDto getProductById(Long id) {
        Product product = products.get(id);
        if (product != null) {
            return  convertToDto(product);
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );    }

    public ProductDto createProduct(ProductDto newProduct) {
        if (products.containsKey(newProduct.getEan())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "entity already exists"
            );
        }
        Product product = convertToEntity(newProduct);
        products.put(product.getEan(),product);
        return newProduct;

    }

    public ProductDto updateProduct(ProductDto newProduct) {
        if (products.containsKey(newProduct.getEan())) {
            Product product = products.get(newProduct.getEan());
            product.setName(newProduct.getName());
            return newProduct;
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );    }


    public ProductDto deleteProduct(Long id) {
        Product product = products.get(id);
        if (product != null) {
            ProductDto result = convertToDto(product);
            products.remove(id);
            return result;
        }
        else     throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );

    }

    private ProductDto convertToDto(Product product) {
        return new ProductDto(product.getName(),product.getEan());
    }
    private Product convertToEntity(ProductDto product) {
        return new Product(product.getName(),product.getEan());
    }



}
