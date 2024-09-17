package com.fernandomontealegre.reservationsystem.reservationsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para cambiar la contraseña del usuario")
public class ChangePasswordRequest {

    @NotBlank(message = "La contraseña antigua es obligatoria")
    @Schema(description = "Contraseña antigua del usuario", example = "password123")
    private String oldPassword;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Schema(description = "Nueva contraseña del usuario", example = "nuevaPassword456")
    private String newPassword;

    // Constructor vacío
    public ChangePasswordRequest() {
    }

    // Constructor con parámetros
    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    // Getters y Setters

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

     public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}