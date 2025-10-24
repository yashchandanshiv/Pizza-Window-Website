package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private Double totalPrice;
    
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, PAYMENT_PENDING, CONFIRMED, PREPARING, DELIVERED, CANCELLED
    
    @Column(nullable = false)
    private String deliveryAddress;
    
    private String phoneNumber;
    
    @Column(nullable = false)
    private String paymentMethod = "ONLINE"; // ONLINE, COD
    
    @Column(nullable = false)
    private String paymentStatus = "PENDING"; // PENDING, COMPLETED, FAILED
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime orderDate = LocalDateTime.now();
    
    private LocalDateTime deliveredDate;
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;
}
