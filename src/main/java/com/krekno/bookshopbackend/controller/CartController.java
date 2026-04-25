package com.krekno.bookshopbackend.controller;

import com.krekno.bookshopbackend.entity.Book;
import com.krekno.bookshopbackend.entity.Cart;
import com.krekno.bookshopbackend.entity.CartItem;
import com.krekno.bookshopbackend.entity.User;
import com.krekno.bookshopbackend.repository.BookRepository;
import com.krekno.bookshopbackend.repository.CartItemRepository;
import com.krekno.bookshopbackend.repository.CartRepository;
import com.krekno.bookshopbackend.repository.UserRepository;
import com.krekno.bookshopbackend.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;

    @GetMapping("/getCart")
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (cart == null) {
            return ResponseEntity.notFound().build();
        } else if (cart.getItems().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cart.getItems());
    }

    @PostMapping("/add{id}")
    public ResponseEntity<?> addToCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long bookId) {

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new UsernameNotFoundException("Book not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });

        List<CartItem> cartItems = cart.getItems();

        Optional<CartItem> existingItem = cartItems.stream()
                .filter(item -> item.getBook().equals(book))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setBook(book);
            newItem.setQuantity(1);

            cartItemRepository.save(newItem);
            cartItems.add(newItem);
        }

        cartRepository.save(cart);

        return ResponseEntity.ok("Added to cart");
    }

    @PutMapping("/remove{bookId}")
    public ResponseEntity<?> deleteCartItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long bookId) {

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            cartItemRepository.save(item);
        } else {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        }

        cartRepository.save(cart);

        return ResponseEntity.ok("Removed from cart");
    }
}