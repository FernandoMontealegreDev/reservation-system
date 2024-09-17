package com.fernandomontealegre.reservationsystem.reservationsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations")
@Schema(description = "Modelo para una reserva de habitación")
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la reserva", example = "1")
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @Schema(description = "Usuario que realizó la reserva")
    private User user;

    @NotNull(message = "El servicio es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    @ToString.Exclude
    @Schema(description = "Habitación reservada")
    private HotelRoom service;

    @NotNull(message = "La fecha y hora de reserva son obligatorias")
    @Future(message = "La fecha y hora de la reserva deben ser futuras")
    @Column(nullable = false)
    @Schema(description = "Fecha y hora de la reserva", example = "2024-09-15T14:30:00")
    private LocalDateTime reservationDateTime;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Estado de la reserva", example = "PENDING")
    private ReservationStatus status;

    // Define el enum para el estado de la reserva
    public enum ReservationStatus {
        PENDING,
        CONFIRMED,
        CANCELED
    }
}