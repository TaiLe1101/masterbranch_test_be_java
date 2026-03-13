package com.masterbranch_be_test.masterbranch_be_test_calendar_java.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI calendarOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Calendar Availability API")
                .description("Backend interview project: events and availability APIs")
                .version("v1"));
    }
}
