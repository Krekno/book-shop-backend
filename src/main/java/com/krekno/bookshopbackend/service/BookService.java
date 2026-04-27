package com.krekno.bookshopbackend.service;

import com.krekno.bookshopbackend.dto.BookRequestDto;
import com.krekno.bookshopbackend.entity.Book;
import com.krekno.bookshopbackend.repository.BookRepository;
import com.krekno.bookshopbackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> getAllInStockBooks() {
        return bookRepository.findAll()
                .stream()
                .filter(book -> book.getStock() > 0)
                .toList();
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book saveBook(BookRequestDto dto) {
        Book book = new Book();
        book.setName(dto.getName());
        book.setAuthor(dto.getAuthor());
        book.setGenre(dto.getGenre());
        book.setDescription(dto.getDescription());
        book.setImage(dto.getImage());
        book.setIsbn(dto.getIsbn());
        book.setPublisher(dto.getPublisher());
        book.setLanguage(dto.getLanguage());
        book.setYear(dto.getYear());
        book.setPages(dto.getPages());
        book.setPrice(dto.getPrice());
        book.setStock(dto.getStock());
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, BookRequestDto updatedBook) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found!"));

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

        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
