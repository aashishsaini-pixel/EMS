package com.Ems.EmployeeManagmentSystem.Service;

import com.Ems.EmployeeManagmentSystem.Exceptions.SecretKeyNotFoundException;
import com.Ems.EmployeeManagmentSystem.Exceptions.TokenExpiredException;
import com.Ems.EmployeeManagmentSystem.Exceptions.TokenGenerationException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    private static final String TOKEN_TYPE = "JWT";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.issuer:EMS}")
    private String issuer;

    @Value("${jwt.expiration}") // Default 1 hour in milliseconds
    private Long expiration;

    @Value("${jwt.refresh-expiration}") // Default 7 days in milliseconds
    private Long refreshExpiration;

    private Key secretKey;

    @PostConstruct
    public void init() {
        log.info("JWT expiration time from config = {}", expiration);
        if (!StringUtils.hasText(secretKeyString)) {
            throw new SecretKeyNotFoundException("JWT secret key cannot be null or empty");
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(secretKeyString);
        } catch (IllegalArgumentException e) {
            keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        }

        if (keyBytes.length < 32) { // 256 bits
            log.warn("JWT secret key is shorter than recommended 256 bits");
        }

        secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
        log.info("JWT Service initialized successfully");
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, expiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, refreshExpiration);
    }

    private String generateToken(UserDetails userDetails, Long tokenExpiration) {
        if (!StringUtils.hasText(userDetails.getUsername())) {
            throw new IllegalArgumentException("UserDetails must have a non-empty username");
        }

        Instant now = Instant.now();
        Instant expiryDate = now.plus(tokenExpiration, ChronoUnit.MILLIS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userDetails.getUsername());
        claims.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        String primaryRole = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");
        claims.put("role", primaryRole);

        try {
            return Jwts.builder()
                    .setHeaderParam("typ", TOKEN_TYPE)
                    .setClaims(claims)
                    .setSubject(userDetails.getUsername())
                    .setIssuer(issuer)
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expiryDate))
                    .setId(UUID.randomUUID().toString())
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token for user: {}", userDetails.getUsername(), e);
            throw new TokenGenerationException("Failed to generate JWT token: " + e.getMessage());
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    @SuppressWarnings("unchecked")
    public List<String> extractAuthorities(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get("authorities"));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            log.debug("Token is expired: {}", e.getMessage());
            return true;
        } catch (Exception e) {
            log.warn("Error checking token expiration", e);
            return true;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .requireIssuer(issuer)
                    .setAllowedClockSkewSeconds(60) // 1 minute clock skew
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            if (username == null || !username.equals(userDetails.getUsername())) {
                return false;
            }

            if (isTokenExpired(token)) {
                throw new TokenExpiredException("Token has expired while validating");
            }

            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            throw new TokenExpiredException("Token expired while validating");
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid token: {}", e.getMessage());
        }
        return false;
    }

    public Optional<String> extractTokenFromHeader(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return Optional.of(authorizationHeader.substring(TOKEN_PREFIX.length()));
        }
        return Optional.empty();
    }

    public long getRemainingTimeUntilExpiration(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            log.warn("Error calculating remaining time for token", e);
            return 0;
        }
    }

    public boolean canTokenBeRefreshed(String token) {
        try {
            final Date expiration = extractExpiration(token);
            final long gracePeriod = 24 * 60 * 60 * 1000; // 24 hours
            return expiration.getTime() + gracePeriod > System.currentTimeMillis();
        } catch (Exception e) {
            log.debug("Cannot refresh token due to error", e);
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.debug("JWT token is expired: {}", e.getMessage());
            throw new TokenExpiredException(e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            throw new JwtException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed: {}", e.getMessage());
            throw new JwtException("Malformed JWT token", e);
        } catch (SecurityException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
            throw new JwtException("JWT signature validation failed", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is invalid: {}", e.getMessage());
            throw new JwtException("Invalid JWT token", e);
        }
    }
}
