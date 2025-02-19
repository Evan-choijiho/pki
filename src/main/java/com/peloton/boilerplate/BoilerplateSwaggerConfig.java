package com.peloton.boilerplate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({ "dev", "stg" })
@OpenAPIDefinition(
        info = @Info(
                title = "Peloton BoilerPlate API",  // 문서 제목
                description = "펠로톤 API Swagger", // 문서 설명
                version = "1.0",                  // API 버전
                contact = @Contact(
                        name = "Peloton Support",  // 문의 담당자
                        email = "support@peloton.com",
                        url = "https://peloton.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:15422", description = "Local Server"),
                @Server(url = "https://api.peloton.com", description = "Production Server")
        }
)
public class BoilerplateSwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .pathsToMatch("/api/**")
                .build();
    }

}
