package com.api.spring.boot.ventas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Ventas - Perfunlandia")
                .description("API REST para gestión de ventas con soporte HATEOAS")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Equipo de Desarrollo")
                    .email("desarrollo@perfunlandia.com")
                    .url("https://perfunlandia.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8181")
                    .description("Servidor local de desarrollo"),
                new Server()
                    .url("http://localhost:8888")
                    .description("API Gateway")
            ));
    }
} 