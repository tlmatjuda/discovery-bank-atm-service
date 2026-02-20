package com.discovery.atm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI discoveryAtmOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Discovery ATM Service API")
                .version("v1")
                .description("Assessment API for balance queries, withdrawals, and ATM note dispensing"));
    }
}
