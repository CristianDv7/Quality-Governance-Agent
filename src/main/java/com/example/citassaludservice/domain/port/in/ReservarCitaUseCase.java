package com.example.citassaludservice.domain.port.in;

import com.example.citassaludservice.domain.model.Cita;

import java.util.UUID;

public interface ReservarCitaUseCase {
    Cita reservar(UUID pacienteId, UUID disponibilidadId);
}
