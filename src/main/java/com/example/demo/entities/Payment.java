package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;
    
    @Column(nullable = false)
    private String razorpayOrderId;
    
    private String razorpayPaymentId;
    
    private String razorpaySignature;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(nullable = false)
    private String currency = "INR";
    
    @Column(nullable = false)
    private String status = "CREATED"; // CREATED, PAID, FAILED
    
    private String paymentMethod; // card, netbanking, upi, wallet
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime paidAt;
    
    private String failureReason;
}
