package com.oxxito.exception;

import com.oxxito.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Maneja la excepción cuando un producto no existe (Error 404)
    @ExceptionHandler(NotFoundProduct.class)
    public ResponseEntity<ErrorResponseDto> handleRecursoNoEncontrado(NotFoundProduct ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Maneja los errores de validación de @Valid (ej. campos vacíos) (Error 400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidations(MethodArgumentNotValidException ex) {
        String mensajeDeError = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                mensajeDeError
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}