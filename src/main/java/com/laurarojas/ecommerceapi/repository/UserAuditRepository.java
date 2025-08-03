package com.laurarojas.ecommerceapi.repository;

import com.laurarojas.ecommerceapi.entity.UserAuditEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Transactional
public interface UserAuditRepository extends JpaRepository<UserAuditEntity, UUID> {
}
