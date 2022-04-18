package com.chaos.springboot.service;

import com.chaos.springboot.entities.Shop;

import java.util.List;


public interface ShopService {
    // TODO: Implement interface

    public List<Shop> getShops(int page);

    public Shop getShopById(Long id);

    public Shop createShop(Shop shop);

    public Shop updateShop(Shop shop);

    public void deleteShop(Long id);


}
