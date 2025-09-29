package com.Ems.EmployeeManagmentSystem.Exceptions;

import com.Ems.EmployeeManagmentSystem.DTO.Response.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,HttpServletResponse response,AccessDeniedException accessDeniedException) throws IOException {

        log.error("Custom Access Denied Handler");

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        CommonResponse<?> errorResponse = CommonResponse.error(
                "You do not have permission to access this resource",
                "ACCESS_DENIED"
        );

        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}
