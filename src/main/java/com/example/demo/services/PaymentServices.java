package com.example.demo.services;

import com.example.demo.entities.Orders;
import com.example.demo.entities.Payment;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServices {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.currency}")
    private String currency;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Map<String, Object> createRazorpayOrder(Long orderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Orders order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                response.put("success", false);
                response.put("message", "Order not found");
                return response;
            }

            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (order.getTotalPrice() * 100)); // Amount in paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "order_" + orderId);
            
            JSONObject notes = new JSONObject();
            notes.put("orderId", orderId);
            notes.put("userId", order.getUser().getId());
            notes.put("productId", order.getProduct().getId());
            orderRequest.put("notes", notes);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            // Save payment record
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setRazorpayOrderId(razorpayOrder.get("id"));
            payment.setAmount(order.getTotalPrice());
            payment.setCurrency(currency);
            payment.setStatus("CREATED");
            paymentRepository.save(payment);

            // Update order status
            order.setStatus("PAYMENT_PENDING");
            orderRepository.save(order);

            response.put("success", true);
            response.put("orderId", razorpayOrder.get("id"));
            response.put("amount", razorpayOrder.get("amount"));
            response.put("currency", razorpayOrder.get("currency"));
            response.put("keyId", razorpayKeyId);

        } catch (RazorpayException e) {
            response.put("success", false);
            response.put("message", "Payment creation failed: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> verifyPayment(Map<String, String> paymentData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String razorpayOrderId = paymentData.get("razorpay_order_id");
            String razorpayPaymentId = paymentData.get("razorpay_payment_id");
            String razorpaySignature = paymentData.get("razorpay_signature");

            // Verify signature
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            boolean isValidSignature = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (isValidSignature) {
                Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId).orElse(null);
                
                if (payment != null) {
                    payment.setRazorpayPaymentId(razorpayPaymentId);
                    payment.setRazorpaySignature(razorpaySignature);
                    payment.setStatus("PAID");
                    payment.setPaidAt(LocalDateTime.now());
                    paymentRepository.save(payment);

                    // Update order
                    Orders order = payment.getOrder();
                    order.setPaymentStatus("COMPLETED");
                    order.setStatus("CONFIRMED");
                    orderRepository.save(order);

                    response.put("success", true);
                    response.put("message", "Payment verified successfully");
                    response.put("orderId", order.getId());
                }
            } else {
                response.put("success", false);
                response.put("message", "Invalid payment signature");
            }

        } catch (RazorpayException e) {
            response.put("success", false);
            response.put("message", "Payment verification failed: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> handlePaymentFailure(String razorpayOrderId, String reason) {
        Map<String, Object> response = new HashMap<>();
        
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId).orElse(null);
        
        if (payment != null) {
            payment.setStatus("FAILED");
            payment.setFailureReason(reason);
            paymentRepository.save(payment);

            Orders order = payment.getOrder();
            order.setPaymentStatus("FAILED");
            order.setStatus("CANCELLED");
            orderRepository.save(order);

            response.put("success", true);
            response.put("message", "Payment failure recorded");
        } else {
            response.put("success", false);
            response.put("message", "Payment record not found");
        }

        return response;
    }

    public Payment getPaymentByOrderId(Long orderId) {
        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            return paymentRepository.findByOrder(order).orElse(null);
        }
        return null;
    }
}
