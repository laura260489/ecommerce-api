package com.laurarojas.ecommerceapi.repository;

import com.laurarojas.ecommerceapi.entity.DiscountEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<DiscountEntity, String> {

    Optional<DiscountEntity> findByStatus(Status status);
}
