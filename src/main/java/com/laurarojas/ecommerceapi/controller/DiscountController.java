package com.laurarojas.ecommerceapi.controller;


import com.laurarojas.ecommerceapi.dtos.CreateDiscountRequest;
import com.laurarojas.ecommerceapi.dtos.DiscountDTO;
import com.laurarojas.ecommerceapi.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DiscountController {
    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/create-discount")
    public ResponseEntity<DiscountDTO> createCategory(@Valid @RequestBody CreateDiscountRequest request) {
        DiscountDTO created = discountService.createDiscount(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/active-discount")
    public ResponseEntity<List<DiscountDTO>> getActiveDiscount() {
        List<DiscountDTO> discounts = discountService.getActiveDiscountNow();
        if (discounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(discounts);
    }
}
