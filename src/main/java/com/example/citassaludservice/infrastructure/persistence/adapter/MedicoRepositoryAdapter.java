package com.example.citassaludservice.infrastructure.persistence.adapter;

import com.example.citassaludservice.domain.model.Especialidad;
import com.example.citassaludservice.domain.model.Medico;
import com.example.citassaludservice.domain.port.out.MedicoRepository;
import com.example.citassaludservice.infrastructure.persistence.mapper.PersistenceMapper;
import com.example.citassaludservice.infrastructure.persistence.repository.MedicoJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MedicoRepositoryAdapter implements MedicoRepository {

    private final MedicoJpaRepository jpaRepository;
    private final PersistenceMapper mapper;

    public MedicoRepositoryAdapter(MedicoJpaRepository jpaRepository, PersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Medico> findByEspecialidadAndFecha(Especialidad especialidad, LocalDate fecha) {
        return jpaRepository.findByEspecialidad(especialidad)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Medico> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Medico> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
