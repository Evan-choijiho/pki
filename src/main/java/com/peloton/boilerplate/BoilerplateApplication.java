package com.peloton.boilerplate;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EntityScan(basePackages = { "com.peloton" })   	// Entity 클래스 검색할 패키지 지정
@ComponentScan(basePackages = { "com.peloton" })    // Spring bean 검색할 패키지 지정
//@EnableScheduling ==> @Scheduled 사용시, 활성화
public class BoilerplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoilerplateApplication.class, args);
	}

	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

}
