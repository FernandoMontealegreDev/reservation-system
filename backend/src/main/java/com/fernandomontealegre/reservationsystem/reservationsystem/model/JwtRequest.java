package com.fernandomontealegre.reservationsystem.reservationsystem.model;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo para las solicitudes de autenticación JWT")
public class JwtRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Schema(description = "Nombre de usuario para la autenticación", example = "cliente1")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña para la autenticación", example = "password123")
    private String password;
}