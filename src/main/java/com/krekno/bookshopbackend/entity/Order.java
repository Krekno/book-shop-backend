package com.krekno.bookshopbackend.entity;

import com.krekno.bookshopbackend.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private float price;
}
