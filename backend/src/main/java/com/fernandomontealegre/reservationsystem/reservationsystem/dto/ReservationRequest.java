package com.fernandomontealegre.reservationsystem.reservationsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Schema(description = "Datos para crear o actualizar una reserva")
public class ReservationRequest {

    @NotNull(message = "El ID del servicio es obligatorio")
    @Schema(description = "ID del servicio (habitación) a reservar", example = "1")
    private Long serviceId;

    @NotNull(message = "La fecha y hora de la reserva son obligatorias")
    @Schema(description = "Fecha y hora de la reserva", example = "2024-09-15T14:30:00")
    private LocalDateTime reservationDateTime;

    // Constructor vacío
    public ReservationRequest() {
    }

    // Constructor con parámetros
    public ReservationRequest(Long serviceId, LocalDateTime reservationDateTime) {
        this.serviceId = serviceId;
        this.reservationDateTime = reservationDateTime;
    }

    // Getters y Setters
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }
}