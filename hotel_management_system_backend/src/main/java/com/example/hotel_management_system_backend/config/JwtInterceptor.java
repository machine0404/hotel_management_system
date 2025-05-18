package com.example.hotel_management_system_backend.config;

import com.example.hotel_management_system_backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                JwtUtil.parseToken(token); // 你需要在JwtUtil.java里实现parseToken方法
                return true;
            } catch (Exception e) {
                response.setStatus(401);
                return false;
            }
        }
        response.setStatus(401);
        return false;
    }
}
