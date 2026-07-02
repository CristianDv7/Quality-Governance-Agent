package com.example.citassaludservice.interface_.rest;

import com.example.citassaludservice.domain.model.Cita;
import com.example.citassaludservice.domain.port.in.ListarCitasPacienteUseCase;
import com.example.citassaludservice.domain.port.in.ReservarCitaUseCase;
import com.example.citassaludservice.interface_.rest.api.CitasApiDelegate;
import com.example.citassaludservice.interface_.rest.dto.CitaResponse;
import com.example.citassaludservice.interface_.rest.dto.CrearCitaRequest;
import com.example.citassaludservice.interface_.rest.dto.EstadoCita;
import com.example.citassaludservice.interface_.rest.mapper.ApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CitaController implements CitasApiDelegate {

    private final ReservarCitaUseCase reservarCitaUseCase;
    private final ListarCitasPacienteUseCase listarCitasUseCase;
    private final ApiMapper apiMapper;

    public CitaController(ReservarCitaUseCase reservarCitaUseCase,
                          ListarCitasPacienteUseCase listarCitasUseCase,
                          ApiMapper apiMapper) {
        this.reservarCitaUseCase = reservarCitaUseCase;
        this.listarCitasUseCase = listarCitasUseCase;
        this.apiMapper = apiMapper;
    }

    @Override
    public ResponseEntity<CitaResponse> crearCita(CrearCitaRequest crearCitaRequest) {
        Cita cita = reservarCitaUseCase.reservar(
                crearCitaRequest.getPacienteId(),
                crearCitaRequest.getDisponibilidadId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiMapper.toResponse(cita));
    }

    @Override
    public ResponseEntity<List<CitaResponse>> listarCitasPaciente(UUID pacienteId, EstadoCita estado) {
        com.example.citassaludservice.domain.model.EstadoCita domainEstado =
                estado != null ? com.example.citassaludservice.domain.model.EstadoCita.valueOf(estado.name()) : null;
        List<Cita> citas = listarCitasUseCase.listar(pacienteId, domainEstado);
        return ResponseEntity.ok(apiMapper.toResponseList(citas));
    }

    @Override
    public ResponseEntity<CitaResponse> obtenerCita(UUID citaId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
