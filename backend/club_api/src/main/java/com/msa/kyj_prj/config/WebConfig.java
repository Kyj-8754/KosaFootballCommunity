package com.msa.kyj_prj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ 운영 서버 기준 경로로 수정 (이미지 접근용)
        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:/home/ubuntu/app/uploads/");
        .addResourceLocations("file:///C:/workspace-sts4/MsaTeamProject/backend/club_api/uploads/");
    }

    // ✅ 모든 경로에 대해 CORS 허용
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/uploads/**")
                .allowedOrigins("*")
                .allowedMethods("GET");
    }
}
