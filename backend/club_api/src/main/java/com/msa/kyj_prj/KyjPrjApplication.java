package com.msa.kyj_prj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {
	    "com.msa.kyj_prj.club",
	    "com.msa.kyj_prj.alarm" // 🔥 이거 꼭 추가
	})
public class KyjPrjApplication {

    public static void main(String[] args) {
        SpringApplication.run(KyjPrjApplication.class, args);
    }

    // 🔽 RestTemplate 등록
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
