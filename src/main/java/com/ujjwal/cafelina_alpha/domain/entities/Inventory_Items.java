package com.ujjwal.cafelina_alpha.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Inventory_Items {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID inventoryItemId;

    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false)
    private String unit; // This the unit being used to measure something. i.e Kg/Lbs

    @Column(nullable = false)
    private Long currentStock;

    @Column(nullable = false)
    private Long minimumStockLevel;

    @Column(nullable = false)
    private Long costPerUnit;
}
