package com.example.citassaludservice.infrastructure.persistence.repository;

import com.example.citassaludservice.infrastructure.persistence.entity.DisponibilidadJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisponibilidadJpaRepository extends JpaRepository<DisponibilidadJpaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DisponibilidadJpaEntity d WHERE d.id = :id")
    Optional<DisponibilidadJpaEntity> findByIdForUpdate(UUID id);

    @Query("SELECT d FROM DisponibilidadJpaEntity d WHERE d.medicoId = :medicoId AND d.fecha = :fecha AND d.estado = 'DISPONIBLE'")
    List<DisponibilidadJpaEntity> findDisponiblesByMedicoAndFecha(UUID medicoId, LocalDate fecha);
}
