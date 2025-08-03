package com.laurarojas.ecommerceapi.repository;

import com.laurarojas.ecommerceapi.entity.RoleAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleAuditRepository extends JpaRepository<RoleAuditEntity, UUID> {
}
