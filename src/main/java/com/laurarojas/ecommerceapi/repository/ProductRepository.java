package com.laurarojas.ecommerceapi.repository;

import com.laurarojas.ecommerceapi.entity.ProductEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    Page<ProductEntity> findByTitleContainingIgnoreCaseAndStatus(String title, Status status, Pageable pageable);
    List<ProductEntity> findByCategoriesNameAndStatus(String categoryName, Status status);
    Optional<ProductEntity> findById(String id);
    List<ProductEntity> findByStatus(Status status);
}
