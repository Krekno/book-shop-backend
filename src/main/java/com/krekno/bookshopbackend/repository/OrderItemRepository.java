package com.krekno.bookshopbackend.repository;

import com.krekno.bookshopbackend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
}
