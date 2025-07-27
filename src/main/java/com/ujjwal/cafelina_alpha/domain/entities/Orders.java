package com.ujjwal.cafelina_alpha.domain.entities;

import com.ujjwal.cafelina_alpha.domain.OrderStatus;
import com.ujjwal.cafelina_alpha.domain.Tables;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Tables tableNumber;

    @OneToMany(mappedBy = "orderItemId", cascade = CascadeType.ALL)
    private List<Order_Items> orderItems = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private LocalDateTime orderTimeStamp;

    @PrePersist
    protected void onCreate(){
        this.orderTimeStamp = LocalDateTime.now();
    }
}
