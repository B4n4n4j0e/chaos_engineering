package com.chaos.springboot.controller;

import com.chaos.springboot.entities.Shop;
import com.chaos.springboot.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ShopRestController {

    @Autowired
    private ShopService shopService;

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/shops")
    public List<Shop> shops(@RequestParam(value = "page", required = false) Integer page) {

        // TODO: Implement
        return null;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/shops/{id}")
    public Shop shop(@PathVariable Long id) {
        // TODO: Implement
        return null;    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/shops")
    public Shop post() {
        // TODO: Implement
        return null;

    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/shops")
    public Shop update(@RequestBody Shop shop) {
        // TODO: Implement
        return null;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("/shops/{id}")
    public void delete() {
        // TODO: Implement

    }


}
