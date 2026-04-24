package com.krekno.bookshopbackend.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.krekno.bookshopbackend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;


public class UserDetailsImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String username;
    private final String email;

    // Prevents the password from being serialized if you ever return this object in a JSON response
    @JsonIgnore
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String password, String email,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // A static factory method to easily build this object from your database User entity
    public static UserDetailsImpl build(User user) {
        // NOTE: If you add roles to your User entity later (e.g., ROLE_USER, ROLE_ADMIN),
        // you would map those roles into GrantedAuthorities here.
        // Since the previous User entity didn't include roles, we are passing an empty list.
        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                authorities);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
