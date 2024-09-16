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

import java.io.IOException;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    // Rutas que no requieren autenticación
    private static final List<String> NON_SECURED_PATHS = List.of(
        "/api/auth/register",
        "/api/auth/login",
        "/api/auth/refresh-token",
        "/v3/api-docs",
        "/swagger-ui"
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        String requestPath = request.getServletPath();

        // Verificar si la ruta es no protegida usando una comprobación más flexible
        if (isNonSecuredPath(requestPath)) {
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
            } catch (IllegalArgumentException e) {
                logger.error("No se puede obtener el token JWT", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token JWT no válido");
                return;
            } catch (ExpiredJwtException e) {
                logger.warn("El token JWT ha expirado", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT expirado");
                return;
            }
        } else {
            logger.warn("El token JWT no comienza con 'Bearer '");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        chain.doFilter(request, response);
    }

    // Método para verificar si una ruta es no protegida usando coincidencias flexibles
    private boolean isNonSecuredPath(String requestPath) {
        return NON_SECURED_PATHS.stream().anyMatch(requestPath::startsWith);
    }
}