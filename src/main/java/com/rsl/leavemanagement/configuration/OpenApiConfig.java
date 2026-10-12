package com.rsl.leavemanagement.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class defining OpenAPI v3 metadata for Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI leaveManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Leave Management REST API")
                        .description("Production-ready HR Leave Management System developed with Spring Boot 3. " +
                                "Demonstrates layered architecture, in-memory thread safety, Jakarta Bean Validation, " +
                                "and global exception handling.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sanket Ahire")
                                .email("sanket.ahire@rajasoftwarelabs.com")
                                .url("https://github.com/rsllearning/leave-management-api-sanketahire"))
                        .license(new License()
                                .name("Raja Software Labs Internal Training")
                                .url("https://rajasoftwarelabs.com")));
    }
}
