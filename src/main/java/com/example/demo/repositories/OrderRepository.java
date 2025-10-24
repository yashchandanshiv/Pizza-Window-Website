package com.example.demo.repositories;

import com.example.demo.entities.Orders;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUser(User user);
    List<Orders> findByUserId(Long userId);
    List<Orders> findByStatus(String status);
    List<Orders> findByUserOrderByOrderDateDesc(User user);
}
