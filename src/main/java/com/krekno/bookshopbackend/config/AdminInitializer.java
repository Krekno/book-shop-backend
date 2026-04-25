package com.krekno.bookshopbackend.config;

import com.krekno.bookshopbackend.entity.User;
import com.krekno.bookshopbackend.enums.Role;
import com.krekno.bookshopbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    @Bean
    public CommandLineRunner createAdmin(UserRepository userRepository,
                                         PasswordEncoder passwordEncoder) {
        return args -> {

            String username = "admin";
            String email = "admin@bookshop.com";
            String password = "admin123";

            if (userRepository.findByUsername(username).isEmpty()) {

                User admin = new User();
                admin.setUsername(username);
                admin.setEmail(email);
                admin.setPassword(passwordEncoder.encode(password));
                admin.setRole(Role.ROLE_ADMIN);

                userRepository.save(admin);

                System.out.println("admin user created");
            } else {
                System.out.println("admin already exists");
            }
        };
    }
}