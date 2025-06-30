package com.msa.kyj_prj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.msa.kyj_prj")
@SpringBootApplication
@EnableScheduling
public class KyjPrjApplication {

	public static void main(String[] args) {
		SpringApplication.run(KyjPrjApplication.class, args);
	}

}
