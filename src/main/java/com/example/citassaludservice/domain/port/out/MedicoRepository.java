package com.example.citassaludservice.domain.port.out;

import com.example.citassaludservice.domain.model.Especialidad;
import com.example.citassaludservice.domain.model.Medico;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicoRepository {
    List<Medico> findByEspecialidadAndFecha(Especialidad especialidad, LocalDate fecha);
    List<Medico> findAll();
    Optional<Medico> findById(UUID id);
}
