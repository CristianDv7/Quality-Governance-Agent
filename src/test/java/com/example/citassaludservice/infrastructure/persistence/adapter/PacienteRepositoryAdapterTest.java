package com.example.citassaludservice.infrastructure.persistence.adapter;

import com.example.citassaludservice.domain.model.Paciente;
import com.example.citassaludservice.infrastructure.persistence.entity.PacienteJpaEntity;
import com.example.citassaludservice.infrastructure.persistence.mapper.PersistenceMapper;
import com.example.citassaludservice.infrastructure.persistence.repository.PacienteJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteRepositoryAdapterTest {

    @Mock PacienteJpaRepository jpaRepository;
    @Mock PersistenceMapper mapper;
    @InjectMocks PacienteRepositoryAdapter adapter;

    @Test
    void given_existing_id_when_findById_then_returns_paciente() {
        UUID id = UUID.randomUUID();
        PacienteJpaEntity entity = new PacienteJpaEntity();
        Paciente paciente = new Paciente(id, "Ana García", "+573001234567");
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(paciente);

        Optional<Paciente> result = adapter.findById(id);

        assertThat(result).isPresent().contains(paciente);
    }

    @Test
    void given_nonexistent_id_when_findById_then_returns_empty() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(adapter.findById(id)).isEmpty();
    }
}
