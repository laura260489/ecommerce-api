package com.laurarojas.ecommerceapi.dtos;
import com.laurarojas.ecommerceapi.enums.Status;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class CreateProductRequest {
    @NotNull
    private String title;

    private String description;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @DecimalMin("0.0")
    private Double discountPercentage;

    @NotNull
    @Min(0)
    private Integer stock;

    @NotNull
    private Status status;

    @NotEmpty
    private List<String> categories;
}
