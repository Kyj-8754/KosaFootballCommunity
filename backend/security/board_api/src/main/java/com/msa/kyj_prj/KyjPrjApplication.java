package com.msa.kyj_prj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.msa") 
@MapperScan("com.msa")
@EnableScheduling
public class KyjPrjApplication {

    public static void main(String[] args) {
        SpringApplication.run(KyjPrjApplication.class, args);
    }
}
