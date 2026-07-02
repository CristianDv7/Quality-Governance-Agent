package com.example.citassaludservice.infrastructure.persistence.repository;

import com.example.citassaludservice.domain.model.Especialidad;
import com.example.citassaludservice.infrastructure.persistence.entity.MedicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MedicoJpaRepository extends JpaRepository<MedicoJpaEntity, UUID> {
    List<MedicoJpaEntity> findByEspecialidad(Especialidad especialidad);
}
