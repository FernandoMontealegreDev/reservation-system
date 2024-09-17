package com.fernandomontealegre.reservationsystem.reservationsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API del Sistema de Reservas")
                        .version("2.0")
                        .description("API para gestionar reservas de habitaciones.")
                        .contact(new Contact()
                                .name("Fernando Montealegre")
                                .email("cesarmontealegere@gmail.com")
                        )
                );
    }
}