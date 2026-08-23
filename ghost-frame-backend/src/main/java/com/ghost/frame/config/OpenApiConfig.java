package com.ghost.frame.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("XX系统 API 文档")
            .version("1.0.0")
            .description("系统接口文档，支持在线调试")
            .contact(new Contact().name("张三").email("zhangsan@example.com"))
            .license(new License().name("MIT")))
        // 全局安全认证（JWT 示例）
        .addSecurityItem(new SecurityRequirement().addList("Bearer Token"))
        .components(new Components()
            .addSecuritySchemes("Bearer Token",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
  }
}