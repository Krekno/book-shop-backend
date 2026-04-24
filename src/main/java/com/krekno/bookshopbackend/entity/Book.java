package com.krekno.bookshopbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    private String name;
    private String author;
    private String genre;
    private String description;
    private String image;
    private String isbn;
    private String publisher;
    private String language;
    private Date year;
    private int pages;
    private float price;
    private int stock;
}
