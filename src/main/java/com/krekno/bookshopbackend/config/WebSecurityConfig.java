package com.krekno.bookshopbackend.config;

import com.krekno.bookshopbackend.enums.Role;
import com.krekno.bookshopbackend.security.AuthTokenFilter;
import com.krekno.bookshopbackend.service.JwtUtils;
import com.krekno.bookshopbackend.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.DispatcherType;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtUtils jwtUtils;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsServiceImpl);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                                // Admin-only endpoints
                                .requestMatchers("/api/order/admin/**").hasAuthority(Role.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.POST, "/api/books/**").hasAuthority(Role.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.PATCH, "/api/books/**").hasAuthority(Role.ROLE_ADMIN.name())
                                .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasAuthority(Role.ROLE_ADMIN.name())
                                .requestMatchers("/api/books/all/admin").hasAuthority(Role.ROLE_ADMIN.name())
                                // Public endpoints
                                .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                                // Auth endpoints
                                .requestMatchers("/api/auth/update").authenticated()
                                .requestMatchers("/api/auth/me").authenticated()
                                .requestMatchers("/api/auth/**").permitAll()
                                // Authenticated endpoints
                                .requestMatchers("/api/cart/**").authenticated()
                                .requestMatchers("/api/order/**").authenticated()
                                .anyRequest().authenticated()
                );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}