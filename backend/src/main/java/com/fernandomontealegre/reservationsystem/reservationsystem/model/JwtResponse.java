package com.fernandomontealegre.reservationsystem.reservationsystem.model;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo para las respuestas de autenticación JWT")
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5...")
    private final String jwtToken;

    public JwtResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getToken() {
        return this.jwtToken;
    }
}