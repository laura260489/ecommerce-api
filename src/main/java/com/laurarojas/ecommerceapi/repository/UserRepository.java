package com.laurarojas.ecommerceapi.repository;

import com.laurarojas.ecommerceapi.entity.UserEntity;
import com.laurarojas.ecommerceapi.enums.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles"})
    Optional<UserEntity> findUserWithRolesByEmail(String email);

    List<UserEntity> findByStatus(Status status);

    Optional<UserEntity> findByIdAndStatus(String id, Status status);
}
