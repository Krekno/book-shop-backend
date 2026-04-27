package com.krekno.bookshopbackend.controller;

import com.krekno.bookshopbackend.config.WebSecurityConfig;
import com.krekno.bookshopbackend.entity.CartItem;
import com.krekno.bookshopbackend.entity.Book;
import com.krekno.bookshopbackend.service.CartService;
import com.krekno.bookshopbackend.service.JwtUtils;
import com.krekno.bookshopbackend.service.UserDetailsImpl;
import com.krekno.bookshopbackend.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.BeforeEach;
import com.krekno.bookshopbackend.entity.User;
import org.springframework.security.test.context.support.TestExecutionEvent;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import(WebSecurityConfig.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole(com.krekno.bookshopbackend.enums.Role.ROLE_USER);
        
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
    }

    @Test
    @WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getCart_Success() throws Exception {
        CartItem item = new CartItem();
        Book book = new Book();
        book.setId(1L);
        book.setName("My Book");
        item.setBook(book);
        item.setQuantity(2);

        when(cartService.getCart(any())).thenReturn(List.of(item));

        mockMvc.perform(get("/api/cart/getCart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].book.name").value("My Book"))
                .andExpect(jsonPath("$[0].quantity").value(2));
    }

    @Test
    @WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void addToCart_Success() throws Exception {
        doNothing().when(cartService).addToCart(any(), eq(1L));

        mockMvc.perform(post("/api/cart/add/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Added to cart"));
    }

    @Test
    @WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteCartItem_Success() throws Exception {
        doNothing().when(cartService).removeFromCart(any(), eq(1L));

        mockMvc.perform(put("/api/cart/remove/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Removed from cart"));
    }
}
