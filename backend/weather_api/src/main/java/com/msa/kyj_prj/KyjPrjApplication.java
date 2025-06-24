package com.msa.kyj_prj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.msa.kyj_prj")
@SpringBootApplication
public class KyjPrjApplication {

	public static void main(String[] args) {
		SpringApplication.run(KyjPrjApplication.class, args);
	}

}
