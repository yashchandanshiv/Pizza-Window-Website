package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    // In-memory cart storage (For production, use database or session)
    private Map<Long, List<Map<String, Object>>> userCarts = new HashMap<>();

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, Object> cartItem) {
        Long userId = Long.valueOf(cartItem.get("userId").toString());
        
        if (!userCarts.containsKey(userId)) {
            userCarts.put(userId, new ArrayList<>());
        }
        
        userCarts.get(userId).add(cartItem);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Item added to cart");
        response.put("cartSize", userCarts.get(userId).size());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getCartByUserId(@PathVariable Long userId) {
        List<Map<String, Object>> cart = userCarts.getOrDefault(userId, new ArrayList<>());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, String>> removeFromCart(@RequestBody Map<String, Object> data) {
        Long userId = Long.valueOf(data.get("userId").toString());
        Long productId = Long.valueOf(data.get("productId").toString());
        
        if (userCarts.containsKey(userId)) {
            List<Map<String, Object>> cart = userCarts.get(userId);
            cart.removeIf(item -> item.get("productId").equals(productId));
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Item removed from cart");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Map<String, String>> clearCart(@PathVariable Long userId) {
        userCarts.remove(userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart cleared successfully");
        return ResponseEntity.ok(response);
    }
}
