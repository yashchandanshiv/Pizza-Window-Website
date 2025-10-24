package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "Home";
    }

    @GetMapping("/home")
    public String home() {
        return "Home";
    }

    @GetMapping("/login")
    public String login() {
        return "Login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/products")
    public String products() {
        return "Products";
    }

    @GetMapping("/about")
    public String about() {
        return "About";
    }

    @GetMapping("/buyproduct")
    public String buyProduct() {
        return "BuyProduct";
    }

    @GetMapping("/payment")
    public String payment() {
        return "payment";
    }

    @GetMapping("/order-success")
    public String orderSuccess() {
        return "Order_success";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Admin_Page";
    }

    @GetMapping("/locate")
    public String locate() {
        return "Locate_us";
    }

    @GetMapping("/exception")
    public String exception() {
        return "exception";
    }
}
