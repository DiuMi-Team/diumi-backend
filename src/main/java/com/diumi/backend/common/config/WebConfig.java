package com.diumi.backend.common.config;

import com.diumi.backend.visitor.web.CurrentVisitorArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final CurrentVisitorArgumentResolver currentVisitorArgumentResolver;

    public WebConfig(CurrentVisitorArgumentResolver currentVisitorArgumentResolver) {
        this.currentVisitorArgumentResolver = currentVisitorArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentVisitorArgumentResolver);
    }
}
