package com.chaos.springboot.service;

import com.chaos.springboot.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RedisUtility {

    @Autowired
    private RedisTemplate<String, ProductDto> redisTemplate;


    public void setValue(ProductDto productDto) {
        redisTemplate.opsForValue().set(productDto.getEan(), productDto);

    }

    /**
     * Checks if key is already in redis
     *
     * @return Boolean
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }


    /**
     * Gets Product from redis cache and sets view counter
     *
     * @param key String EAN of the searched ProductDto
     * @return ProductDto from cache
     * @throws ResponseStatusException if entitiy not found
     */

    public ProductDto getValue(final String key) {
        ProductDto value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
        Boolean expired = redisTemplate.opsForZSet().addIfAbsent("product", new ProductDto(value.getName(), value.getEan()), 0);
        if (expired == Boolean.TRUE) {
            redisTemplate.expire("product", Duration.ofMinutes(60L));
        }
        redisTemplate.opsForZSet().incrementScore("product", new ProductDto(value.getName(), value.getEan()), 1);

        return value;
    }

    /**
     * Gets TopKAccessList from redis cache
     *
     * @param size long for requested size of list
     * @return List<ProductDto> from cache for most accessed ProductDtos
     */

    public List<ProductDto> getTopKAccess(long size) {
        List<ProductDto> productDtos = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<ProductDto>> zSet = redisTemplate.opsForZSet().reverseRangeByScoreWithScores("product", 0L, size);
        if (zSet == null) {
            return new ArrayList<>();
        }
        zSet.forEach(product -> {
            ProductDto productDto = product.getValue();
            if (productDto != null) {
                productDto.setViews(product.getScore());
                productDtos.add(productDto);
            }
        });
        return productDtos;
    }


    /**
     * Deletes ProductDto from redis cache
     *
     * @param ean String key to delete ProductDto
     */

    public void deleteByKey(String ean) {
        redisTemplate.delete(ean);
    }
}