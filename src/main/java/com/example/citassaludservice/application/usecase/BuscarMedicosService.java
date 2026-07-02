package com.example.citassaludservice.application.usecase;

import com.example.citassaludservice.domain.model.Especialidad;
import com.example.citassaludservice.domain.model.Medico;
import com.example.citassaludservice.domain.port.in.BuscarMedicosUseCase;
import com.example.citassaludservice.domain.port.out.MedicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BuscarMedicosService implements BuscarMedicosUseCase {

    private final MedicoRepository medicoRepository;

    public BuscarMedicosService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @Override
    public List<Medico> buscar(Especialidad especialidad, LocalDate fecha) {
        if (especialidad != null) {
            return medicoRepository.findByEspecialidadAndFecha(especialidad, fecha);
        }
        return medicoRepository.findAll();
    }
}
