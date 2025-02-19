package com.peloton.boilerplate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> Customizer.withDefaults())  // CORS 활성화
                .csrf(csrf -> csrf.disable())             // CSRF 보호 비활성화 (API 개발 시)
                .authorizeHttpRequests(auth -> auth
                            .anyRequest().permitAll()  // 그 외 요청은 인증 필요
//                        .requestMatchers("/public/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Swagger 및 공용 API 허용
//                        .anyRequest().authenticated()  // 그 외 요청은 인증 필요
                )
                .formLogin(login -> login.disable())  // 기본 로그인 폼 활성화
                .httpBasic(basic -> basic.disable());   // HTTP Basic 인증 비활성화

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
