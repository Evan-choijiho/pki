package com.peloton.boilerplate;

import com.peloton.boilerplate.service.common.LoginUserAuditorAware;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = { "com.peloton" })   	// Entity 클래스 검색할 패키지 지정
@ComponentScan(basePackages = { "com.peloton" })    // Spring bean 검색할 패키지 지정
@EnableScheduling // ==> @Scheduled 사용시, 활성화
public class BoilerplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoilerplateApplication.class, args);
	}

	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	@Bean
	public AuditorAware<String> auditorProvider() { // 사용자의 정보 조회 후 DB 등록자와 수정자로 활용
		return new LoginUserAuditorAware();
	}

}
