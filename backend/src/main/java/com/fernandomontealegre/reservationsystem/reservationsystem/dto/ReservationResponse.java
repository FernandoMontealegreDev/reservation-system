package com.fernandomontealegre.reservationsystem.reservationsystem.dto;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.HotelRoom;
import com.fernandomontealegre.reservationsystem.reservationsystem.model.Reservation.ReservationStatus;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Datos de respuesta de una reserva")
public class ReservationResponse {

    @Schema(description = "ID único de la reserva", example = "1")
    private Long id;

    @Schema(description = "Datos del servicio (habitación) reservado")
    private HotelRoom service;

    @Schema(description = "Fecha y hora de la reserva", example = "2024-09-15T14:30:00")
    private LocalDateTime reservationDateTime;

    @Schema(description = "Estado de la reserva", example = "PENDING")
    private String status;

    // Constructor vacío
    public ReservationResponse() {
    }

    // Constructor con parámetros
    public ReservationResponse(Long id, HotelRoom service, LocalDateTime reservationDateTime, ReservationStatus status) {
        this.id = id;
        this.service = service;
        this.reservationDateTime = reservationDateTime;
        this.status = status.toString(); 
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HotelRoom getService() {
        return service;
    }

    public void setService(HotelRoom service) {
        this.service = service;
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