package com.example.citassaludservice.infrastructure.persistence.entity;

import com.example.citassaludservice.domain.model.Especialidad;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "medico")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MedicoJpaEntity {
    @Id
    private UUID id;
    private String nombre;
    @Enumerated(EnumType.STRING)
    @Column(name = "especialidad")
    private Especialidad especialidad;
}
