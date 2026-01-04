package com.ad.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // 从环境变量或配置文件中获取允许的来源
                String allowedOrigins = System.getenv("ALLOWED_ORIGINS");
                if (allowedOrigins == null || allowedOrigins.isEmpty()) {
                    // 默认允许本地开发环境和生产环境
                    allowedOrigins = "http://localhost:5173,http://localhost:5174,http://localhost:5175,http://localhost:80,http://localhost:8080,https://localhost,http://127.0.0.1,http://127.0.0.1:80,http://127.0.0.1:8080";
                }
                
                registry.addMapping("/**")  // 修改为/**以确保所有API端点都能处理跨域请求
                        .allowedOrigins(allowedOrigins.split(","))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}