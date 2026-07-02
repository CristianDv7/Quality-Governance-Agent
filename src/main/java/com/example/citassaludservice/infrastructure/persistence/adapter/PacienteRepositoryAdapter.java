package com.example.citassaludservice.infrastructure.persistence.adapter;

import com.example.citassaludservice.domain.model.Paciente;
import com.example.citassaludservice.domain.port.out.PacienteRepository;
import com.example.citassaludservice.infrastructure.persistence.mapper.PersistenceMapper;
import com.example.citassaludservice.infrastructure.persistence.repository.PacienteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PacienteRepositoryAdapter implements PacienteRepository {

    private final PacienteJpaRepository jpaRepository;
    private final PersistenceMapper mapper;

    public PacienteRepositoryAdapter(PacienteJpaRepository jpaRepository, PersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Paciente> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
