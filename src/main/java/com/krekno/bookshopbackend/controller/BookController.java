package com.krekno.bookshopbackend.controller;

import com.krekno.bookshopbackend.entity.Book;
import com.krekno.bookshopbackend.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllBooks() {

        List<Book> books = bookRepository.findAll();
        books = books.stream().filter(book -> book.getStock() > 0).toList();

        return ResponseEntity.ok(books);
    }

    @GetMapping("/all/admin")
    public ResponseEntity<?> getAllBooksAdmin() {

        List<Book> books = bookRepository.findAll();
        return ResponseEntity.ok(books);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {

        return bookRepository.findById(id)
                .map(book -> {

                    if (updatedBook.getName() != null)
                        book.setName(updatedBook.getName());

                    if (updatedBook.getAuthor() != null)
                        book.setAuthor(updatedBook.getAuthor());

                    if (updatedBook.getGenre() != null)
                        book.setGenre(updatedBook.getGenre());

                    if (updatedBook.getDescription() != null)
                        book.setDescription(updatedBook.getDescription());

                    if (updatedBook.getImage() != null)
                        book.setImage(updatedBook.getImage());

                    if (updatedBook.getIsbn() != null)
                        book.setIsbn(updatedBook.getIsbn());

                    if (updatedBook.getPublisher() != null)
                        book.setPublisher(updatedBook.getPublisher());

                    if (updatedBook.getLanguage() != null)
                        book.setLanguage(updatedBook.getLanguage());

                    if (updatedBook.getYear() != null)
                        book.setYear(updatedBook.getYear());

                    if (updatedBook.getPages() > 0)
                        book.setPages(updatedBook.getPages());

                    if (updatedBook.getPrice() > 0)
                        book.setPrice(updatedBook.getPrice());

                    if (updatedBook.getStock() >= 0)
                        book.setStock(updatedBook.getStock());

                    bookRepository.save(book);

                    return ResponseEntity.ok("Book updated successfully!");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Book not found!"));
    }

    @PutMapping
    public ResponseEntity<?> deleteBook(@RequestBody Long id) {

        bookRepository.deleteById(id);
        return ResponseEntity.ok("Book deleted successfully!");
    }
}
