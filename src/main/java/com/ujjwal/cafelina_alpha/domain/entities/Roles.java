package com.ujjwal.cafelina_alpha.domain.entities;

import com.ujjwal.cafelina_alpha.domain.RoleList;
import com.ujjwal.cafelina_alpha.domain.RolePermissions;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Roles {
    @Id
//    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleList roleName;

    @ManyToMany(mappedBy = "roles")
    private Set<Users> users = new HashSet<>();

    public Roles(RoleList roleName, Set<Users> users) {
        this.roleName = roleName;
        this.users = users;
    }
}
