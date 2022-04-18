package com.chaos.springboot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

public class ProductDto implements Serializable {
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    @JsonProperty
    @NotNull
    @NotEmpty
    private String ean;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer ratingCounter;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double rating;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double views;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double price;

    public ProductDto(String name, String ean, Integer ratingCounter, Double rating) {
        this.name = name;
        this.ean = ean;
        this.ratingCounter = ratingCounter;
        this.rating = rating;
    }

    public ProductDto(String name, String ean) {
        this.name = name;
        this.ean = ean;
    }

    public ProductDto(String ean, double price) {
        this.price = price;
        this.ean = ean;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public ProductDto() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String toString() {
        return "ProductDto{" +
                ", name=" + name + '\'' +
                ", ean=" + ean +
                ", ratingCounter=" + ratingCounter + '\'' +
                ", rating=" + rating + '\'' +
                ", views=" + views + '\'' +

                '}';
    }


    public Integer getRatingCounter() {
        return ratingCounter;
    }

    public void setRatingCounter(Integer ratingCounter) {
        this.ratingCounter = ratingCounter;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getViews() {
        return this.views;
    }

    public void setViews(Double views) {
        this.views = views;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDto)) {
            return false;
        }
        ProductDto p = (ProductDto) o;
        return Objects.equals(ean, ((ProductDto) o).ean);
    }

    @Override
    public int hashCode() {
        return ean.hashCode();
    }

}



