package com.msa.kyj_prj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.msa.kyj_prj") // 🔍 config 포함 모든 하위 패키지 스캔
@EnableScheduling
public class KyjPrjApplication {

	public static void main(String[] args) {
		SpringApplication.run(KyjPrjApplication.class, args);
	}
}
