package com.example.citassaludservice.application.usecase;

import com.example.citassaludservice.domain.exception.FranjaNoDisponibleException;
import com.example.citassaludservice.domain.exception.RecursoNoEncontradoException;
import com.example.citassaludservice.domain.model.*;
import com.example.citassaludservice.domain.port.out.CitaRepository;
import com.example.citassaludservice.domain.port.out.DisponibilidadRepository;
import com.example.citassaludservice.domain.port.out.NotificacionWhatsAppPort;
import com.example.citassaludservice.domain.port.out.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservarCitaServiceTest {

    @Mock DisponibilidadRepository disponibilidadRepository;
    @Mock PacienteRepository pacienteRepository;
    @Mock CitaRepository citaRepository;
    @Mock NotificacionWhatsAppPort notificacionPort;

    ReservarCitaService service;

    private final UUID pacienteId = UUID.fromString("b1b2c3d4-0001-0001-0001-000000000001");
    private final UUID disponibilidadId = UUID.fromString("c1000001-0001-0001-0001-000000000001");
    private final UUID medicoId = UUID.fromString("a1b2c3d4-0001-0001-0001-000000000001");
    private final String telefono = "+573001234567";

    @BeforeEach
    void setUp() {
        service = new ReservarCitaService(
                disponibilidadRepository, pacienteRepository, citaRepository, notificacionPort);
    }

    private DisponibilidadMedico disponibilidadDisponible() {
        return new DisponibilidadMedico(
                disponibilidadId, medicoId,
                LocalDate.now().plusDays(1),
                LocalTime.of(8, 0), LocalTime.of(8, 30),
                EstadoDisponibilidad.DISPONIBLE
        );
    }

    private Paciente paciente() {
        return new Paciente(pacienteId, "Ana García", telefono);
    }

    @Test
    void given_franja_disponible_when_reservar_then_cita_created_and_notification_sent() {
        DisponibilidadMedico disponibilidad = disponibilidadDisponible();
        when(disponibilidadRepository.findByIdForUpdate(disponibilidadId))
                .thenReturn(Optional.of(disponibilidad));
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(paciente()));
        Cita expectedCita = mock(Cita.class);
        when(citaRepository.save(any())).thenReturn(expectedCita);

        Cita result = service.reservar(pacienteId, disponibilidadId);

        assertThat(disponibilidad.getEstado()).isEqualTo(EstadoDisponibilidad.OCUPADA);
        verify(citaRepository).save(any());
        verify(notificacionPort).enviar(eq(expectedCita), eq(telefono));
        assertThat(result).isEqualTo(expectedCita);
    }

    @Test
    void given_franja_ocupada_when_reservar_then_throws_franja_no_disponible() {
        DisponibilidadMedico ocupada = new DisponibilidadMedico(
                disponibilidadId, medicoId,
                LocalDate.now().plusDays(1),
                LocalTime.of(8, 0), LocalTime.of(8, 30),
                EstadoDisponibilidad.OCUPADA
        );
        when(disponibilidadRepository.findByIdForUpdate(disponibilidadId))
                .thenReturn(Optional.of(ocupada));

        assertThatThrownBy(() -> service.reservar(pacienteId, disponibilidadId))
                .isInstanceOf(FranjaNoDisponibleException.class)
                .hasMessageContaining("ocupada");

        verify(citaRepository, never()).save(any());
        verify(notificacionPort, never()).enviar(any(), any());
    }

    @Test
    void given_nonexistent_disponibilidad_when_reservar_then_throws_recurso_no_encontrado() {
        when(disponibilidadRepository.findByIdForUpdate(disponibilidadId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reservar(pacienteId, disponibilidadId))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(citaRepository, never()).save(any());
        verify(notificacionPort, never()).enviar(any(), any());
    }

    @Test
    void given_nonexistent_paciente_when_reservar_then_throws_recurso_no_encontrado_and_no_cita() {
        when(disponibilidadRepository.findByIdForUpdate(disponibilidadId))
                .thenReturn(Optional.of(disponibilidadDisponible()));
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reservar(pacienteId, disponibilidadId))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Paciente");

        verify(citaRepository, never()).save(any());
        verify(notificacionPort, never()).enviar(any(), any());
    }
}
