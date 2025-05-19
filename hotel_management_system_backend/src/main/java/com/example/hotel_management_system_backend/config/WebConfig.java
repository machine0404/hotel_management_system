package com.example.hotel_management_system_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/api/admin/**")
            .excludePathPatterns("/api/admin/login");
        // 新增：用户端也拦截，排除注册和登录
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/api/user/**")
            .excludePathPatterns("/api/user/login", "/api/user/register", "/api/user/room-list", "/api/user/room-type-list");
    }
}
