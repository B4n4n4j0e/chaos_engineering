package com.chaos.springboot.repository;

import com.chaos.springboot.entities.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    @Query(value = "SELECT CASE WHEN count(*) > 0 THEN true else false END FROM shop WHERE lower(:name) like lower(shop.name) and shop.latitude =:latitude and shop.longitude=:longitude", nativeQuery = true)
    int existsByNameAndLocation(String name, double latitude, double longitude);

}

