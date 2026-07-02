package com.example.citassaludservice.infrastructure.persistence.adapter;

import com.example.citassaludservice.domain.exception.CitaDuplicadaException;
import com.example.citassaludservice.domain.model.Cita;
import com.example.citassaludservice.domain.model.EstadoCita;
import com.example.citassaludservice.domain.port.out.CitaRepository;
import com.example.citassaludservice.infrastructure.persistence.mapper.PersistenceMapper;
import com.example.citassaludservice.infrastructure.persistence.repository.CitaJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CitaRepositoryAdapter implements CitaRepository {

    private final CitaJpaRepository jpaRepository;
    private final PersistenceMapper mapper;

    public CitaRepositoryAdapter(CitaJpaRepository jpaRepository, PersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cita save(Cita cita) {
        try {
            // saveAndFlush fuerza la validación de la constraint UNIQUE
            // (paciente_id, disponibilidad_id) de inmediato, garantizando idempotencia
            // ante doble envío (FR-010) y traduciendo el fallo a una excepción de dominio.
            return mapper.toDomain(jpaRepository.saveAndFlush(mapper.toEntity(cita)));
        } catch (DataIntegrityViolationException ex) {
            throw new CitaDuplicadaException(
                    "Ya existe una cita para este paciente en la franja seleccionada.");
        }
    }

    @Override
    public Optional<Cita> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Cita> findByPacienteId(UUID pacienteId) {
        return jpaRepository.findByPacienteId(pacienteId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Cita> findByPacienteIdAndEstado(UUID pacienteId, EstadoCita estado) {
        return jpaRepository.findByPacienteIdAndEstado(pacienteId, estado.name())
                .stream().map(mapper::toDomain).toList();
    }
}
