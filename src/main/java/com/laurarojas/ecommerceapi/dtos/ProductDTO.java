package com.laurarojas.ecommerceapi.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.laurarojas.ecommerceapi.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductDTO {
    private String id;
    private String title;
    private String description;
    private BigDecimal price;
    private Double discountPercentage;
    private Integer stock;
    private Status status;
    private Set<String> categories;
}