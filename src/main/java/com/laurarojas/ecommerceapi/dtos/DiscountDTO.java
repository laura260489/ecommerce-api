package com.laurarojas.ecommerceapi.dtos;

import com.laurarojas.ecommerceapi.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDTO {
    private String id;
    private Double percentage;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Status status;
}
