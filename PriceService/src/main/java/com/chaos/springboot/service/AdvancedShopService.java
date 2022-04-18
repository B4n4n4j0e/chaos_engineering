package com.chaos.springboot.service;

import com.chaos.springboot.entities.Shop;
import com.chaos.springboot.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class AdvancedShopService implements ShopService {

    @Autowired
    private ShopRepository shopRepository;

    /**
     * Returns current list of Shops
     * and the command line arguments.
     *
     * @param page An int containing the requested page
     * @return list of Shops .
     */

    public List<Shop> getShops(int page) {
        return shopRepository.findAll(PageRequest.of(page, 50)).getContent();
    }

    /**
     * Returns Shop by Id
     *
     * @param id An Long containing the id of the Shop
     * @return Shop .
     * @throws ResponseStatusException if entitiy not found
     */
    public Shop getShopById(Long id) {
        return shopRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        });
    }

    /**
     * Creates new shop and adds it to the database
     *
     * @param newShop to be created
     * @return new shop
     * @throws ResponseStatusException if entity already exists or wrong ean type
     */
    public Shop createShop(Shop newShop) {
        if (shopRepository.existsByNameAndLocation(newShop.getName(), newShop.getLatitude(), newShop.getLongitude()) == 0) {
            shopRepository.save(newShop);
            return newShop;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "entity already exists");
        }
    }


    /**
     * Updates shop in database
     *
     * @param newShop A shop
     * @return new shop
     * @throws ResponseStatusException if entitiy not found
     */
    public Shop updateShop(Shop newShop) {
        Shop shop;
        shop = shopRepository.findById(newShop.getId()).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        });

        if (!shop.getName().equals(newShop.getName())) {
            shop.setName(newShop.getName());
        }

        if (!(shop.getLatitude() != newShop.getLatitude())) {
            shop.setLatitude(newShop.getLatitude());
        }
        if (!(shop.getLongitude() != newShop.getLongitude())) {
            shop.setLatitude(newShop.getLatitude());
        }
        shopRepository.save(shop);
        return shop;
    }

    /**
     * Deletes shop in database
     *
     * @param id of shop to be deleted
     * @throws ResponseStatusException if entitiy not found
     */
    public void deleteShop(Long id) {
        if (shopRepository.existsById(id)) {
            shopRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }
}

