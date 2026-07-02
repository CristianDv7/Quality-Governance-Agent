package com.example.citassaludservice.interface_.rest;

import com.example.citassaludservice.domain.exception.CitaDuplicadaException;
import com.example.citassaludservice.domain.exception.FranjaNoDisponibleException;
import com.example.citassaludservice.domain.exception.RecursoNoEncontradoException;
import com.example.citassaludservice.interface_.rest.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FranjaNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleFranjaNoDisponible(FranjaNoDisponibleException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error("FRANJA_NO_DISPONIBLE", ex.getMessage()));
    }

    @ExceptionHandler(CitaDuplicadaException.class)
    public ResponseEntity<ErrorResponse> handleCitaDuplicada(CitaDuplicadaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error("CITA_DUPLICADA", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error("CITA_DUPLICADA", "Ya existe una cita para este paciente en la franja seleccionada."));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("RECURSO_NO_ENCONTRADO", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error("ERROR_INTERNO", "Ha ocurrido un error inesperado."));
    }

    private ErrorResponse error(String codigo, String mensaje) {
        ErrorResponse r = new ErrorResponse();
        r.setCodigo(codigo);
        r.setMensaje(mensaje);
        r.setTimestamp(OffsetDateTime.now());
        return r;
    }
}
