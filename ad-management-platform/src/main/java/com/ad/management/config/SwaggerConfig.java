package com.ad.management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI adManagementPlatformAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("广告管理平台 API")
                        .description("广告管理平台 - 管理广告商、广告素材、投放策略、数据分析")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("技术支持")
                                .email("support@adplatform.com")));
    }
}