package com.laurarojas.ecommerceapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReportDTO {
    private String name;
    private String email;
    private Long totalOrders;
}
