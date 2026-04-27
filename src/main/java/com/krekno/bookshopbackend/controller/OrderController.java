package com.krekno.bookshopbackend.controller;

import com.krekno.bookshopbackend.entity.Order;
import com.krekno.bookshopbackend.enums.OrderStatus;
import com.krekno.bookshopbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@AuthenticationPrincipal UserDetails userDetails) {
        orderService.placeOrder(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("getOrders")
    public ResponseEntity<?> getOrders(@AuthenticationPrincipal UserDetails userDetails) {
        List<Order> orders = orderService.getOrdersByUserEmail(userDetails.getUsername());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/admin/getOrders")
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/admin/approve/{id}")
    public ResponseEntity<?> approveOrder(@PathVariable Long id) {
        orderService.updateOrderStatus(id, OrderStatus.APPROVED);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/admin/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        orderService.updateOrderStatus(id, OrderStatus.CANCELLED);
        return ResponseEntity.ok().build();
    }
}
