package com.laurarojas.ecommerceapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReportDTO {
    private String id;
    private String title;
    private String category;
    private Long totalSell;
}
