package com.ujjwal.cafelina_alpha.repository;

import com.ujjwal.cafelina_alpha.domain.RoleList;
import com.ujjwal.cafelina_alpha.domain.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Roles, UUID> {
    Optional<Roles> findByRoleName(RoleList roleName);

    RoleList RoleName(RoleList roleName);
}
