package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@CrossOrigin(origins = "*")
public class CartController {

    // In-memory cart storage (demo only - in production use database)
    private final Map<Long, List<Map<String, Object>>> userCarts = new HashMap<>();

    // =========================
    // THYMELEAF VIEW ROUTE
    // =========================

    /**
     * Render the myCart.html page with cart items
     */
    @GetMapping("/myCart")
    public String myCart(@RequestParam(defaultValue = "1") Long userId, Model model) {
        try {
            List<Map<String, Object>> cart = userCarts.getOrDefault(userId, new ArrayList<>());

            // Calculate total safely
            double total = 0.0;
            if (cart != null && !cart.isEmpty()) {
                for (Map<String, Object> item : cart) {
                    try {
                        Object subtotalObj = item.get("subtotal");
                        if (subtotalObj != null) {
                            total += Double.parseDouble(String.valueOf(subtotalObj));
                        }
                    } catch (Exception e) {
                        System.out.println("Error calculating item total: " + e.getMessage());
                    }
                }
            }

            model.addAttribute("cart", cart);
            model.addAttribute("total", total);

            System.out.println("Cart size: " + cart.size());
            System.out.println("Total: " + total);

            return "myCart";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("cart", new ArrayList<>());
            model.addAttribute("total", 0.0);
            return "myCart";
        }
    }

    // =========================
    // REST API ENDPOINTS
    // =========================

    /**
     * Add a product to user's cart
     */
    @PostMapping("/api/cart/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, Object> cartItem) {
        try {
            Long userId = Long.valueOf(String.valueOf(cartItem.get("userId")));
            Long productId = Long.valueOf(String.valueOf(cartItem.get("productId")));

            // Initialize cart for user if not exists
            userCarts.computeIfAbsent(userId, k -> new ArrayList<>());

            // Check if product already exists in cart
            List<Map<String, Object>> cart = userCarts.get(userId);
            boolean productExists = false;

            for (Map<String, Object> item : cart) {
                if (productId.equals(Long.valueOf(String.valueOf(item.get("productId"))))) {
                    // Product exists, increment quantity
                    int currentQty = Integer.parseInt(String.valueOf(item.get("qty")));
                    int newQty = currentQty + 1;
                    item.put("qty", newQty);

                    // Recalculate subtotal
                    double price = Double.parseDouble(String.valueOf(item.get("price")));
                    item.put("subtotal", price * newQty);

                    productExists = true;
                    break;
                }
            }

            // If product doesn't exist, add new entry
            if (!productExists) {
                double price = Double.parseDouble(String.valueOf(cartItem.get("price")));
                int qty = Integer.parseInt(String.valueOf(cartItem.get("qty")));
                double subtotal = price * qty;
                cartItem.put("subtotal", subtotal);
                cart.add(cartItem);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item added to cart");
            response.put("cartSize", cart.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to add item: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Update quantity of a product in cart
     */
    @PutMapping("/api/cart/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCartQuantity(@RequestBody Map<String, Object> data) {
        try {
            Long userId = Long.valueOf(String.valueOf(data.get("userId")));
            Long productId = Long.valueOf(String.valueOf(data.get("productId")));
            int newQty = Integer.parseInt(String.valueOf(data.get("qty")));

            if (userCarts.containsKey(userId)) {
                for (Map<String, Object> item : userCarts.get(userId)) {
                    if (productId.equals(Long.valueOf(String.valueOf(item.get("productId"))))) {
                        item.put("qty", newQty);

                        // Recalculate subtotal
                        double price = Double.parseDouble(String.valueOf(item.get("price")));
                        item.put("subtotal", price * newQty);
                        break;
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Quantity updated");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get all products in user's cart
     */
    @GetMapping("/api/cart/user/{userId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getCartByUserId(@PathVariable Long userId) {
        List<Map<String, Object>> cart = userCarts.getOrDefault(userId, new ArrayList<>());
        return ResponseEntity.ok(cart);
    }

    /**
     * Remove a specific product from user's cart
     */
    @DeleteMapping("/api/cart/remove")
    @ResponseBody
    public ResponseEntity<Map<String, String>> removeFromCart(@RequestBody Map<String, Object> data) {
        try {
            Long userId = Long.valueOf(String.valueOf(data.get("userId")));
            Long productId = Long.valueOf(String.valueOf(data.get("productId")));

            if (userCarts.containsKey(userId)) {
                userCarts.get(userId).removeIf(item ->
                        productId.equals(Long.valueOf(String.valueOf(item.get("productId")))));
            }

            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", "Item removed from cart");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("success", "false");
            response.put("message", "Failed to remove item: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Clear all products from user's cart
     */
    @DeleteMapping("/api/cart/clear/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> clearCart(@PathVariable Long userId) {
        userCarts.remove(userId);

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Cart cleared successfully");
        return ResponseEntity.ok(response);
    }
}
