package com.krekno.bookshopbackend.controller;

import com.krekno.bookshopbackend.dto.LoginRequest;
import com.krekno.bookshopbackend.dto.SignupRequest;
import com.krekno.bookshopbackend.service.AuthService;
import com.krekno.bookshopbackend.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        authService.registerUser(signupRequest);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PatchMapping("/update")
    ResponseEntity<?> updateUser(@RequestBody SignupRequest signupRequest, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        HttpHeaders headers = authService.updateUser(signupRequest, userDetails);
        return ResponseEntity.ok()
                .headers(headers)
                .body("User updated successfully!");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        HttpHeaders headers = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok()
                .headers(headers)
                .body("User signed in successfully!");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        HttpHeaders headers = authService.refreshToken(request);
        return ResponseEntity.ok()
                .headers(headers)
                .body("Token is refreshed successfully!");
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        HttpHeaders headers = authService.logoutUser();
        return ResponseEntity.ok()
                .headers(headers)
                .body("You've been signed out!");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        Map<String, Object> res = authService.getCurrentUser(userDetails);
        return ResponseEntity.ok(res);
    }
}
