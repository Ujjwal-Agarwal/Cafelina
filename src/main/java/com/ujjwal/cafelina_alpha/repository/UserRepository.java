package com.ujjwal.cafelina_alpha.repository;

import com.ujjwal.cafelina_alpha.domain.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    Boolean existsByEmail(String email);
    Optional<Users> findByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<Users> findByUsername(String username);
}
