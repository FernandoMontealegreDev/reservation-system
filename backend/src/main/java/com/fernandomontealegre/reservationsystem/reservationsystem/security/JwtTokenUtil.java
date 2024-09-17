package com.fernandomontealegre.reservationsystem.reservationsystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    // Validez del token en milisegundos (5 horas)
    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    // Crear un objeto Key a partir de la clave secreta
    private Key getSigningKey() {
        // Validación de la longitud de la clave secreta
        if (secret.length() < 32) {
            throw new IllegalArgumentException("La clave secreta debe tener al menos 32 caracteres para HS512.");
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Obtener el nombre de usuario del token JWT
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Obtener la fecha de expiración del token JWT
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Obtener un solo claim
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Para obtener información del token necesitamos la clave secreta
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("El token JWT ha expirado", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Error al analizar el token JWT", e);
        }
    }

    // Verificar si el token ha expirado
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Método para configurar claims comunes (issuedAt, expiration, etc.)
    private JwtBuilder buildToken(Claims claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512);
    }

    // Generar token para el usuario
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(Jwts.claims(claims), userDetails.getUsername()).compact();
    }

    // Refrescar el token
    public String refreshToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return buildToken(claims, claims.getSubject()).compact();
    }

    // Validar token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String usernameFromToken = getUsernameFromToken(token);
        return (usernameFromToken.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Verificar si el token puede ser refrescado
    public Boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token);
    }
}