package com.fernandomontealegre.reservationsystem.reservationsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para actualizar el perfil del usuario")
public class UserProfileUpdateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre completo del usuario", example = "Cliente Actualizado")
    private String name;

    @Email(message = "El correo electrónico debe ser válido")
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Schema(description = "Correo electrónico del usuario", example = "cliente_actualizado@example.com")
    private String email;

    // Constructor vacío
    public UserProfileUpdateRequest() {
    }

    // Constructor con parámetros
    public UserProfileUpdateRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters y Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

     public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}