package com.fernandomontealegre.reservationsystem.reservationsystem.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Registrar advertencia
        logger.warn("Intento de acceso no autorizado: {}", authException.getMessage());
        
        // Responder con 401 No autorizado
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
    }
}