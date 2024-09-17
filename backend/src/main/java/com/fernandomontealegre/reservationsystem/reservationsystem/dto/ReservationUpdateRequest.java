package com.fernandomontealegre.reservationsystem.reservationsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Datos para actualizar una reserva")
public class ReservationUpdateRequest {

    @NotNull(message = "La fecha y hora de la reserva son obligatorias")
    @Schema(description = "Nueva fecha y hora de la reserva", example = "2024-09-15T14:30:00")
    private LocalDateTime reservationDateTime;

    @Schema(description = "Nuevo estado de la reserva", example = "CONFIRMED")
    private String status;

    // Constructor vacío
    public ReservationUpdateRequest() {
    }

    // Constructor con parámetros
    public ReservationUpdateRequest(LocalDateTime reservationDateTime, String status) {
        this.reservationDateTime = reservationDateTime;
        this.status = status;
    }

    // Getters y Setters

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}