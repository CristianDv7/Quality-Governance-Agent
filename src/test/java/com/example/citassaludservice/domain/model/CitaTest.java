package com.example.citassaludservice.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class CitaTest {

    @Test
    void given_valid_data_when_create_cita_then_estado_is_confirmada() {
        Cita cita = new Cita(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(9, 30),
                EstadoCita.CONFIRMADA, "ONLINE", Instant.now()
        );
        assertThat(cita.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
        assertThat(cita.getCanalCreacion()).isEqualTo("ONLINE");
        assertThat(cita.getCreadaEn()).isNotNull();
    }
}
