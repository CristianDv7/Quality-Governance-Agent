package com.example.citassaludservice.application.usecase;

import com.example.citassaludservice.domain.model.DisponibilidadMedico;
import com.example.citassaludservice.domain.model.EstadoDisponibilidad;
import com.example.citassaludservice.domain.port.out.DisponibilidadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtenerDisponibilidadServiceTest {

    @Mock DisponibilidadRepository repository;
    @InjectMocks ObtenerDisponibilidadService service;

    @Test
    void given_medico_and_fecha_when_obtener_then_delegates_to_repository() {
        UUID medicoId = UUID.randomUUID();
        LocalDate fecha = LocalDate.now().plusDays(1);
        DisponibilidadMedico d = new DisponibilidadMedico(
                UUID.randomUUID(), medicoId, fecha,
                LocalTime.of(9, 0), LocalTime.of(9, 30),
                EstadoDisponibilidad.DISPONIBLE
        );
        when(repository.findDisponiblesByMedicoAndFecha(medicoId, fecha)).thenReturn(List.of(d));

        List<DisponibilidadMedico> result = service.obtener(medicoId, fecha);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMedicoId()).isEqualTo(medicoId);
        verify(repository).findDisponiblesByMedicoAndFecha(medicoId, fecha);
    }

    @Test
    void given_no_disponibilidades_when_obtener_then_returns_empty_list() {
        UUID medicoId = UUID.randomUUID();
        LocalDate fecha = LocalDate.now().plusDays(1);
        when(repository.findDisponiblesByMedicoAndFecha(medicoId, fecha)).thenReturn(List.of());

        assertThat(service.obtener(medicoId, fecha)).isEmpty();
    }
}
