package com.ReZherk.microservice_auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .version("1.0")
                        .description("Authentication and Authorization Service"))
                // Agrega el requisito de seguridad global
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                // Define el esquema de seguridad tipo Bearer JWT
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}

/*
 * @Configuration
 * public class SwaggerConfig {
 * 
 * @Bean
 * public OpenAPI customOpenAPI() {
 * return new OpenAPI()
 * .info(new Info()
 * .title("Auth Service API")
 * .version("1.0")
 * .description("Authentication and Authorization Service"));
 * }
 * }
 */
