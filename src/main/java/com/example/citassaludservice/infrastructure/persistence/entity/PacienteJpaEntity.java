package com.example.citassaludservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "paciente")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PacienteJpaEntity {
    @Id
    private UUID id;
    private String nombre;
    private String telefono;
}
