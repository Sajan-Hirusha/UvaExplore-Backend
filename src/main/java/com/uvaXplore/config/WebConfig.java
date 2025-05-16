package com.uvaXplore.config;  // Place this in your config package or the same package as UserController

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173") // Ensure frontend URL is correct
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
        System.out.println("CORS configuration applied");
    }
}
