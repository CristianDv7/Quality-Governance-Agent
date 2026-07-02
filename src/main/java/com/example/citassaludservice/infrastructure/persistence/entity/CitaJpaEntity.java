package com.example.citassaludservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "cita",
       uniqueConstraints = @UniqueConstraint(columnNames = {"paciente_id", "disponibilidad_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CitaJpaEntity {
    @Id
    private UUID id;
    @Column(name = "paciente_id")
    private UUID pacienteId;
    @Column(name = "medico_id")
    private UUID medicoId;
    @Column(name = "disponibilidad_id")
    private UUID disponibilidadId;
    private LocalDate fecha;
    @Column(name = "hora_inicio")
    private LocalTime horaInicio;
    @Column(name = "hora_fin")
    private LocalTime horaFin;
    private String estado;
    @Column(name = "canal_creacion")
    private String canalCreacion;
    @Column(name = "creada_en")
    private Instant creadaEn;
}
