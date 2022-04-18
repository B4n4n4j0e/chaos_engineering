package com.chaos.springboot.controller;

import com.chaos.springboot.entities.Shop;
import com.chaos.springboot.service.AdvancedShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ShopRestController {

    @Autowired
    private AdvancedShopService shopService;

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/shops")
    public List<Shop> shops(@RequestParam(value = "page", required = false) Integer page) {
        if (page == null || page < 0) {
            page = 0;
        }
        return shopService.getShops(page);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/shops/{id}")
    public Shop shop(@PathVariable Long id) {
        return shopService.getShopById(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/shops")
    public Shop post(@RequestBody Shop shop) {
        if (shop.getLatitude() < -90 || shop.getLatitude() > 90 || shop.getLongitude() < -180 || shop.getLongitude() > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "provided geo data is wrong");
        }
        return shopService.createShop(shop);

    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/shops")
    public Shop update(@RequestBody Shop shop) {
        if (shop.getLatitude() < -90 || shop.getLatitude() > 90 || shop.getLongitude() < -180 || shop.getLongitude() > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "provided geo data is wrong");
        }
        return shopService.updateShop(shop);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("/shops/{id}")
    public void delete(@PathVariable Long id) {
        shopService.deleteShop(id);

    }


}
