package com.laurarojas.ecommerceapi.repository;

import com.laurarojas.ecommerceapi.entity.CategoryEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {
    List<CategoryEntity> findByStatus(Status status);
    Optional<CategoryEntity> findByNameIgnoreCase(String name);
}
