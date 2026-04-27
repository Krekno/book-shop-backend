package com.krekno.bookshopbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krekno.bookshopbackend.config.WebSecurityConfig;
import com.krekno.bookshopbackend.dto.BookRequestDto;
import com.krekno.bookshopbackend.entity.Book;
import com.krekno.bookshopbackend.service.BookService;
import com.krekno.bookshopbackend.service.JwtUtils;
import com.krekno.bookshopbackend.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(WebSecurityConfig.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void getAllBooks_Success() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setName("Test Book");

        when(bookService.getAllInStockBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Book"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBooksAdmin_Success() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setName("Admin Book");

        when(bookService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/all/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Admin Book"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllBooksAdmin_ForbiddenForUser() throws Exception {
        mockMvc.perform(get("/api/books/all/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveBook_Success() throws Exception {
        BookRequestDto dto = new BookRequestDto();
        dto.setName("New Book");
        dto.setAuthor("Author");
        dto.setPages(100);
        dto.setPrice(10.5f);
        dto.setStock(5);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setName("New Book");

        when(bookService.saveBook(any(BookRequestDto.class))).thenReturn(savedBook);

        mockMvc.perform(post("/api/books/saveBook")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Book"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveBook_ValidationError() throws Exception {
        BookRequestDto dto = new BookRequestDto();
        dto.setName(""); // Invalid

        mockMvc.perform(post("/api/books/saveBook")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBook_Success() throws Exception {
        BookRequestDto dto = new BookRequestDto();
        dto.setName("Updated Book");
        dto.setAuthor("Author");
        dto.setPages(100);
        dto.setPrice(10.5f);
        dto.setStock(5);

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setName("Updated Book");

        when(bookService.updateBook(eq(1L), any(BookRequestDto.class))).thenReturn(updatedBook);

        mockMvc.perform(patch("/api/books/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Book updated successfully!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_Success() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted successfully!"));
    }
}
