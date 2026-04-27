package com.krekno.bookshopbackend.controller;

import com.krekno.bookshopbackend.config.WebSecurityConfig;
import com.krekno.bookshopbackend.entity.Order;
import com.krekno.bookshopbackend.enums.OrderStatus;
import com.krekno.bookshopbackend.service.JwtUtils;
import com.krekno.bookshopbackend.service.OrderService;
import com.krekno.bookshopbackend.service.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(WebSecurityConfig.class)
@RequiredArgsConstructor
public class OrderControllerTest {

    private final MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @WithMockUser(username = "testuser")
    void placeOrder_Success() throws Exception {
        doNothing().when(orderService).placeOrder("testuser");

        mockMvc.perform(post("/api/order/place").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getOrders_Success() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderService.getOrdersByUserEmail("testuser")).thenReturn(List.of(order));

        mockMvc.perform(get("/api/order/getOrders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOrders_Admin_Success() throws Exception {
        Order order = new Order();
        order.setId(2L);
        order.setStatus(OrderStatus.APPROVED);

        when(orderService.getAllOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/order/admin/getOrders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllOrders_User_Forbidden() throws Exception {
        mockMvc.perform(get("/api/order/admin/getOrders"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void approveOrder_Success() throws Exception {
        doNothing().when(orderService).updateOrderStatus(1L, OrderStatus.APPROVED);

        mockMvc.perform(patch("/api/order/admin/approve/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelOrder_Success() throws Exception {
        doNothing().when(orderService).updateOrderStatus(1L, OrderStatus.CANCELLED);

        mockMvc.perform(patch("/api/order/admin/cancel/1").with(csrf()))
                .andExpect(status().isOk());
    }
}
