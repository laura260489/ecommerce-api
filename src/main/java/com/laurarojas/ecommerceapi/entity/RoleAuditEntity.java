package com.laurarojas.ecommerceapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="roles_audit")
public class RoleAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "user", length = 150, updatable = false)
    private String user;

    @Column(name = "action", length = 50, updatable = false)
    private String action;

    @Column(name = "old_data", columnDefinition = "json", updatable = false)
    private String oldData;

    @Column(name = "new_data", columnDefinition = "json", updatable = false)
    private String newData;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
}
