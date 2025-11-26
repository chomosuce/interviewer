package com.t1impulse.interviewer.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SystemTokenFilter extends OncePerRequestFilter {

    @Value("${app.system.token:}")
    private String systemToken;

    private static final String SYSTEM_TOKEN_HEADER = "X-System-Token";
    
    @PostConstruct
    public void init() {
        if (systemToken != null && !systemToken.isEmpty()) {
            log.info("SystemTokenFilter initialized with token: {} (length: {})", 
                    systemToken.substring(0, Math.min(3, systemToken.length())) + "...", systemToken.length());
        } else {
            log.warn("SystemTokenFilter initialized WITHOUT token - protection is DISABLED");
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        log.debug("SystemTokenFilter called: path={}, method={}, systemToken={}", 
                requestPath, method, systemToken != null && !systemToken.isEmpty() ? "SET" : "NOT SET");
        
        // Проверяем, является ли это защищенным эндпоинтом
        // GET /api/public/session/{accessToken} - требует токен
        // POST /api/public/session/{accessToken}/results - требует токен
        // POST /api/public/session/{accessToken}/start - НЕ требует токен (доступен пользователям)
        boolean requiresToken = false;
        
        if (requestPath.startsWith("/api/public/session/")) {
            // GET /api/public/session/{accessToken} - требует токен (но не /start)
            if ("GET".equals(method) && !requestPath.endsWith("/start") && !requestPath.endsWith("/results")) {
                requiresToken = true;
                log.info("SystemTokenFilter: GET request requires token for path={}", requestPath);
            } 
            // POST /api/public/session/{accessToken}/results - требует токен
            else if ("POST".equals(method) && requestPath.endsWith("/results")) {
                requiresToken = true;
                log.info("SystemTokenFilter: POST /results requires token for path={}", requestPath);
            }
        }
        
        // Если токен не установлен в конфигурации, пропускаем проверку (для разработки)
        if (!requiresToken) {
            log.debug("SystemTokenFilter: No token required for path={}, method={}", requestPath, method);
            filterChain.doFilter(request, response);
            return;
        }
        
        if (systemToken == null || systemToken.isEmpty()) {
            log.warn("SystemTokenFilter: Token required but systemToken is not set! Allowing access for path={}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }
        
        // Проверяем наличие системного токена в заголовке
        String providedToken = request.getHeader(SYSTEM_TOKEN_HEADER);
        
        log.info("SystemTokenFilter: path={}, method={}, requiresToken={}, providedToken={}", 
                requestPath, method, requiresToken, providedToken != null ? "PRESENT" : "MISSING");
        
        if (providedToken == null || !providedToken.equals(systemToken)) {
            log.warn("SystemTokenFilter: Access denied for path={}, method={}", requestPath, method);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Access denied. System token required.\"}");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}


