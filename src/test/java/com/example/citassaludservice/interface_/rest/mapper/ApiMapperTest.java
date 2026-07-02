package com.example.citassaludservice.interface_.rest.mapper;

import com.example.citassaludservice.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ApiMapperTest {

    final ApiMapper mapper = new ApiMapper();

    @Test
    void given_cita_when_toResponse_then_maps_all_fields() {
        UUID id = UUID.randomUUID();
        UUID pacienteId = UUID.randomUUID();
        UUID medicoId = UUID.randomUUID();
        UUID dispId = UUID.randomUUID();
        Cita cita = new Cita(id, pacienteId, medicoId, dispId,
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(9, 30),
                EstadoCita.CONFIRMADA, "WEB", Instant.now());

        var response = mapper.toResponse(cita);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getPacienteId()).isEqualTo(pacienteId);
        assertThat(response.getMedicoId()).isEqualTo(medicoId);
        assertThat(response.getHoraInicio()).isEqualTo("09:00");
        assertThat(response.getHoraFin()).isEqualTo("09:30");
        assertThat(response.getEstado().name()).isEqualTo("CONFIRMADA");
    }

    @Test
    void given_cita_with_null_horas_when_toResponse_then_null_hora_fields() {
        Cita cita = new Cita(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now(), null, null, EstadoCita.CONFIRMADA, "WEB", Instant.now());

        var response = mapper.toResponse(cita);

        assertThat(response.getHoraInicio()).isNull();
        assertThat(response.getHoraFin()).isNull();
    }

    @Test
    void given_list_of_citas_when_toResponseList_then_maps_all() {
        Cita cita = new Cita(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(10, 30),
                EstadoCita.CONFIRMADA, "WEB", Instant.now());
        var list = mapper.toResponseList(List.of(cita));
        assertThat(list).hasSize(1);
    }

    @Test
    void given_medico_when_toResumen_then_maps_all_fields() {
        UUID id = UUID.randomUUID();
        Medico medico = new Medico(id, "Dr. Juan", com.example.citassaludservice.domain.model.Especialidad.MEDICINA_GENERAL);
        var resumen = mapper.toResumen(medico);
        assertThat(resumen.getId()).isEqualTo(id);
        assertThat(resumen.getNombre()).isEqualTo("Dr. Juan");
        assertThat(resumen.getEspecialidad().name()).isEqualTo("MEDICINA_GENERAL");
    }

    @Test
    void given_disponibilidad_when_toFranja_then_maps_all_fields() {
        UUID id = UUID.randomUUID();
        UUID medicoId = UUID.randomUUID();
        DisponibilidadMedico d = new DisponibilidadMedico(id, medicoId, LocalDate.now(),
                LocalTime.of(8, 0), LocalTime.of(8, 30), EstadoDisponibilidad.DISPONIBLE);
        var franja = mapper.toFranja(d);
        assertThat(franja.getId()).isEqualTo(id);
        assertThat(franja.getMedicoId()).isEqualTo(medicoId);
        assertThat(franja.getEstado().name()).isEqualTo("DISPONIBLE");
        assertThat(franja.getHoraInicio()).isEqualTo("08:00");
        assertThat(franja.getHoraFin()).isEqualTo("08:30");
    }

    @Test
    void given_cita_with_early_hora_fin_when_cita_created_then_horaFin_mapped() {
        Cita cita = new Cita(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now(), null, null, EstadoCita.CONFIRMADA, "MOBILE", Instant.now());
        var response = mapper.toResponse(cita);
        assertThat(response.getHoraInicio()).isNull();
        assertThat(response.getHoraFin()).isNull();
    }
}
