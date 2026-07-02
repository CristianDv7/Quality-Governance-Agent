package com.example.citassaludservice.domain.port.out;

import com.example.citassaludservice.domain.model.Paciente;

import java.util.Optional;
import java.util.UUID;

public interface PacienteRepository {
    Optional<Paciente> findById(UUID id);
}
