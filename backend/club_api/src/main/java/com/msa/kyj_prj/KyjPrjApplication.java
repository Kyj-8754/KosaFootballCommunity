package com.msa.kyj_prj;

import org.mybatis.spring.annotation.MapperScan; // 추가!!
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
@SpringBootApplication(scanBasePackages = {
	    "com.msa.kyj_prj.club",
	    "com.msa.kyj_prj.alarm",
	    "com.msa.kyj_prj.config"
	})

@MapperScan(basePackages = {
	    "com.msa.kyj_prj.club.apply", 
	    "com.msa.kyj_prj.club"    // ★★★ 여기를 꼭 추가해줘야 ClubDAO도 자동 등록!
	})
public class KyjPrjApplication {

    public static void main(String[] args) {
        SpringApplication.run(KyjPrjApplication.class, args);
    }

    // 🔽 RestTemplate 등록
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
}
