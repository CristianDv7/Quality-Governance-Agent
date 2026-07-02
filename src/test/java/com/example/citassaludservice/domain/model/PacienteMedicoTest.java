package com.example.citassaludservice.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class PacienteMedicoTest {

    @Test
    void given_paciente_when_getters_then_returns_correct_values() {
        UUID id = UUID.randomUUID();
        var p = new Paciente(id, "Ana García", "+573001234567");
        assertThat(p.getId()).isEqualTo(id);
        assertThat(p.getNombre()).isEqualTo("Ana García");
        assertThat(p.getTelefono()).isEqualTo("+573001234567");
    }

    @Test
    void given_medico_when_getters_then_returns_correct_values() {
        UUID id = UUID.randomUUID();
        var m = new Medico(id, "Dr. Carlos Mendoza", Especialidad.MEDICINA_GENERAL);
        assertThat(m.getId()).isEqualTo(id);
        assertThat(m.getNombre()).isEqualTo("Dr. Carlos Mendoza");
        assertThat(m.getEspecialidad()).isEqualTo(Especialidad.MEDICINA_GENERAL);
    }

    @Test
    void given_estado_notificacion_enum_when_accessed_then_all_values_available() {
        assertThat(EstadoNotificacion.values()).containsExactlyInAnyOrder(
                EstadoNotificacion.PENDIENTE,
                EstadoNotificacion.ENVIADA,
                EstadoNotificacion.FALLIDA
        );
        assertThat(EstadoNotificacion.valueOf("ENVIADA")).isEqualTo(EstadoNotificacion.ENVIADA);
    }

    @Test
    void given_especialidad_enum_when_accessed_then_all_values_present() {
        assertThat(Especialidad.MEDICINA_GENERAL).isNotNull();
        assertThat(Especialidad.PEDIATRIA).isNotNull();
        assertThat(Especialidad.CARDIOLOGIA).isNotNull();
    }
}
