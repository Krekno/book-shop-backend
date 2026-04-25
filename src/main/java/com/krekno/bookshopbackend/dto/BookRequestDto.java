package com.krekno.bookshopbackend.dto;

import lombok.Data;
import java.util.Date;

@Data
public class BookRequestDto {

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