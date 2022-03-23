package com.chaos.springboot.repository;
import com.chaos.springboot.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}