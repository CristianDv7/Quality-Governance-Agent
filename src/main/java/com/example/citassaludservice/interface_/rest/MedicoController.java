package com.example.citassaludservice.interface_.rest;

import com.example.citassaludservice.domain.model.DisponibilidadMedico;
import com.example.citassaludservice.domain.model.Medico;
import com.example.citassaludservice.domain.port.in.BuscarMedicosUseCase;
import com.example.citassaludservice.domain.port.in.ObtenerDisponibilidadUseCase;
import com.example.citassaludservice.interface_.rest.api.MedicosApiDelegate;
import com.example.citassaludservice.interface_.rest.dto.Especialidad;
import com.example.citassaludservice.interface_.rest.dto.FranjaHoraria;
import com.example.citassaludservice.interface_.rest.dto.MedicoResumen;
import com.example.citassaludservice.interface_.rest.mapper.ApiMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class MedicoController implements MedicosApiDelegate {

    private final BuscarMedicosUseCase buscarMedicosUseCase;
    private final ObtenerDisponibilidadUseCase obtenerDisponibilidadUseCase;
    private final ApiMapper apiMapper;

    public MedicoController(BuscarMedicosUseCase buscarMedicosUseCase,
                            ObtenerDisponibilidadUseCase obtenerDisponibilidadUseCase,
                            ApiMapper apiMapper) {
        this.buscarMedicosUseCase = buscarMedicosUseCase;
        this.obtenerDisponibilidadUseCase = obtenerDisponibilidadUseCase;
        this.apiMapper = apiMapper;
    }

    @Override
    public ResponseEntity<List<MedicoResumen>> buscarMedicos(Especialidad especialidad, LocalDate fecha) {
        com.example.citassaludservice.domain.model.Especialidad domainEspecialidad =
                especialidad != null
                        ? com.example.citassaludservice.domain.model.Especialidad.valueOf(especialidad.name())
                        : null;
        List<Medico> medicos = buscarMedicosUseCase.buscar(domainEspecialidad, fecha);
        return ResponseEntity.ok(medicos.stream().map(apiMapper::toResumen).toList());
    }

    @Override
    public ResponseEntity<List<FranjaHoraria>> obtenerDisponibilidadMedico(UUID medicoId, LocalDate fecha) {
        List<DisponibilidadMedico> franjas = obtenerDisponibilidadUseCase.obtener(medicoId, fecha);
        return ResponseEntity.ok(franjas.stream().map(apiMapper::toFranja).toList());
    }
}
