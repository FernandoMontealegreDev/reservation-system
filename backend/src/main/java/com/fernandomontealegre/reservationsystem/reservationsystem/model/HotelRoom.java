package com.fernandomontealegre.reservationsystem.reservationsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel_rooms")
public class HotelRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El número de habitación es obligatorio")
    private String roomNumber;
    
    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @Positive(message = "El precio debe ser un número positivo")
    private double price;

    @NotNull(message = "El tipo de habitación es obligatorio")
    @Enumerated(EnumType.STRING)
    private RoomType roomType;
}