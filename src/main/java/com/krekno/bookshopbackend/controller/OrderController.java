package com.krekno.bookshopbackend.controller;

import com.krekno.bookshopbackend.entity.*;
import com.krekno.bookshopbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cart.getItems();

        if(cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("cart empty");
        }

        Order order = new Order();

        float totalPrice = (float) cart.getItems()
                .stream()
                .mapToDouble(item -> item.getBook().getPrice())
                .sum();

        order.setUser(user);
        order.setOrderItems(cartItems
                .stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setPrice(cartItem.getBook().getPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setOrder(order);
                    orderItemRepository.save(orderItem);
                    return orderItem;
                }).toList());
        order.setDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setPrice(totalPrice);
        orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return ResponseEntity.ok().build();
    }

    @GetMapping("getOrders")
    public ResponseEntity<?> getOrders(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Order> orders = orderRepository.findAllByUser(user);

        if (orders.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/admin/getOrders")
    public ResponseEntity<?> getAllOrders() {

        List<Order> orders = orderRepository.findAll();

        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/admin/approve{id}")
    public ResponseEntity<?> approveOrder(@PathVariable Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("order not found"));

        order.setStatus("APPROVED");
        orderRepository.save(order);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/admin/cancel{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("order not found"));

        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return ResponseEntity.ok().build();
    }
}
