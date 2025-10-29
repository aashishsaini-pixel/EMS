package com.Ems.EmployeeManagmentSystem.dto.Response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class JwtResponse {
    private String token;
    private String tokenType; // usually "Bearer"
    private String email;
}
