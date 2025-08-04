package com.laurarojas.ecommerceapi.dtos;


import com.laurarojas.ecommerceapi.enums.Status;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class CreateCategoryRequest {

    @NotNull
    private String name;

    private String description;

    @NotNull
    private Status status;
}
