package com.laurarojas.ecommerceapi.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderDTO {
    private String orderId;
    private String userId;
    private BigDecimal totalAmount;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private Map<String, Integer> products;
}
