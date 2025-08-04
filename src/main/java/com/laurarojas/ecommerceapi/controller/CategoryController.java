package com.laurarojas.ecommerceapi.controller;

import com.laurarojas.ecommerceapi.dtos.CategoryDTO;
import com.laurarojas.ecommerceapi.dtos.CreateCategoryRequest;
import com.laurarojas.ecommerceapi.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list-categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(categoryService.getActiveCategories());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/create-category")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryDTO created = categoryService.createCategory(request);
        return ResponseEntity.ok(created);
    }
}
