package com.krekno.bookshopbackend.controller;

import com.krekno.bookshopbackend.dto.BookRequestDto;
import com.krekno.bookshopbackend.entity.Book;
import com.krekno.bookshopbackend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllBooks() {
        List<Book> books = bookService.getAllInStockBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/all/admin")
    public ResponseEntity<?> getAllBooksAdmin() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @PostMapping("/saveBook")
    public ResponseEntity<Book> saveBook(@RequestBody BookRequestDto dto) {
        Book book = bookService.saveBook(dto);
        return ResponseEntity.ok(book);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookRequestDto updatedBook) {
        Book book = bookService.updateBook(id, updatedBook);
        return ResponseEntity.ok("Book updated successfully!");
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully!");
    }
}
