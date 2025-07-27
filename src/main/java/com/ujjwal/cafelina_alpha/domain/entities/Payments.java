package com.ujjwal.cafelina_alpha.domain.entities;

import com.ujjwal.cafelina_alpha.domain.PaymentMethods;
import com.ujjwal.cafelina_alpha.domain.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "id")
    private UUID paymentId;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Orders order; // Foreign Key, to Do

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Represent Enum as String in DB
    private PaymentMethods paymentMethod;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private LocalDateTime paymentTimeStamp;

    @PrePersist
    protected void onCreate(){
        this.paymentTimeStamp = LocalDateTime.now();
    }
}
