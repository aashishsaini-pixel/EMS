package com.Ems.EmployeeManagmentSystem.dto.response;

import com.Ems.EmployeeManagmentSystem.dto.Response.JwtResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtResponseTest {

    @Test
    @DisplayName("Should create JwtResponse using constructor")
    void shouldCreateJwtResponseUsingConstructor() {
        JwtResponse jwtResponse = new JwtResponse("jwt-token", "Bearer", "user@example.com");

        assertEquals("jwt-token", jwtResponse.getToken());
        assertEquals("Bearer", jwtResponse.getTokenType());
        assertEquals("user@example.com", jwtResponse.getEmail());
    }

    @Test
    @DisplayName("Should create JwtResponse using builder")
    void shouldCreateJwtResponseUsingBuilder() {
        JwtResponse jwtResponse = JwtResponse.builder()
                .token("jwt-builder-token")
                .tokenType("Bearer")
                .email("builder@example.com")
                .build();

        assertEquals("jwt-builder-token", jwtResponse.getToken());
        assertEquals("Bearer", jwtResponse.getTokenType());
        assertEquals("builder@example.com", jwtResponse.getEmail());
    }

    @Test
    @DisplayName("Should set and get JwtResponse fields")
    void shouldSetAndGetJwtResponseFields() {
        JwtResponse jwtResponse = JwtResponse.builder().build();

        jwtResponse.setToken("set-token");
        jwtResponse.setTokenType("Bearer");
        jwtResponse.setEmail("set@example.com");

        assertEquals("set-token", jwtResponse.getToken());
        assertEquals("Bearer", jwtResponse.getTokenType());
        assertEquals("set@example.com", jwtResponse.getEmail());
    }
}
