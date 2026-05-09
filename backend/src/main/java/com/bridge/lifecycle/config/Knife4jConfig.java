package com.bridge.lifecycle.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API 文档配置
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Configuration
public class Knife4jConfig {

    /**
     * OpenAPI 配置
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("桥梁设备全生命周期管理系统 API")
                        .description("RESTful API 接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("程国忠")
                                .email("chenggzhong@ecjtu.edu.cn"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}