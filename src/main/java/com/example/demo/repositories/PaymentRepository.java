package com.example.demo.repositories;

import com.example.demo.entities.Payment;
import com.example.demo.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    Optional<Payment> findByOrder(Orders order);
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);
}
