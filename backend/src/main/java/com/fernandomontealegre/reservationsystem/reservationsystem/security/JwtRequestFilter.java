package com.fernandomontealegre.reservationsystem.reservationsystem.security;

// Importaciones de Jakarta Servlet
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Importaciones de Spring Framework y Spring Security
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

// Importaciones de JWT
import io.jsonwebtoken.ExpiredJwtException;

// Importaciones de Java
import java.io.IOException;
import java.util.Arrays; 
import java.util.List;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    // Rutas que no requieren autenticación
    private static final List<String> NON_SECURED_PATHS = Arrays.asList("/api/auth/register", "/api/auth/login");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        // Obtener el path de la solicitud
        String requestPath = request.getServletPath();

        // Ignorar las rutas que no necesitan autenticación
        if (NON_SECURED_PATHS.contains(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // JWT Token está en el formato "Bearer token". Eliminar la palabra "Bearer "
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.error("No se puede obtener el token JWT", e);
            } catch (ExpiredJwtException e) {
                logger.warn("El token JWT ha expirado", e);
            }
        } else {
            logger.warn("El token JWT no comienza con 'Bearer '");
        }

        // Validar token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar UserDetails usando el nombre de usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validar el token
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
}