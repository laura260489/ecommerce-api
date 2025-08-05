package com.laurarojas.ecommerceapi.repository;

import com.laurarojas.ecommerceapi.entity.DiscountEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscountRepository extends JpaRepository<DiscountEntity, String> {

    List<DiscountEntity> findByStatus(Status status);
}
