package com.ad.tracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI adTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("广告追踪服务 API")
                        .description("广告追踪服务提供用户行为追踪、广告展示与点击记录、用户画像管理以及广告推荐功能")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("广告系统开发团队")
                                .email("ad-system@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8084")
                                .description("本地开发服务器"),
                        new Server()
                                .url("http://track.test.com:8084")
                                .description("本地测试服务器")));
    }
}