package com.chaos.springboot.repository;

import com.chaos.springboot.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT * from product WHERE product.shop_id =:shopId and product.ean =:ean", nativeQuery = true)
    Optional<Product> findByShopIdAndEan(long ean, long shopId);

    @Query(value = "SELECT CASE WHEN count(*) > 0 THEN true else false END FROM product WHERE product.shop_id= :shopId and product.ean= :ean", nativeQuery = true)
    int existsByShopIdAndEan(long ean, long shopId);


}