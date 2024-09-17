package com.fernandomontealegre.reservationsystem.reservationsystem.dto;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para crear o actualizar una habitación")
public class HotelRoomRequest {

    @NotBlank(message = "El número de habitación es obligatorio")
    @Schema(description = "Número de la habitación", example = "101")
    private String roomNumber;

    @NotBlank(message = "La descripción es obligatoria")
    @Schema(description = "Descripción de la habitación", example = "Habitación sencilla con vista al mar")
    private String description;

    @Positive(message = "El precio debe ser un número positivo")
    @Schema(description = "Precio de la habitación", example = "100.0")
    private double price;

    @NotNull(message = "El tipo de habitación es obligatorio")
    @Schema(description = "Tipo de habitación", example = "SINGLE")
    private RoomType roomType;

    // Constructor vacío
    public HotelRoomRequest() {
    }

    // Constructor con parámetros
    public HotelRoomRequest(String roomNumber, String description, double price, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.description = description;
        this.price = price;
        this.roomType = roomType;
    }

    // Getters y Setters
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}