package com.ujjwal.cafelina_alpha.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Menu_Items {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "id", nullable = false, updatable = false)
    private UUID menuItemId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double price;

    @OneToMany
    @JoinColumn(name = "CategoryId")
    private List<Categories> categoriesList = new ArrayList<>();

    private Boolean available = false;

    private String imageURL; // Image will be saved on Redis
}
