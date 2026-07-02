package com.example.citassaludservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "disponibilidad_medico")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DisponibilidadJpaEntity {
    @Id
    private UUID id;
    @Column(name = "medico_id")
    private UUID medicoId;
    private LocalDate fecha;
    @Column(name = "hora_inicio")
    private LocalTime horaInicio;
    @Column(name = "hora_fin")
    private LocalTime horaFin;
    private String estado;
}
