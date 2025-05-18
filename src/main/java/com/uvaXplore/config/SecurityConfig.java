package com.uvaXplore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with our custom config
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Disable CSRF for simplicity in dev environment
                .csrf(csrf -> csrf.disable())
                // Allow OPTIONS requests for preflight without authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Allow all other requests without authentication
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow React frontend running on localhost:5173
        config.setAllowedOriginPatterns(List.of("http://localhost:5173"));

        // Allow common HTTP methods used by REST APIs
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers, including Authorization etc.
        config.setAllowedHeaders(List.of("*"));

        // Allow credentials like cookies, authorization headers
        config.setAllowCredentials(true);

        // Cache preflight response for 1 hour (optional but recommended)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Apply this config to all paths
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
