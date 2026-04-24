package com.krekno.bookshopbackend.repository;

import com.krekno.bookshopbackend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
