package com.fernandomontealegre.reservationsystem.reservationsystem.model;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo para las solicitudes de autenticación JWT")
public class JwtRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Nombre de usuario para la autenticación", example = "cliente1")
    private String username;

    @Schema(description = "Contraseña para la autenticación", example = "password123")
    private String password;

    // Constructor predeterminado para JSON Parsing
    public JwtRequest() {
    }

    public JwtRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    // Getters y Setters
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}