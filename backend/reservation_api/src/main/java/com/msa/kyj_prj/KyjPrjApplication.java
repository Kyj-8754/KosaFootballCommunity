package com.msa.kyj_prj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // 추가
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate; // 추가

@SpringBootApplication
@EnableScheduling
public class KyjPrjApplication {

	public static void main(String[] args) {
		SpringApplication.run(KyjPrjApplication.class, args);
	}

	// ✅ RestTemplate Bean 등록
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
