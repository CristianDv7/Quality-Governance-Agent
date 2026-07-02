package com.example.citassaludservice.infrastructure.persistence.adapter;

import com.example.citassaludservice.domain.model.DisponibilidadMedico;
import com.example.citassaludservice.domain.port.out.DisponibilidadRepository;
import com.example.citassaludservice.infrastructure.persistence.mapper.PersistenceMapper;
import com.example.citassaludservice.infrastructure.persistence.repository.DisponibilidadJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DisponibilidadRepositoryAdapter implements DisponibilidadRepository {

    private final DisponibilidadJpaRepository jpaRepository;
    private final PersistenceMapper mapper;

    public DisponibilidadRepositoryAdapter(DisponibilidadJpaRepository jpaRepository,
                                           PersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Optional<DisponibilidadMedico> findByIdForUpdate(UUID id) {
        return jpaRepository.findByIdForUpdate(id).map(mapper::toDomain);
    }

    @Override
    public List<DisponibilidadMedico> findDisponiblesByMedicoAndFecha(UUID medicoId, LocalDate fecha) {
        return jpaRepository.findDisponiblesByMedicoAndFecha(medicoId, fecha)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public DisponibilidadMedico save(DisponibilidadMedico disponibilidad) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(disponibilidad)));
    }
}
