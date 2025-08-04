package com.laurarojas.ecommerceapi.repository;

import com.laurarojas.ecommerceapi.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
}
