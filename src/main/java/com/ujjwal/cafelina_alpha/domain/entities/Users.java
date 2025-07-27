package com.ujjwal.cafelina_alpha.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Users {
    @Id
//    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String passwordHash;

    @OneToMany
    @JoinColumn(name = "roles")
    private List<Roles> rolesList;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}
