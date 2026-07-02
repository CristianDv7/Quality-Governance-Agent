package com.example.citassaludservice.interface_.rest;

import com.example.citassaludservice.domain.exception.CitaDuplicadaException;
import com.example.citassaludservice.domain.exception.FranjaNoDisponibleException;
import com.example.citassaludservice.domain.exception.RecursoNoEncontradoException;
import com.example.citassaludservice.interface_.rest.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;

class GlobalExceptionHandlerTest {

    final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void given_FranjaNoDisponibleException_when_handle_then_409_conflict() {
        ResponseEntity<ErrorResponse> response = handler.handleFranjaNoDisponible(
                new FranjaNoDisponibleException("franja ocupada"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCodigo()).isEqualTo("FRANJA_NO_DISPONIBLE");
        assertThat(response.getBody().getMensaje()).isEqualTo("franja ocupada");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void given_CitaDuplicadaException_when_handle_then_409_conflict() {
        ResponseEntity<ErrorResponse> response = handler.handleCitaDuplicada(
                new CitaDuplicadaException("cita duplicada"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getCodigo()).isEqualTo("CITA_DUPLICADA");
    }

    @Test
    void given_DataIntegrityViolationException_when_handle_then_409_conflict() {
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrity(
                new DataIntegrityViolationException("constraint violation"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getCodigo()).isEqualTo("CITA_DUPLICADA");
    }

    @Test
    void given_RecursoNoEncontradoException_when_handle_then_404_not_found() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new RecursoNoEncontradoException("recurso no encontrado"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCodigo()).isEqualTo("RECURSO_NO_ENCONTRADO");
    }

    @Test
    void given_generic_exception_when_handle_then_500_internal_error() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(
                new RuntimeException("error inesperado"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getCodigo()).isEqualTo("ERROR_INTERNO");
    }
}
