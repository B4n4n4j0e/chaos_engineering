package com.chaos.springboot.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "product")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private long id;

    @NotNull
    @Column(nullable = false)
    private long ean;

    @NotNull
    @Column(nullable = false)
    private double price;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    public Product(long ean, double price, Shop shop) {
        this.ean = ean;
        this.shop = shop;
        this.price = price;
    }

    public Product() {

    }

    public long getEan() {
        return ean;
    }

    public void setEan(long ean) {
        this.ean = ean;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Override
    public String toString() {
        return "Product{" +
                ", ean=" + ean + '\'' +
                ", price" + price + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) {
            return false;
        }
        Product p = (Product) o;
        return ean == ((Product) o).ean;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(ean);
    }
}


