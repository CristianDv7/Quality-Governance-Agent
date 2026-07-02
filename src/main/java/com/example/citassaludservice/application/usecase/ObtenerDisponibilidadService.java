package com.example.citassaludservice.application.usecase;

import com.example.citassaludservice.domain.model.DisponibilidadMedico;
import com.example.citassaludservice.domain.port.in.ObtenerDisponibilidadUseCase;
import com.example.citassaludservice.domain.port.out.DisponibilidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ObtenerDisponibilidadService implements ObtenerDisponibilidadUseCase {

    private final DisponibilidadRepository disponibilidadRepository;

    public ObtenerDisponibilidadService(DisponibilidadRepository disponibilidadRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
    }

    @Override
    public List<DisponibilidadMedico> obtener(UUID medicoId, LocalDate fecha) {
        return disponibilidadRepository.findDisponiblesByMedicoAndFecha(medicoId, fecha);
    }
}
