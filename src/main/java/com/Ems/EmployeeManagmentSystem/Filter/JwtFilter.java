package com.Ems.EmployeeManagmentSystem.Filter;

import com.Ems.EmployeeManagmentSystem.Exceptions.AuthenticationFailedException;
import com.Ems.EmployeeManagmentSystem.Service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${jwt.filter.excluded-paths}")
    private List<String> excludedPaths;

    @Value("${jwt.filter.enable-detailed-logging:false}")
    private boolean enableDetailedLogging;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {

        final String requestId = getOrGenerateRequestId(request);
        final String requestURI = request.getRequestURI();
        final String method = request.getMethod();

        if (enableDetailedLogging) {
            log.debug("Processing request [{}] {} {}", requestId, method, requestURI);
        }

        try {
            Optional<String> tokenOpt = extractTokenFromRequest(request);

            if (tokenOpt.isEmpty()) {
                if (enableDetailedLogging) {
                    log.debug("No JWT token found in request [{}]", requestId);
                }
                filterChain.doFilter(request, response);
                return;
            }

            String token = tokenOpt.get();

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                if (enableDetailedLogging) {
                    log.debug("User is not authenticated yet. Validating JWT token for request [{}]", requestId);
                }
                processAuthentication(token, request, requestId);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired for request [{}] {}: {}", requestId, requestURI, e.getMessage());
            throw new AuthenticationFailedException("Token expired", HttpStatus.UNAUTHORIZED, "JWT_EXPIRED");

        } catch (JwtException e) {
            log.warn("Invalid JWT token for request [{}] {}: {}", requestId, requestURI, e.getMessage());
            throw new AuthenticationFailedException("Invalid token", HttpStatus.UNAUTHORIZED, "JWT_INVALID");

        } catch (UsernameNotFoundException e) {
            log.warn("User not found for request [{}] {}: {}", requestId, requestURI, e.getMessage());
            throw new AuthenticationFailedException("User not found", HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND");

        } catch (Exception e) {
            log.error("Unexpected error processing JWT for request [{}] {}", requestId, requestURI, e);
            throw new AuthenticationFailedException("Authentication processing error", HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
        }
    }

    private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (enableDetailedLogging) {
            log.debug("Extracting token from Authorization header");
        }
        return jwtService.extractTokenFromHeader(authHeader);
    }

    private void processAuthentication(String token, HttpServletRequest request, String requestId) {
        String username = jwtService.extractUsername(token);

        if (!StringUtils.hasText(username)) {
            log.warn("JWT token contains empty username for request [{}]", requestId);
            throw new AuthenticationFailedException("Invalid token - no username", HttpStatus.UNAUTHORIZED, "JWT_INVALID_USERNAME");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.validateToken(token, userDetails)) {
            log.warn("JWT token validation failed for user '{}' in request [{}]", username, requestId);
            throw new AuthenticationFailedException("Token validation failed", HttpStatus.UNAUTHORIZED, "JWT_INVALID_SIGNATURE");
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);

        if (enableDetailedLogging) {
            log.debug("Authenticated user '{}' for request [{}]", username, requestId);
            log.debug("Authorities: {}", userDetails.getAuthorities());
        }

        // Optional: set as request attributes
        request.setAttribute("jwt.username", username);
        request.setAttribute("jwt.authorities", userDetails.getAuthorities());
        request.setAttribute("jwt.token", token);
    }

    private String getOrGenerateRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        return StringUtils.hasText(requestId)
                ? requestId
                : UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        boolean shouldSkip = excludedPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));

        if (shouldSkip) {
            if (enableDetailedLogging) {
                log.debug("Skipping JWT filter for {} {} (excluded path)", method, requestURI);
            }
            return true;
        }

        if ("OPTIONS".equalsIgnoreCase(method)) {
            if (enableDetailedLogging) {
                log.debug("Skipping JWT filter for OPTIONS request: {}", requestURI);
            }
            return true;
        }

        if (enableDetailedLogging) {
            log.debug("JWT filter will process {} {}", method, requestURI);
        }

        return false;
    }

    public static Optional<UserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return Optional.of((UserDetails) authentication.getPrincipal());
        }
        return Optional.empty();
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String);
    }
}
