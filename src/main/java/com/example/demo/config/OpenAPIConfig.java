package com.example.demo.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Ingredientes e Compartimentos")
                        .description("API responsável por gerenciar estoque, compartimentos e histórico de movimentações de uma fábrica de alimentos.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mariane Araujo")
                                .email("email@exemplo.com")
                        )
                );
    }
}
