package com.fernandomontealegre.reservationsystem.reservationsystem.model;

public enum RoleType {
    ADMIN("Administrador"),
    CLIENT("Cliente");

    private final String description;

    // Constructor
    RoleType(String description) {
        this.description = description;
    }

    // Obtener la descripción del rol
    public String getDescription() {
        return description;
    }

    // Método para obtener el Role a partir de un String
    public static RoleType fromString(String roleString) {
        for (RoleType role : RoleType.values()) {
            if (role.name().equalsIgnoreCase(roleString)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No se encontró un rol para el valor: " + roleString);
    }
}