package com.Ems.EmployeeManagmentSystem.Filter;

import com.Ems.EmployeeManagmentSystem.dto.Response.CommonResponse;
import com.Ems.EmployeeManagmentSystem.Exceptions.AuthenticationFailedException;
import com.Ems.EmployeeManagmentSystem.Service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.filter.enable-detailed-logging:false}")
    private boolean enableDetailedLogging;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

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

        }
        catch (AuthenticationFailedException e) {
            log.warn("Authentication failed [{}] {}: {}", requestId, requestURI, e.getMessage());
            writeAuthError(response, e);
        } catch (Exception e) {
            log.error("Unexpected error [{}] {}", requestId, requestURI, e);
            writeAuthError(response, new AuthenticationFailedException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR"));
        }
    }

    private void processAuthentication(String token, HttpServletRequest request, String requestId) {
        String username = jwtService.extractUsername(token);

        if (!StringUtils.hasText(username)) {
            log.warn("Empty username in token [{}]", requestId);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.validateToken(token, userDetails)) {
            log.warn("Token validation failed for user '{}' [{}]", username, requestId);
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);

        if (enableDetailedLogging) {
            log.debug("Authenticated user '{}' [{}]", username, requestId);
            log.debug("Authorities: {}", userDetails.getAuthorities());
        }

//        request.setAttribute("jwt.username", username);
//        request.setAttribute("jwt.authorities", userDetails.getAuthorities());
//        request.setAttribute("jwt.token", token);
    }

    private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (enableDetailedLogging) {
            log.debug("Extracting token from Authorization header");
        }
        return jwtService.extractTokenFromHeader(authHeader);
    }

    private void writeAuthError(HttpServletResponse response, AuthenticationFailedException e) throws IOException {
        response.setStatus(e.getStatus().value());
        response.setContentType("application/json");

        CommonResponse<?> errorResponse = CommonResponse.failed(false , e.getMessage(), e.getErrorCode());

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }

    private String getOrGenerateRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        return StringUtils.hasText(requestId)
                ? requestId
                : UUID.randomUUID().toString().substring(0, 8);
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
