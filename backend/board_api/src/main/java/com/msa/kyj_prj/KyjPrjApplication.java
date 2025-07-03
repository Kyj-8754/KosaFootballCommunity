package com.msa.kyj_prj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.msa") 
@MapperScan("com.msa")
public class KyjPrjApplication {

	public static void main(String[] args) {
		SpringApplication.run(KyjPrjApplication.class, args);
	}

}
