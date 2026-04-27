package com.krekno.bookshopbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krekno.bookshopbackend.config.WebSecurityConfig;
import com.krekno.bookshopbackend.dto.LoginRequest;
import com.krekno.bookshopbackend.dto.SignupRequest;
import com.krekno.bookshopbackend.service.AuthService;
import com.krekno.bookshopbackend.service.JwtUtils;
import com.krekno.bookshopbackend.service.UserDetailsImpl;
import com.krekno.bookshopbackend.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(AuthController.class)
@Import(WebSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    // Mocked for WebSecurityConfig and AuthTokenFilter
    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void registerUser_Success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("test@test.com");
        request.setPassword("password123");

        doNothing().when(authService).registerUser(any(SignupRequest.class));

        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    void registerUser_ValidationError() throws Exception {
        SignupRequest request = new SignupRequest();
        // Missing username, email, password

        mockMvc.perform(post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void authenticateUser_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, "jwt=test-cookie");

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(headers);

        mockMvc.perform(post("/api/auth/signin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(content().string("User signed in successfully!"));
    }

    @Test
    @WithMockUser
    void updateUser_Success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("newuser");
        request.setEmail("new@test.com");
        request.setPassword("newpass123");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, "jwt=updated-cookie");

        // Note: WithMockUser provides a UserDetails, but AuthController expects UserDetailsImpl.
        // Spring Security test context needs special handling for custom UserDetails, 
        // but for MockMvc matching, we can just mock the service.
        when(authService.updateUser(any(), any())).thenReturn(headers);

        mockMvc.perform(patch("/api/auth/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully!"));
    }

    @Test
    void updateUser_Unauthorized() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("newuser");
        request.setEmail("new@test.com");
        request.setPassword("newpass123");

        mockMvc.perform(patch("/api/auth/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Or 401 depending on config
    }

    @Test
    void logoutUser_Success() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, "jwt=clean-cookie; Max-Age=0");

        when(authService.logoutUser()).thenReturn(headers);

        mockMvc.perform(post("/api/auth/signout").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("You've been signed out!"));
    }
}
