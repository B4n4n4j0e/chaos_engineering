package com.chaos.springboot.service;

import com.chaos.springboot.dto.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import reactor.core.publisher.Mono;


@Service
public class AdvancedPriceService implements PriceService {

    @Value("${spring.price-service.url}")
    private String URL;


    @Override
    public Mono<ProductDto> getProductByEanAndShopId(Long shopId, Long ean) {
        WebClient webClient = WebClient.builder()
                .baseUrl(URL)
                .build();

        UriSpec<RequestBodySpec> uriSpec = webClient.method(HttpMethod.GET);
        RequestBodySpec bodySpec = uriSpec.uri(String.format("/api/products/%d/?shop=%d", ean, shopId));
        RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue("");
        return headersSpec.exchangeToMono(resp -> {
            if (resp.statusCode()
                    .equals(HttpStatus.OK)) {
                return resp.bodyToMono(ProductDto.class);
            } else {
                return resp.createException()
                        .flatMap(Mono::error);
            }
        });


    }

}
