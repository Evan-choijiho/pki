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
                .cors(cors -> cors.disable())             // cors 방지
                .csrf(csrf -> csrf.disable())             // CSRF 보호 비활성화 (API 개발 시)
                .formLogin(login -> login.disable())      // 기본 로그인 폼 비활성화
                .httpBasic(basic -> basic.disable());     // HTTP Basic 인증 비활성화 ( ID, Pass 기반 아닌 JWT 사용 )
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }    // ID, Pass 로그인 단방향 암호화
}
