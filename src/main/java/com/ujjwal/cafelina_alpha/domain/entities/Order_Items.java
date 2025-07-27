package com.ujjwal.cafelina_alpha.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order_Items {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderItems",nullable = false)
    private Orders order;

    @OneToOne
    @JoinColumn(name = "menu_item_Id")
    private Menu_Items menuItem;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Long unitPrice;
}
