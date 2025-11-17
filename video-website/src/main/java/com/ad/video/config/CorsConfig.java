package com.ad.video.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")                        // 所有接口都允许跨域
                        .allowedOrigins("http://localhost:5173")  // 指定前端地址
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许方法
                        .allowedHeaders("*")                     // 允许请求头
                        .allowCredentials(true);                 // 允许携带 Cookie
            }
        };
    }
}
