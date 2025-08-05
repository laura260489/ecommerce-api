package com.laurarojas.ecommerceapi.dtos;

import com.laurarojas.ecommerceapi.enums.Status;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Data
public class CreateDiscountRequest {

    @NotNull
    private String description;

    @NotNull
    private Status status;

    @NotNull
    private Double percentage;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;
}
