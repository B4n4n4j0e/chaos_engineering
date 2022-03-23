package com.chaos.springboot.products;


public class Product {
    private String name;
    private Long ean;

    public Product(String name, Long ean) {
        this.name = name;
        this.ean = ean;
    }
    public String getName() {
        return name;
    }

    public Long getEan() {
        return ean;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEan(Long ean) {
        this.ean = ean;
    }
}
