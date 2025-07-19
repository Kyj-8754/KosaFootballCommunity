package com.msa.kyj_prj;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "JWT",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@OpenAPIDefinition(
	    info = @Info(
	        title = "게시글 API",
	        description = "게시글 및 매치 관련을 관리하는 API",
	        version = "1.0"
	    )
	)
@SecurityRequirement(name = "JWT") // 전체 API에 적용하려면 여기에 선언
public class SwaggerConfig {
}
