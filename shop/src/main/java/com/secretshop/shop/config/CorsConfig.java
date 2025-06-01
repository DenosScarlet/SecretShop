package com.secretshop.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Разрешить фронтенд
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешенные методы
                .allowedHeaders("*") // Разрешить все заголовки
                .allowCredentials(true); // Для токенов авторизации
    }
}
