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
    // TODO: Implement

    @Autowired
    private RedisTemplate<String, ProductDto> redisTemplate;


}