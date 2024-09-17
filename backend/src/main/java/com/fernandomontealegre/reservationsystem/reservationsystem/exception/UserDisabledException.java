package com.fernandomontealegre.reservationsystem.reservationsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UserDisabledException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserDisabledException(String message) {
        super(message);
    }
}