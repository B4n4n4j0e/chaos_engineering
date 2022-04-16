package com.chaos.springboot.products;

import com.chaos.springboot.util.MathHelper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "product")
public class Product implements Serializable {

    @NotEmpty
    @NotNull
    private String name;
    @NotNull
    @Id
    @Column(unique = true, nullable = false)
    private long ean;
    private int ratingCounter;
    private double rating;

    public Product(String name, long ean) {
        this.name = name;
        this.ean = ean;
        this.ratingCounter = 0;
        this.rating = 0;
    }

    public Product() {
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

    public void setEan(long ean) {
        this.ean = ean;
    }

    public int getRatingCounter() {
        return ratingCounter;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getCalculatedRating() {
        if (this.ratingCounter == 0) {
            return 0.0;
        }
        return MathHelper.round(this.getRating() / this.getRatingCounter(), 2);
    }

    public void incrementRatingCounter() {
        this.ratingCounter += 1;
    }

    @Override
    public String toString() {
        return "Product{" +
                ", name=" + name + '\'' +
                ", ean=" + ean + '\'' +
                ", ratingCounter" + ratingCounter + '\'' +
                ", rating" + rating +
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


