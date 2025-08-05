package com.laurarojas.ecommerceapi.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class CreateOrderRequest {
    @NotNull
    private String userId;

    @NotEmpty
    private Map<String, @Min(1) Integer> products;
}