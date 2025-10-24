package com.example.demo.controllers;

import com.example.demo.entities.Payment;
import com.example.demo.services.PaymentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentServices paymentServices;

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Long> data) {
        Long orderId = data.get("orderId");
        Map<String, Object> response = paymentServices.createRazorpayOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, String> paymentData) {
        Map<String, Object> response = paymentServices.verifyPayment(paymentData);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/failure")
    public ResponseEntity<Map<String, Object>> paymentFailure(@RequestBody Map<String, String> data) {
        String razorpayOrderId = data.get("razorpay_order_id");
        String reason = data.get("reason");
        Map<String, Object> response = paymentServices.handlePaymentFailure(razorpayOrderId, reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable Long orderId) {
        Payment payment = paymentServices.getPaymentByOrderId(orderId);
        return payment != null ? ResponseEntity.ok(payment) : ResponseEntity.notFound().build();
    }
}
