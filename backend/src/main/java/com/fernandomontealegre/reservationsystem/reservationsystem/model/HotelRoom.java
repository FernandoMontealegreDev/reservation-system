package com.fernandomontealegre.reservationsystem.reservationsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel_rooms")
@Schema(description = "Representa una habitación de hotel")
public class HotelRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la habitación", example = "1")
    private Long id;
    
    @NotBlank(message = "El número de habitación es obligatorio")
    @Size(max = 10, message = "El número de habitación no debe exceder los 10 caracteres")
    @Column(nullable = false, length = 10)
    @Schema(description = "Número de la habitación", example = "101")
    private String roomNumber;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255, message = "La descripción no debe exceder los 255 caracteres")
    @Column(nullable = false, length = 255)
    @Schema(description = "Descripción de la habitación", example = "Habitación sencilla con vista al mar")
    private String description;

    @Positive(message = "El precio debe ser un número positivo")
    @Digits(integer = 6, fraction = 2, message = "El precio debe tener como máximo 6 dígitos enteros y 2 decimales")
    @Column(nullable = false)
    @Schema(description = "Precio de la habitación", example = "100.00")
    private double price;

    @NotNull(message = "El tipo de habitación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Tipo de habitación", example = "SINGLE")
    private RoomType roomType;
}