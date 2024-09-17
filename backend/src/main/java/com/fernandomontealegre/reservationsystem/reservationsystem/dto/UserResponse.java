package com.fernandomontealegre.reservationsystem.reservationsystem.dto;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de respuesta del usuario")
public class UserResponse {

    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre de usuario", example = "cliente1")
    private String username;

    @Schema(description = "Nombre completo del usuario", example = "Cliente Uno")
    private String name;

    @Schema(description = "Correo electrónico del usuario", example = "cliente1@example.com")
    private String email;

    @Schema(description = "Rol del usuario", example = "CLIENT")
    private RoleType role;

    // Constructor vacío
    public UserResponse() {
    }

    // Constructor con parámetros
    public UserResponse(Long id, String username, String name, String email, RoleType role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }
}