package com.example.citassaludservice.infrastructure.persistence.repository;

import com.example.citassaludservice.infrastructure.persistence.entity.PacienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PacienteJpaRepository extends JpaRepository<PacienteJpaEntity, UUID> {
}
