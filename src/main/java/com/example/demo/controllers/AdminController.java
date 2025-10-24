package com.example.demo.controllers;

import com.example.demo.entities.Admin;
import com.example.demo.entities.User;
import com.example.demo.entities.Product;
import com.example.demo.entities.Orders;
import com.example.demo.loginCredentials.AdminLogin;
import com.example.demo.services.AdminServices;
import com.example.demo.services.UserServices;
import com.example.demo.services.ProductServices;
import com.example.demo.services.OrderServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// --- For Thymeleaf dashboard: ---
@Controller
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminServices adminServices;
    @Autowired
    private UserServices userServices;
    @Autowired
    private ProductServices productServices;
    @Autowired
    private OrderServices orderServices;

    // --- Thymeleaf Admin Dashboard Page ---
    @GetMapping("/admin-dashboard")
    public String adminDashboard(Model model) {
        List<Admin> admins = adminServices.getAllAdmins();
        List<User> users = userServices.getAllUsers();
        List<Product> products = productServices.getAllProducts();
        List<Orders> orders = orderServices.getAllOrders();

        model.addAttribute("admins", admins);
        model.addAttribute("users", users);
        model.addAttribute("products", products);
        model.addAttribute("orders", orders);

        return "admin-dashboard"; // (resources/templates/admin-dashboard.html)
    }

    // --- REST APIs ---
    @RestController
    @RequestMapping("/api/admin")
    @CrossOrigin(origins = "*")
    public static class AdminRestAPI {

        @Autowired
        private AdminServices adminServices;

        @PostMapping("/create")
        public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
            Admin createdAdmin = adminServices.createAdmin(admin);
            return ResponseEntity.ok(createdAdmin);
        }

        @PostMapping("/login")
        public ResponseEntity<Map<String, Object>> loginAdmin(@RequestBody AdminLogin adminLogin) {
            Map<String, Object> response = adminServices.loginAdmin(adminLogin);
            return ResponseEntity.ok(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
            Admin admin = adminServices.getAdminById(id);
            return admin != null ? ResponseEntity.ok(admin) : ResponseEntity.notFound().build();
        }

        @GetMapping("/all")
        public ResponseEntity<List<Admin>> getAllAdmins() {
            return ResponseEntity.ok(adminServices.getAllAdmins());
        }
    }
}
