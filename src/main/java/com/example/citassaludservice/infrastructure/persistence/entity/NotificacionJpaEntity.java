package com.example.citassaludservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notificacion_whatsapp")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NotificacionJpaEntity {
    @Id
    private UUID id;
    @Column(name = "cita_id")
    private UUID citaId;
    private String estado;
    private int intentos;
    @Column(name = "ultimo_intento")
    private Instant ultimoIntento;
    private String error;
}
