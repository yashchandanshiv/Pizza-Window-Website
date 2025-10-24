package com.example.demo.controllers;

import com.example.demo.entities.Orders;
import com.example.demo.services.OrderServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderServices orderServices;

    @PostMapping("/create")
    public ResponseEntity<Orders> createOrder(@RequestBody Map<String, Object> orderData) {
        Orders order = new Orders();
        order.setQuantity((Integer) orderData.get("quantity"));
        order.setDeliveryAddress((String) orderData.get("deliveryAddress"));
        order.setPhoneNumber((String) orderData.get("phoneNumber"));
        order.setPaymentMethod((String) orderData.get("paymentMethod"));
        
        Long userId = Long.valueOf(orderData.get("userId").toString());
        Long productId = Long.valueOf(orderData.get("productId").toString());
        
        Orders createdOrder = orderServices.createOrder(order, userId, productId);
        return createdOrder != null ? ResponseEntity.ok(createdOrder) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orders> getOrderById(@PathVariable Long id) {
        Orders order = orderServices.getOrderById(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Orders>> getAllOrders() {
        return ResponseEntity.ok(orderServices.getAllOrders());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Orders>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderServices.getOrdersByUserId(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Orders>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderServices.getOrdersByStatus(status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Orders> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> statusData) {
        Orders updatedOrder = orderServices.updateOrderStatus(id, statusData.get("status"));
        return updatedOrder != null ? ResponseEntity.ok(updatedOrder) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        boolean deleted = orderServices.deleteOrder(id);
        return deleted ? ResponseEntity.ok("Order deleted successfully") : ResponseEntity.notFound().build();
    }
}
