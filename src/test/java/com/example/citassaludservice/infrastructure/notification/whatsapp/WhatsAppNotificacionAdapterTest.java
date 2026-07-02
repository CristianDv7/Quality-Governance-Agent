package com.example.citassaludservice.infrastructure.notification.whatsapp;

import com.example.citassaludservice.domain.model.Cita;
import com.example.citassaludservice.domain.model.EstadoCita;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class WhatsAppNotificacionAdapterTest {

    private final WhatsAppNotificacionAdapter adapter = new WhatsAppNotificacionAdapter();

    private Cita citaEjemplo() {
        return new Cita(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(9, 30),
                EstadoCita.CONFIRMADA, "ONLINE", Instant.now()
        );
    }

    @Test
    void given_valid_cita_when_enviar_then_does_not_throw() {
        assertThatCode(() -> adapter.enviar(citaEjemplo(), "+573001234567"))
                .doesNotThrowAnyException();
    }

    @Test
    void given_exhausted_retries_when_recuperar_then_registers_failure_without_throwing() {
        // El método @Recover se invoca al agotar los reintentos; NO debe propagar la
        // excepción para no afectar la reserva ya confirmada (FR-005, FR-009).
        Cita cita = citaEjemplo();
        assertThatCode(() ->
                adapter.recuperar(new RuntimeException("proveedor caído"), cita, "+573001234567"))
                .doesNotThrowAnyException();
    }
}
