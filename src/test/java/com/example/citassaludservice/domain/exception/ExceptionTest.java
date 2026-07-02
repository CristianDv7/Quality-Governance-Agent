package com.example.citassaludservice.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ExceptionTest {

    @Test
    void given_FranjaNoDisponibleException_when_created_then_has_message() {
        var ex = new FranjaNoDisponibleException("franja ocupada");
        assertThat(ex.getMessage()).isEqualTo("franja ocupada");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void given_RecursoNoEncontradoException_when_created_then_has_message() {
        var ex = new RecursoNoEncontradoException("recurso no existe");
        assertThat(ex.getMessage()).isEqualTo("recurso no existe");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void given_CitaDuplicadaException_when_created_then_has_message() {
        var ex = new CitaDuplicadaException("cita ya existe");
        assertThat(ex.getMessage()).isEqualTo("cita ya existe");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}
