package com.example.demo.services;

import com.example.demo.config.JwtUtil;
import com.example.demo.entities.Admin;
import com.example.demo.loginCredentials.AdminLogin;
import com.example.demo.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminServices {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> loginAdmin(AdminLogin adminLogin) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Admin> adminOpt = adminRepository.findByEmail(adminLogin.getEmail());
        
        if (adminOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return response;
        }

        Admin admin = adminOpt.get();
        
        if (!passwordEncoder.matches(adminLogin.getPassword(), admin.getPassword())) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return response;
        }

        String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole());

        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("admin", admin);
        return response;
    }

    public Admin createAdmin(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole("ADMIN");
        return adminRepository.save(admin);
    }

    public Admin getAdminById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
}
