package com.ujjwal.cafelina_alpha.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Categories {
    @Id
//    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID CategoryId;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private String categoryDescription;

    private Boolean isActive = false; // Initial value as false
}
