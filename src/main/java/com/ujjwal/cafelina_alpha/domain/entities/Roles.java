package com.ujjwal.cafelina_alpha.domain.entities;

import com.ujjwal.cafelina_alpha.domain.RolePermissions;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Roles {
    @Id
//    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable = false)
    private String roleName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RolePermissions rolePermissions;
}
