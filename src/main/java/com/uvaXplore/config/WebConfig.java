package com.uvaXplore.config;  // Place this in your config package or the same package as UserController

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // The URL pattern to apply CORS to
                .allowedOrigins("http://localhost:5173")  // Allow your frontend domain (adjust this as needed)
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Allow the necessary HTTP methods
                .allowedHeaders("*");  // Allow all headers (you can restrict this as needed)
    }
}
