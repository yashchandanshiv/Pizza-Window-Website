package com.example.demo.services;

import com.example.demo.entities.Orders;
import com.example.demo.entities.Product;
import com.example.demo.entities.User;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServices {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public Orders createOrder(Orders order, Long userId, Long productId) {
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);
        
        if (user == null || product == null) {
            return null;
        }

        order.setUser(user);
        order.setProduct(product);
        order.setTotalPrice(product.getPrice() * order.getQuantity());
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());
        
        return orderRepository.save(order);
    }

    public Orders getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Orders> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Orders> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public Orders updateOrderStatus(Long orderId, String status) {
        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            if (status.equals("DELIVERED")) {
                order.setDeliveredDate(LocalDateTime.now());
            }
            return orderRepository.save(order);
        }
        return null;
    }

    public boolean deleteOrder(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
