package com.example.citassaludservice.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class NotificacionWhatsAppTest {

    @Test
    void given_new_notificacion_when_created_then_estado_is_pendiente() {
        var n = new NotificacionWhatsApp(UUID.randomUUID(), UUID.randomUUID());
        assertThat(n.getEstado()).isEqualTo(EstadoNotificacion.PENDIENTE);
        assertThat(n.getIntentos()).isZero();
    }

    @Test
    void given_pendiente_when_registrarEnvio_then_estado_is_enviada() {
        var n = new NotificacionWhatsApp(UUID.randomUUID(), UUID.randomUUID());
        n.registrarEnvio();
        assertThat(n.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(n.getIntentos()).isEqualTo(1);
        assertThat(n.getUltimoIntento()).isNotNull();
    }

    @Test
    void given_two_failures_when_registrarFallo_then_estado_stays_pendiente() {
        var n = new NotificacionWhatsApp(UUID.randomUUID(), UUID.randomUUID());
        n.registrarFallo("error1");
        assertThat(n.getEstado()).isEqualTo(EstadoNotificacion.PENDIENTE);
        assertThat(n.getIntentos()).isEqualTo(1);
        n.registrarFallo("error2");
        assertThat(n.getEstado()).isEqualTo(EstadoNotificacion.PENDIENTE);
        assertThat(n.getIntentos()).isEqualTo(2);
    }

    @Test
    void given_three_failures_when_registrarFallo_then_estado_is_fallida() {
        var n = new NotificacionWhatsApp(UUID.randomUUID(), UUID.randomUUID());
        n.registrarFallo("e1");
        n.registrarFallo("e2");
        n.registrarFallo("e3");
        assertThat(n.getEstado()).isEqualTo(EstadoNotificacion.FALLIDA);
        assertThat(n.getError()).isEqualTo("e3");
        assertThat(n.getUltimoIntento()).isNotNull();
    }

    @Test
    void given_notificacion_when_getters_called_then_returns_correct_values() {
        UUID id = UUID.randomUUID();
        UUID citaId = UUID.randomUUID();
        var n = new NotificacionWhatsApp(id, citaId);
        assertThat(n.getId()).isEqualTo(id);
        assertThat(n.getCitaId()).isEqualTo(citaId);
        assertThat(n.getError()).isNull();
    }
}
