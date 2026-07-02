package com.example.citassaludservice.domain.port.in;

import com.example.citassaludservice.domain.model.Especialidad;
import com.example.citassaludservice.domain.model.Medico;

import java.time.LocalDate;
import java.util.List;

public interface BuscarMedicosUseCase {
    List<Medico> buscar(Especialidad especialidad, LocalDate fecha);
}
