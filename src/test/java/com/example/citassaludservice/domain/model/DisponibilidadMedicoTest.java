package com.example.citassaludservice.domain.model;

import com.example.citassaludservice.domain.exception.FranjaNoDisponibleException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class DisponibilidadMedicoTest {

    private DisponibilidadMedico disponibilidad() {
        return new DisponibilidadMedico(
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                LocalTime.of(8, 0),
                LocalTime.of(8, 30),
                EstadoDisponibilidad.DISPONIBLE
        );
    }

    @Test
    void given_disponible_when_reservar_then_estado_becomes_ocupada() {
        DisponibilidadMedico d = disponibilidad();
        d.reservar();
        assertThat(d.getEstado()).isEqualTo(EstadoDisponibilidad.OCUPADA);
    }

    @Test
    void given_ocupada_when_reservar_then_throws_franja_no_disponible() {
        DisponibilidadMedico d = disponibilidad();
        d.reservar();
        assertThatThrownBy(d::reservar)
                .isInstanceOf(FranjaNoDisponibleException.class);
    }

    @Test
    void given_hora_fin_before_hora_inicio_when_create_then_throws() {
        assertThatThrownBy(() -> new DisponibilidadMedico(
                UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(8, 0),
                EstadoDisponibilidad.DISPONIBLE))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
