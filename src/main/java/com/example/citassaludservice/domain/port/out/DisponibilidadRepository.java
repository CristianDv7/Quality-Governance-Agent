package com.example.citassaludservice.domain.port.out;

import com.example.citassaludservice.domain.model.DisponibilidadMedico;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisponibilidadRepository {
    Optional<DisponibilidadMedico> findByIdForUpdate(UUID id);
    List<DisponibilidadMedico> findDisponiblesByMedicoAndFecha(UUID medicoId, LocalDate fecha);
    DisponibilidadMedico save(DisponibilidadMedico disponibilidad);
}
