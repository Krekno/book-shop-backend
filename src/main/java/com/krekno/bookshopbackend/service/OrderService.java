package com.krekno.bookshopbackend.service;

import com.krekno.bookshopbackend.entity.*;
import com.krekno.bookshopbackend.enums.OrderStatus;
import com.krekno.bookshopbackend.exception.ResourceNotFoundException;
import com.krekno.bookshopbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;

    @Transactional
    public void placeOrder(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        List<CartItem> cartItems = cart.getItems();

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();

        float totalPrice = (float) cart.getItems()
                .stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();

        order.setUser(user);
        order.setOrderItems(cartItems
                .stream()
                .map(cartItem -> {
                    // Decrement stock
                    Book book = cartItem.getBook();
                    int newStock = book.getStock() - cartItem.getQuantity();
                    if (newStock < 0) {
                        throw new IllegalStateException("Insufficient stock for book: " + book.getName());
                    }
                    book.setStock(newStock);
                    bookRepository.save(book);

                    OrderItem orderItem = new OrderItem();
                    orderItem.setPrice(cartItem.getBook().getPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setOrder(order);
                    orderItemRepository.save(orderItem);
                    return orderItem;
                }).toList());
        order.setDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPrice(totalPrice);
        orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public List<Order> getOrdersByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return orderRepository.findAllByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);
        orderRepository.save(order);
    }
}
