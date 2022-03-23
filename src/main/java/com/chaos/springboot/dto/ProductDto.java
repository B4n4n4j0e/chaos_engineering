package com.chaos.springboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ProductDto {
    @JsonProperty
    @NotNull
    @NotEmpty
    private String name;

    @JsonProperty
    @NotNull
    @NotEmpty
    private Long ean;

    public ProductDto(String name, Long ean) {
        this.name = name;
        this.ean = ean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEan() {
        return ean;
    }

    public void setEan(Long ean) {
        this.ean = ean;
    }
}
