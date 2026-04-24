package com.krekno.bookshopbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Book book;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private int quantity;
}
