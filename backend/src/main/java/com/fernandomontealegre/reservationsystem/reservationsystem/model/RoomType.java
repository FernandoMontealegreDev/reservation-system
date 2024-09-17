package com.fernandomontealegre.reservationsystem.reservationsystem.model;

public enum RoomType {
    SINGLE("Sencilla"),
    DOUBLE("Doble"),
    SUITE("Suite");

    private final String description;

    // Constructor
    RoomType(String description) {
        this.description = description;
    }

    // Obtener la descripción del tipo de habitación
    public String getDescription() {
        return description;
    }

    // Método para obtener el RoomType a partir de un String
    public static RoomType fromString(String roomTypeString) {
        for (RoomType roomType : RoomType.values()) {
            if (roomType.name().equalsIgnoreCase(roomTypeString)) {
                return roomType;
            }
        }
        throw new IllegalArgumentException("No se encontró un tipo de habitación para el valor: " + roomTypeString);
    }
}