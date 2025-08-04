package com.laurarojas.ecommerceapi.service;

import com.laurarojas.ecommerceapi.dtos.CategoryDTO;
import com.laurarojas.ecommerceapi.dtos.CreateCategoryRequest;
import com.laurarojas.ecommerceapi.entity.CategoryEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import com.laurarojas.ecommerceapi.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> getActiveCategories() {
        List<CategoryEntity> categories = categoryRepository.findByStatus(Status.ACTIVE);
        return categories.stream()
                .map(category -> new CategoryDTO(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public CategoryDTO createCategory(CreateCategoryRequest request) {
        CategoryEntity category = new CategoryEntity();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setStatus(request.getStatus());

        CategoryEntity saved = categoryRepository.save(category);

        return new CategoryDTO(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getStatus()
        );
    }
}
