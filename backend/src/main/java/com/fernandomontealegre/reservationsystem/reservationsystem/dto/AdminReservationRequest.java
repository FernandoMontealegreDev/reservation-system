package com.fernandomontealegre.reservationsystem.reservationsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Datos para crear o actualizar una reserva por parte del administrador")
public class AdminReservationRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(description = "ID del usuario que realiza la reserva", example = "1")
    private Long userId;

    @NotNull(message = "El ID del servicio (habitación) es obligatorio")
    @Schema(description = "ID del servicio (habitación) a reservar", example = "1")
    private Long serviceId;

    @NotNull(message = "La fecha y hora de la reserva son obligatorias")
    @Schema(description = "Fecha y hora de la reserva", example = "2024-09-15T14:30:00")
    private LocalDateTime reservationDateTime;

    @Schema(description = "Estado de la reserva", example = "CONFIRMED")
    private String status;

    // Constructor vacío
    public AdminReservationRequest() {
    }

    // Constructor con parámetros
    public AdminReservationRequest(Long userId, Long serviceId, LocalDateTime reservationDateTime, String status) {
        this.userId = userId;
        this.serviceId = serviceId;
        this.reservationDateTime = reservationDateTime;
        this.status = status;
    }

    // Getters y Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}