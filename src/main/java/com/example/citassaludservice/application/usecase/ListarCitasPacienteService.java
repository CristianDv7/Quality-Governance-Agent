package com.example.citassaludservice.application.usecase;

import com.example.citassaludservice.domain.model.Cita;
import com.example.citassaludservice.domain.model.EstadoCita;
import com.example.citassaludservice.domain.port.in.ListarCitasPacienteUseCase;
import com.example.citassaludservice.domain.port.out.CitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ListarCitasPacienteService implements ListarCitasPacienteUseCase {

    private final CitaRepository citaRepository;

    public ListarCitasPacienteService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public List<Cita> listar(UUID pacienteId, EstadoCita estado) {
        if (estado != null) {
            return citaRepository.findByPacienteIdAndEstado(pacienteId, estado);
        }
        return citaRepository.findByPacienteId(pacienteId);
    }
}
