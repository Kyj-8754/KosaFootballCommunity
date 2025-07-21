package com.msa.kyj_prj;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "기상청 단기예보 API",
        description = "기상청 OpenAPI를 활용한 단기 예보 시스템",
        version = "1.0"
    )
)
@Configuration
public class SwaggerConfig {
}
