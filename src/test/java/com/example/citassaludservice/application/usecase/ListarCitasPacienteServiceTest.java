package com.example.citassaludservice.application.usecase;

import com.example.citassaludservice.domain.model.Cita;
import com.example.citassaludservice.domain.model.EstadoCita;
import com.example.citassaludservice.domain.port.out.CitaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarCitasPacienteServiceTest {

    @Mock CitaRepository citaRepository;
    @InjectMocks ListarCitasPacienteService service;

    private Cita makeCita() {
        return new Cita(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(9, 30),
                EstadoCita.CONFIRMADA, "WEB", Instant.now());
    }

    @Test
    void given_no_estado_when_listar_then_returns_all_citas() {
        UUID pacienteId = UUID.randomUUID();
        Cita cita = makeCita();
        when(citaRepository.findByPacienteId(pacienteId)).thenReturn(List.of(cita));

        List<Cita> result = service.listar(pacienteId, null);

        assertThat(result).hasSize(1);
        verify(citaRepository).findByPacienteId(pacienteId);
        verify(citaRepository, never()).findByPacienteIdAndEstado(any(), any());
    }

    @Test
    void given_estado_when_listar_then_filters_by_estado() {
        UUID pacienteId = UUID.randomUUID();
        Cita cita = makeCita();
        when(citaRepository.findByPacienteIdAndEstado(pacienteId, EstadoCita.CONFIRMADA)).thenReturn(List.of(cita));

        List<Cita> result = service.listar(pacienteId, EstadoCita.CONFIRMADA);

        assertThat(result).hasSize(1);
        verify(citaRepository).findByPacienteIdAndEstado(pacienteId, EstadoCita.CONFIRMADA);
        verify(citaRepository, never()).findByPacienteId(any());
    }

    @Test
    void given_no_citas_when_listar_then_returns_empty_list() {
        UUID pacienteId = UUID.randomUUID();
        when(citaRepository.findByPacienteId(pacienteId)).thenReturn(List.of());

        assertThat(service.listar(pacienteId, null)).isEmpty();
    }
}
