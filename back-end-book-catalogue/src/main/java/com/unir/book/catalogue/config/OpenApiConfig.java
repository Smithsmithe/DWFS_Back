package com.unir.book.catalogue.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI catalogueOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Book Catalogue API")
                                .description(
                                        "REST API for managing book catalogue resources"
                                )
                                .version("v1.0")
                                .contact(
                                        new Contact()
                                                .name("UNIR Project")
                                                .email("support@unir.local")
                                )
                                .license(
                                        new License()
                                                .name("Academic Project")
                                )
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("Project documentation")
                );
    }
}