package com.krekno.bookshopbackend.controller;

import com.krekno.bookshopbackend.entity.CartItem;
import com.krekno.bookshopbackend.service.CartService;
import com.krekno.bookshopbackend.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/getCart")
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<CartItem> items = cartService.getCart(userDetails.getId());

        if (items == null || items.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(items);
    }

    @PostMapping("/add/{bookId}")
    public ResponseEntity<?> addToCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long bookId) {

        cartService.addToCart(userDetails.getId(), bookId);
        return ResponseEntity.ok("Added to cart");
    }

    @PutMapping("/remove/{bookId}")
    public ResponseEntity<?> deleteCartItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long bookId) {

        cartService.removeFromCart(userDetails.getId(), bookId);
        return ResponseEntity.ok("Removed from cart");
    }
}