package com.fernandomontealegre.reservationsystem.reservationsystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    // Rutas que no requieren autenticación
    private static final List<String> NON_SECURED_PATHS = List.of(
        "/api/auth/**",
        "/v3/api-docs",
        "/swagger-ui"
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        String requestPath = request.getServletPath();

        if (isNonSecuredPath(requestPath)) {
            logger.debug("Acceso permitido sin autenticación a la ruta: " + requestPath);
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                logger.debug("JWT Token recibido: " + jwtToken);
                logger.debug("Usuario extraído del token: " + username);
            } catch (IllegalArgumentException e) {
                logger.error("No se puede obtener el token JWT", e);
                sendJsonErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Token JWT no válido");
                return;
            } catch (ExpiredJwtException e) {
                logger.warn("El token JWT ha expirado", e);
                sendJsonErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token JWT expirado");
                return;
            }
        } else {
            logger.warn("El token JWT no comienza con 'Bearer ' o es nulo");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Autenticando al usuario: " + username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.debug("Usuario autenticado: " + username);
            } else {
                logger.warn("El token JWT no es válido");
                sendJsonErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token JWT no es válido");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    // Método para verificar si una ruta es no protegida usando coincidencias flexibles
    private boolean isNonSecuredPath(String requestPath) {
        return NON_SECURED_PATHS.stream().anyMatch(requestPath::startsWith);
    }

    // Método auxiliar para enviar una respuesta de error en formato JSON
    private void sendJsonErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write("{\"error\": \"" + message + "\"}");
        writer.flush();
        writer.close();
    }
}