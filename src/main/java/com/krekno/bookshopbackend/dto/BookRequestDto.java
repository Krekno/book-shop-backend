package com.krekno.bookshopbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.Date;

@Data
public class BookRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Author is required")
    private String author;

    private String genre;
    private String description;
    private String image;
    private String isbn;
    private String publisher;
    private String language;
    private Date year;

    @Positive(message = "Pages must be a positive number")
    private int pages;

    @Positive(message = "Price must be a positive number")
    private float price;

    @PositiveOrZero(message = "Stock cannot be negative")
    private int stock;
}