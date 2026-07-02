package com.example.citassaludservice.infrastructure.persistence.adapter;

import com.example.citassaludservice.domain.model.Especialidad;
import com.example.citassaludservice.domain.model.Medico;
import com.example.citassaludservice.infrastructure.persistence.entity.MedicoJpaEntity;
import com.example.citassaludservice.infrastructure.persistence.mapper.PersistenceMapper;
import com.example.citassaludservice.infrastructure.persistence.repository.MedicoJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicoRepositoryAdapterTest {

    @Mock MedicoJpaRepository jpaRepository;
    @Mock PersistenceMapper mapper;
    @InjectMocks MedicoRepositoryAdapter adapter;

    @Test
    void given_especialidad_when_findByEspecialidadAndFecha_then_delegates_to_jpa() {
        MedicoJpaEntity entity = new MedicoJpaEntity();
        Medico medico = new Medico(UUID.randomUUID(), "Dr. A", Especialidad.CARDIOLOGIA);
        when(jpaRepository.findByEspecialidad(Especialidad.CARDIOLOGIA)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(medico);

        List<Medico> result = adapter.findByEspecialidadAndFecha(Especialidad.CARDIOLOGIA, LocalDate.now());

        assertThat(result).hasSize(1).contains(medico);
    }

    @Test
    void given_repository_when_findAll_then_returns_all_medicos() {
        MedicoJpaEntity entity = new MedicoJpaEntity();
        Medico medico = new Medico(UUID.randomUUID(), "Dr. B", Especialidad.MEDICINA_GENERAL);
        when(jpaRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(medico);

        List<Medico> result = adapter.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void given_existing_id_when_findById_then_returns_medico() {
        UUID id = UUID.randomUUID();
        MedicoJpaEntity entity = new MedicoJpaEntity();
        Medico medico = new Medico(id, "Dra. C", Especialidad.PEDIATRIA);
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(medico);

        Optional<Medico> result = adapter.findById(id);

        assertThat(result).isPresent().contains(medico);
    }

    @Test
    void given_nonexistent_id_when_findById_then_returns_empty() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Medico> result = adapter.findById(id);

        assertThat(result).isEmpty();
    }
}
