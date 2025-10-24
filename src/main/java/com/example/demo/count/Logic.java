package com.example.demo.count;

import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Logic {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Map<String, Long> getDashboardCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("totalUsers", userRepository.count());
        counts.put("totalProducts", productRepository.count());
        counts.put("totalOrders", orderRepository.count());
        counts.put("pendingOrders", (long) orderRepository.findByStatus("PENDING").size());
        return counts;
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getTotalProducts() {
        return productRepository.count();
    }

    public long getTotalOrders() {
        return orderRepository.count();
    }
}
