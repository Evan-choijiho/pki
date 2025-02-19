package com.peloton.boilerplate.config;

import com.peloton.boilerplate.Interceptor.HttpInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{

    private final HttpInterceptor httpInterceptor;

    public WebConfig(HttpInterceptor httpInterceptor) {
        this.httpInterceptor = httpInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(httpInterceptor)
                .addPathPatterns("/**");
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() { // cross domain is allowed
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")    // 모든 도메인 허용 (보안 필요 시 변경)
                        .allowedMethods("*");   // "GET", "POST", "PUT", "DELETE", "OPTIONS"
            }
        };
    }
}
