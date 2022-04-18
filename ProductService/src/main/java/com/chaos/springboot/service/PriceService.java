package com.chaos.springboot.service;

import com.chaos.springboot.dto.ProductDto;
import reactor.core.publisher.Mono;

public interface PriceService {

    public Mono<ProductDto> getProductByEanAndShopId(Long shopId, Long ean);

}
