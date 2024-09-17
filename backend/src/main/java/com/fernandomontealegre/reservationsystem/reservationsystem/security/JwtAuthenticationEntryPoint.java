package com.fernandomontealegre.reservationsystem.reservationsystem.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Registrar intento de acceso no autorizado con detalles adicionales de la solicitud
        logger.warn("Intento de acceso no autorizado: URI - {}, Mensaje - {}", request.getRequestURI(), authException.getMessage());
        
        // Establecer el estado de la respuesta a 401 No autorizado
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // Respuesta de error personalizada en formato JSON
        PrintWriter writer = response.getWriter();
        writer.write("{\"error\": \"No autorizado\", \"mensaje\": \"" + authException.getMessage() + "\"}");
        writer.flush();
        writer.close();
    }
}