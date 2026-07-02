package com.example.citassaludservice.infrastructure.persistence.repository;

import com.example.citassaludservice.infrastructure.persistence.entity.CitaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CitaJpaRepository extends JpaRepository<CitaJpaEntity, UUID> {
    List<CitaJpaEntity> findByPacienteId(UUID pacienteId);
    List<CitaJpaEntity> findByPacienteIdAndEstado(UUID pacienteId, String estado);
}
