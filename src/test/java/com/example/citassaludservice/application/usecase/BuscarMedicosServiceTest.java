package com.example.citassaludservice.application.usecase;

import com.example.citassaludservice.domain.model.Especialidad;
import com.example.citassaludservice.domain.model.Medico;
import com.example.citassaludservice.domain.port.out.MedicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarMedicosServiceTest {

    @Mock MedicoRepository medicoRepository;
    BuscarMedicosService service;

    @BeforeEach
    void setUp() {
        service = new BuscarMedicosService(medicoRepository);
    }

    @Test
    void given_especialidad_null_when_buscar_then_returns_all_medicos() {
        List<Medico> todos = List.of(
                new Medico(UUID.randomUUID(), "Dr. A", Especialidad.MEDICINA_GENERAL),
                new Medico(UUID.randomUUID(), "Dra. B", Especialidad.PEDIATRIA)
        );
        when(medicoRepository.findAll()).thenReturn(todos);

        List<Medico> result = service.buscar(null, LocalDate.now());

        assertThat(result).hasSize(2);
        verify(medicoRepository).findAll();
        verify(medicoRepository, never()).findByEspecialidadAndFecha(any(), any());
    }

    @Test
    void given_especialidad_when_buscar_then_filters_by_especialidad() {
        List<Medico> filtered = List.of(
                new Medico(UUID.randomUUID(), "Dr. C", Especialidad.CARDIOLOGIA)
        );
        when(medicoRepository.findByEspecialidadAndFecha(Especialidad.CARDIOLOGIA, LocalDate.now()))
                .thenReturn(filtered);

        List<Medico> result = service.buscar(Especialidad.CARDIOLOGIA, LocalDate.now());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEspecialidad()).isEqualTo(Especialidad.CARDIOLOGIA);
    }

    @Test
    void given_no_medicos_when_buscar_then_returns_empty_list() {
        when(medicoRepository.findAll()).thenReturn(List.of());

        List<Medico> result = service.buscar(null, null);

        assertThat(result).isEmpty();
    }
}
