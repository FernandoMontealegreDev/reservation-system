package com.fernandomontealegre.reservationsystem.reservationsystem.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
public class JwtTokenUtil {

    private String secret = ""; // Reemplaza por la clave generada en Base64 con KeyGenerator

    // Validez del token en milisegundos (5 horas)
    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    // Crear un objeto Key a partir de la clave secreta
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Obtener el username del token JWT
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
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    // Verificar si el token ha expirado
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Generar token para el usuario
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Puedes agregar más información al token si lo deseas
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
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

    // Refrescar el token
    public String refreshToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(claims.getSubject())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

}