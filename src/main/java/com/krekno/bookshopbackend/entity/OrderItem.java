package com.krekno.bookshopbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Book book;

    @ManyToOne
    private Order order;

    private int quantity;

    private float price;
}
