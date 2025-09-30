package com.Ems.EmployeeManagmentSystem.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

import static java.awt.SystemColor.info;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Aashish Saini",
                        email = "aashishsaini1226@gmail.com",
                        url = "hsldfkj.com"
                ),
                description = "Open Api Documentation for spring security",
                title = "OpenApi Specification - Aashish",
                version = "1.0",
                license = @License(
                        name = "First License",
                        url = "sdlkfjd.com"
                ),
                termsOfService = "Terms of Service"
        ),
        servers = {
                @Server(
                        description = "Local Environment",
                        url = "http://localhost:8081/api/v1"
                ),
                @Server(
                        description = "Test Environment",
                        url = "http:/env:8081"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth", // this must match @SecurityRequirement name
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
