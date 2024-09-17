package com.fernandomontealegre.reservationsystem.reservationsystem.dto;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de respuesta de una habitación")
public class HotelRoomResponse {

    @Schema(description = "ID único de la habitación", example = "1")
    private Long id;

    @Schema(description = "Número de la habitación", example = "101")
    private String roomNumber;

    @Schema(description = "Descripción de la habitación", example = "Habitación sencilla con vista al mar")
    private String description;

    @Schema(description = "Precio de la habitación", example = "100.0")
    private double price;

    @Schema(description = "Tipo de habitación", example = "SINGLE")
    private RoomType roomType;

    // Constructor vacío
    public HotelRoomResponse() {
    }

    // Constructor con parámetros
    public HotelRoomResponse(Long id, String roomNumber, String description, double price, RoomType roomType) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.description = description;
        this.price = price;
        this.roomType = roomType;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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