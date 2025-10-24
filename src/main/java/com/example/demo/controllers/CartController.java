package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@CrossOrigin(origins = "*")
public class CartController {

    // In-memory cart storage (demo only)
    private final Map<Long, List<Map<String, Object>>> userCarts = new HashMap<>();

    // Thymeleaf route for /myCart page
    @GetMapping("/myCart")
    public String myCart(Model model) {
        // Optionally add test data for first render:
        // model.addAttribute("cart", ...);
        return "myCart";
    }

    // REST: Add product to user's cart
    @PostMapping("/api/cart/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, Object> cartItem) {
        Long userId = Long.valueOf(cartItem.get("userId").toString());
        userCarts.computeIfAbsent(userId, k -> new ArrayList<>());
        userCarts.get(userId).add(cartItem);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Item added to cart");
        response.put("cartSize", userCarts.get(userId).size());
        return ResponseEntity.ok(response);
    }

    // REST: Get all products in user's cart
    @GetMapping("/api/cart/user/{userId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getCartByUserId(@PathVariable Long userId) {
        List<Map<String, Object>> cart = userCarts.getOrDefault(userId, new ArrayList<>());
        return ResponseEntity.ok(cart);
    }

    // REST: Remove a product from user's cart
    @DeleteMapping("/api/cart/remove")
    @ResponseBody
    public ResponseEntity<Map<String, String>> removeFromCart(@RequestBody Map<String, Object> data) {
        Long userId = Long.valueOf(data.get("userId").toString());
        Long productId = Long.valueOf(data.get("productId").toString());
        if (userCarts.containsKey(userId)) {
            userCarts.get(userId).removeIf(item -> productId.equals(Long.valueOf(item.get("productId").toString())));
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "Item removed from cart");
        return ResponseEntity.ok(response);
    }

    // REST: Clear all products from user's cart
    @DeleteMapping("/api/cart/clear/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> clearCart(@PathVariable Long userId) {
        userCarts.remove(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart cleared successfully");
        return ResponseEntity.ok(response);
    }
}
