package com.utn.frc.backend.pruebaservice.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        // Aquí se maneja la excepción y se devuelve el mensaje con el código de error adecuado.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
