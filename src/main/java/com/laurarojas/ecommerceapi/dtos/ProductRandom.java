package com.laurarojas.ecommerceapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRandom {
    private ProductDTO product;
    private int quantity;
}
