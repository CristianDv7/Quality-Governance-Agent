package com.example.citassaludservice.domain.port.out;

import com.example.citassaludservice.domain.model.Cita;
import com.example.citassaludservice.domain.model.EstadoCita;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CitaRepository {
    Cita save(Cita cita);
    Optional<Cita> findById(UUID id);
    List<Cita> findByPacienteId(UUID pacienteId);
    List<Cita> findByPacienteIdAndEstado(UUID pacienteId, EstadoCita estado);
}
