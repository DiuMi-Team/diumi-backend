package com.diumi.backend.common.config;

import com.diumi.backend.visitor.web.CurrentVisitorArgumentResolver;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final CurrentVisitorArgumentResolver currentVisitorArgumentResolver;
    private final List<String> allowedOrigins;

    public WebConfig(
            CurrentVisitorArgumentResolver currentVisitorArgumentResolver,
            @Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {
        this.currentVisitorArgumentResolver = currentVisitorArgumentResolver;
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentVisitorArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "X-Visitor-Id")
                .maxAge(3600);
    }
}
