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
    @Schema(description = "Número de la habitación", example = "101")
    private String roomNumber;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Schema(description = "Descripción de la habitación", example = "Habitación sencilla con vista al mar")
    private String description;

    @Positive(message = "El precio debe ser un número positivo")
    @Schema(description = "Precio de la habitación", example = "100.0")
    private double price;

    @NotNull(message = "El tipo de habitación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Tipo de habitación", example = "SINGLE")
    private RoomType roomType;
}