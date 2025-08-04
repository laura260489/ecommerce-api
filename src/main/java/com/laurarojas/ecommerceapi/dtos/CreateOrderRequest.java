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
    private UUID userId;

    @NotEmpty
    private Map<UUID, @Min(1) Integer> products;
}