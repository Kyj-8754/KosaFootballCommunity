package com.msa.kyj_prj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.msa.kyj_prj") // 🔍 모든 컴포넌트 스캔
@MapperScan("com.msa.kyj_prj") // ✅ MyBatis DAO 자동 등록
@EnableScheduling
public class KyjPrjApplication {

    public static void main(String[] args) {
        SpringApplication.run(KyjPrjApplication.class, args);
    }
}
