package com.krekno.bookshopbackend.repository;

import com.krekno.bookshopbackend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndBookId(Long id, Long bookId);
}
