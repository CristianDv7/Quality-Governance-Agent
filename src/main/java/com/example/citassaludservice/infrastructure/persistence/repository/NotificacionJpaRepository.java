package com.example.citassaludservice.infrastructure.persistence.repository;

import com.example.citassaludservice.infrastructure.persistence.entity.NotificacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificacionJpaRepository extends JpaRepository<NotificacionJpaEntity, UUID> {
}
