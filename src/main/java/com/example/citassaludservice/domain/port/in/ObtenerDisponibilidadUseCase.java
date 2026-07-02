package com.example.citassaludservice.domain.port.in;

import com.example.citassaludservice.domain.model.DisponibilidadMedico;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ObtenerDisponibilidadUseCase {
    List<DisponibilidadMedico> obtener(UUID medicoId, LocalDate fecha);
}
