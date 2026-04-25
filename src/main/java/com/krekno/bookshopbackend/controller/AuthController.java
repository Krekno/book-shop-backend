package com.krekno.bookshopbackend.controller;

import com.krekno.bookshopbackend.dto.LoginRequest;
import com.krekno.bookshopbackend.dto.SignupRequest;
import com.krekno.bookshopbackend.entity.RefreshToken;
import com.krekno.bookshopbackend.entity.User;
import com.krekno.bookshopbackend.enums.Role;
import com.krekno.bookshopbackend.repository.UserRepository;
import com.krekno.bookshopbackend.service.JwtUtils;
import com.krekno.bookshopbackend.service.RefreshTokenService;
import com.krekno.bookshopbackend.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use!");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PatchMapping("/update")
    ResponseEntity<?> updateUser(@RequestBody SignupRequest signupRequest, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

         if(signupRequest.getUsername() != null && !signupRequest.getUsername().isEmpty())
             user.setUsername(signupRequest.getUsername());
         if(signupRequest.getEmail() != null && !signupRequest.getEmail().isEmpty())
             user.setEmail(signupRequest.getEmail());
         if(signupRequest.getPassword() != null && !signupRequest.getPassword().isEmpty())
             user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
         userRepository.save(user);

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails.getEmail());
        refreshTokenService.deleteByUserId(userDetails.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body("User updated successfully!");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal(); // Custom UserDetails

        assert userDetails != null;
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails.getEmail());

        // Delete the old refresh token and create a new one
        refreshTokenService.deleteByUserId(userDetails.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body("User signed in successfully!");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        if ((refreshToken != null) && (!refreshToken.isEmpty())) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user.getEmail());
                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body("Token is refreshed successfully!");
                    })
                    .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
        }
        return ResponseEntity.badRequest().body("Refresh Token is empty!");
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        Object principle = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert principle != null;
        if (!   Objects.equals(principle.toString(), "anonymousUser")) {
            Long userId = ((UserDetailsImpl) principle).getId();
            refreshTokenService.deleteByUserId(userId);
        }

        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body("You've been signed out!");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        Map<String, Object> res = new HashMap<>();
        res.put("username", user.getUsername());
        res.put("roles", userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList());
        return ResponseEntity.ok(res);
    }
}
