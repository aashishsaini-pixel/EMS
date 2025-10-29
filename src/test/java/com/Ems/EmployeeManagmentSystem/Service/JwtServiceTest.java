package com.Ems.EmployeeManagmentSystem.Service;

import com.Ems.EmployeeManagmentSystem.Exceptions.AuthenticationFailedException;
import com.Ems.EmployeeManagmentSystem.Exceptions.SecretKeyNotFoundException;
import com.Ems.EmployeeManagmentSystem.Exceptions.TokenGenerationException;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.secretKeyString = Base64.getEncoder()
                .encodeToString("supersecretkey125465678ugfhjbkn34567890".getBytes());
        jwtService.expiration = 1000L * 60 * 60;
        jwtService.refreshExpiration = 1000L * 60 * 60 * 24;
        jwtService.issuer = "EMS";
        jwtService.init();

        testUser = new User("testuser", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testGenerateTokenAndExtractClaims() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
        assertEquals("ROLE_USER", jwtService.extractRole(token));
        assertTrue(jwtService.extractAuthorities(token).contains("ROLE_USER"));
    }

    @Test
    void testGenerateRefreshToken() {
        String token = jwtService.generateRefreshToken(testUser);
        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    void testValidateToken_Success() {
        String token = jwtService.generateToken(testUser);
        assertTrue(jwtService.validateToken(token, testUser));
    }

    @Test
    void testValidateToken_WrongUsernameThrows() {
        String token = jwtService.generateToken(testUser);
        UserDetails otherUser = new User("other", "password", Collections.emptyList());

        AuthenticationFailedException ex =
                assertThrows(AuthenticationFailedException.class,
                        () -> jwtService.validateToken(token, otherUser));
        assertEquals("JWT_INVALID_USERNAME", ex.getErrorCode());
    }

    @Test
    void testExpiredTokenThrows() throws InterruptedException {
        jwtService.expiration = 1L;
        String token = jwtService.generateToken(testUser);
        Thread.sleep(10);

        AuthenticationFailedException ex =
                assertThrows(AuthenticationFailedException.class,
                        () -> jwtService.validateToken(token, testUser));
        assertEquals("JWT_EXPIRED", ex.getErrorCode());
    }

    @Test
    void testExtractTokenFromHeader() {
        String token = jwtService.generateToken(testUser);
        String header = "Bearer " + token;

        Optional<String> extracted = jwtService.extractTokenFromHeader(header);
        assertTrue(extracted.isPresent());
        assertEquals(token, extracted.get());
    }

    @Test
    void testExtractTokenFromInvalidHeaderReturnsEmpty() {
        assertTrue(jwtService.extractTokenFromHeader("InvalidHeader").isEmpty());
    }

    @Test
    void testGetRemainingTimeUntilExpiration() {
        String token = jwtService.generateToken(testUser);
        long remaining = jwtService.getRemainingTimeUntilExpiration(token);
        assertTrue(remaining > 0);
    }

    @Test
    void testCanTokenBeRefreshed() {
        String token = jwtService.generateToken(testUser);
        assertTrue(jwtService.canTokenBeRefreshed(token));
    }

    @Test
    void testInitWithoutSecretThrows() {
        JwtService badService = new JwtService();
        badService.secretKeyString = null;
        assertThrows(SecretKeyNotFoundException.class, badService::init);
    }

    @Test
    void testMalformedTokenThrows() {
        String malformedToken = "abc.def.ghi";
        AuthenticationFailedException ex =
                assertThrows(AuthenticationFailedException.class,
                        () -> jwtService.extractUsername(malformedToken));
        assertEquals("JWT_MALFORMED", ex.getErrorCode());
    }

    @Test
    void testGenerateTokenThrowsTokenGenerationException() {
        try (MockedStatic<Jwts> mockedJwts = mockStatic(Jwts.class)) {
            JwtBuilder builderMock = mock(JwtBuilder.class);

            when(Jwts.builder()).thenReturn(builderMock);
            when(builderMock.setHeaderParam(anyString(), any())).thenReturn(builderMock);
            when(builderMock.setClaims(anyMap())).thenReturn(builderMock);
            when(builderMock.setSubject(anyString())).thenReturn(builderMock);
            when(builderMock.setIssuer(anyString())).thenReturn(builderMock);
            when(builderMock.setIssuedAt(any())).thenReturn(builderMock);
            when(builderMock.setExpiration(any())).thenReturn(builderMock);
            when(builderMock.setId(anyString())).thenReturn(builderMock);
            when(builderMock.signWith((SignatureAlgorithm) any(), (byte[]) any())).thenReturn(builderMock);
            when(builderMock.compact()).thenThrow(new RuntimeException("sign error"));

            assertThrows(TokenGenerationException.class, () -> jwtService.generateToken(testUser));
        }
    }
    @Test
    void testExtractAllClaims_EmptyTokenThrows() {
        AuthenticationFailedException ex =
                assertThrows(AuthenticationFailedException.class,
                        () -> jwtService.extractUsername(""));
        assertEquals("JWT_EMPTY", ex.getErrorCode());
    }

    @Test
    void testIsTokenExpired_InvalidTokenThrowsAuthenticationFailedException() {
        AuthenticationFailedException ex =
                assertThrows(AuthenticationFailedException.class,
                        () -> jwtService.isTokenExpired("abc.def.ghi"));
        assertEquals("JWT_MALFORMED", ex.getErrorCode());
    }

    @Test
    void testCanTokenBeRefreshed_InvalidTokenThrowsAuthenticationFailedException() {
        AuthenticationFailedException ex =
                assertThrows(AuthenticationFailedException.class,
                        () -> jwtService.canTokenBeRefreshed("abc.def.ghi"));
        assertEquals("JWT_MALFORMED", ex.getErrorCode());
    }


//    @Test
//    void testExtractAllClaims_InvalidTokenThrows() {
//        try (MockedStatic<Jwts> mockedJwts = mockStatic(Jwts.class)) {
//            JwtParserBuilder builderMock = mock(JwtParserBuilder.class);
//            JwtParser parserMock = mock(JwtParser.class);
//
//            mockedJwts.when(() -> Jwts.parserBuilder()).thenReturn(builderMock);
//
//            when(builderMock.setSigningKey((byte[]) any())).thenReturn(builderMock);
//            when(builderMock.requireIssuer(anyString())).thenReturn(builderMock);
//            when(builderMock.build()).thenReturn(parserMock);
//
//            when(parserMock.parseClaimsJws(anyString())).thenThrow(new IllegalArgumentException("Invalid"));
//
//            AuthenticationFailedException ex =
//                    assertThrows(AuthenticationFailedException.class,
//                            () -> jwtService.extractUsername("invalid.token"));
//            assertEquals("JWT_INVALID", ex.getErrorCode());
//        }


}