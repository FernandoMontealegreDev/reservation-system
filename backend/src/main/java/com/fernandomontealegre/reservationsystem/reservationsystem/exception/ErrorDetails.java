package com.fernandomontealegre.reservationsystem.reservationsystem.exception;

import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;
}