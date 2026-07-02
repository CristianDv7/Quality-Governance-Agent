package com.example.citassaludservice.domain.port.in;

import com.example.citassaludservice.domain.model.Cita;
import com.example.citassaludservice.domain.model.EstadoCita;

import java.util.List;
import java.util.UUID;

public interface ListarCitasPacienteUseCase {
    List<Cita> listar(UUID pacienteId, EstadoCita estado);
}
