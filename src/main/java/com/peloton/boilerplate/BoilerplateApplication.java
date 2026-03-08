package com.peloton.boilerplate;

import com.peloton.boilerplate.service.common.LoginUserAuditorAware;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class
})
// DB 사용 시 아래 주석 해제하고 exclude 제거
// @EnableJpaAuditing
// @EntityScan(basePackages = { "com.peloton" })
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

	// DB 사용 시 주석 해제
	// @Bean
	// public AuditorAware<String> auditorProvider() {
	// 	return new LoginUserAuditorAware();
	// }

}
