package com.msa.do_login.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		final String securitySchemeName = "Authorization";

		return new OpenAPI()
				.info(new Info().title("Login API").version("0.0.1").description("Login API 명세서")
						.contact(new Contact().name("관리자").email("dosjin01@gmail.com")))
				.servers(List.of(new Server().url("http://localhost:8111")))
				.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
				.components(new io.swagger.v3.oas.models.Components().addSecuritySchemes(securitySchemeName,
						new SecurityScheme().name(securitySchemeName).type(SecurityScheme.Type.HTTP).scheme("bearer")
								.bearerFormat("JWT")));
	}
}
