package com.example.citassaludservice.infrastructure.persistence.adapter;

import com.example.citassaludservice.domain.exception.CitaDuplicadaException;
import com.example.citassaludservice.domain.model.Cita;
import com.example.citassaludservice.domain.model.EstadoCita;
import com.example.citassaludservice.infrastructure.persistence.entity.CitaJpaEntity;
import org.springframework.dao.DataIntegrityViolationException;
import com.example.citassaludservice.infrastructure.persistence.mapper.PersistenceMapper;
import com.example.citassaludservice.infrastructure.persistence.repository.CitaJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaRepositoryAdapterTest {

    @Mock CitaJpaRepository jpaRepository;
    @Mock PersistenceMapper mapper;
    @InjectMocks CitaRepositoryAdapter adapter;

    private Cita makeCita(UUID id) {
        return new Cita(id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(9, 30),
                EstadoCita.CONFIRMADA, "WEB", Instant.now());
    }

    @Test
    void given_cita_when_save_then_persists_and_returns_domain() {
        UUID id = UUID.randomUUID();
        Cita cita = makeCita(id);
        CitaJpaEntity entity = new CitaJpaEntity();
        when(mapper.toEntity(cita)).thenReturn(entity);
        when(jpaRepository.saveAndFlush(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(cita);

        Cita result = adapter.save(cita);

        assertThat(result).isEqualTo(cita);
        verify(jpaRepository).saveAndFlush(entity);
    }

    @Test
    void given_duplicate_cita_when_save_then_throws_cita_duplicada() {
        Cita cita = makeCita(UUID.randomUUID());
        CitaJpaEntity entity = new CitaJpaEntity();
        when(mapper.toEntity(cita)).thenReturn(entity);
        when(jpaRepository.saveAndFlush(entity))
                .thenThrow(new DataIntegrityViolationException("uq_cita_idempotente"));

        assertThatThrownBy(() -> adapter.save(cita))
                .isInstanceOf(CitaDuplicadaException.class)
                .hasMessageContaining("Ya existe una cita");
    }

    @Test
    void given_existing_id_when_findById_then_returns_cita() {
        UUID id = UUID.randomUUID();
        Cita cita = makeCita(id);
        CitaJpaEntity entity = new CitaJpaEntity();
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(cita);

        Optional<Cita> result = adapter.findById(id);

        assertThat(result).isPresent().contains(cita);
    }

    @Test
    void given_nonexistent_id_when_findById_then_empty() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());
        assertThat(adapter.findById(id)).isEmpty();
    }

    @Test
    void given_paciente_id_when_findByPacienteId_then_returns_list() {
        UUID pacienteId = UUID.randomUUID();
        Cita cita = makeCita(UUID.randomUUID());
        CitaJpaEntity entity = new CitaJpaEntity();
        when(jpaRepository.findByPacienteId(pacienteId)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(cita);

        List<Cita> result = adapter.findByPacienteId(pacienteId);

        assertThat(result).hasSize(1);
    }

    @Test
    void given_paciente_and_estado_when_findByPacienteIdAndEstado_then_returns_list() {
        UUID pacienteId = UUID.randomUUID();
        Cita cita = makeCita(UUID.randomUUID());
        CitaJpaEntity entity = new CitaJpaEntity();
        when(jpaRepository.findByPacienteIdAndEstado(pacienteId, "CONFIRMADA")).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(cita);

        List<Cita> result = adapter.findByPacienteIdAndEstado(pacienteId, EstadoCita.CONFIRMADA);

        assertThat(result).hasSize(1);
    }
}
