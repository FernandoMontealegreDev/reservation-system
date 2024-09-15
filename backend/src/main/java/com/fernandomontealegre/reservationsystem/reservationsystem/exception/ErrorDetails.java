package com.fernandomontealegre.reservationsystem.reservationsystem.exception;

// Importaciones de Lombok
import lombok.*;

// Importaciones de Java
import java.util.Date;


@Data
@AllArgsConstructor
public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;
}