package com.example.citassaludservice.interface_.rest.mapper;

import com.example.citassaludservice.domain.model.Cita;
import com.example.citassaludservice.domain.model.DisponibilidadMedico;
import com.example.citassaludservice.domain.model.Medico;
import com.example.citassaludservice.interface_.rest.dto.CitaResponse;
import com.example.citassaludservice.interface_.rest.dto.Especialidad;
import com.example.citassaludservice.interface_.rest.dto.EstadoCita;
import com.example.citassaludservice.interface_.rest.dto.EstadoDisponibilidad;
import com.example.citassaludservice.interface_.rest.dto.FranjaHoraria;
import com.example.citassaludservice.interface_.rest.dto.MedicoResumen;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

@Component
public class ApiMapper {

    public CitaResponse toResponse(Cita cita) {
        CitaResponse r = new CitaResponse();
        r.setId(cita.getId());
        r.setPacienteId(cita.getPacienteId());
        r.setMedicoId(cita.getMedicoId());
        r.setFecha(cita.getFecha());
        r.setHoraInicio(cita.getHoraInicio() != null ? cita.getHoraInicio().toString() : null);
        r.setHoraFin(cita.getHoraFin() != null ? cita.getHoraFin().toString() : null);
        r.setEstado(EstadoCita.valueOf(cita.getEstado().name()));
        r.setCanalCreacion(cita.getCanalCreacion());
        r.setCreadaEn(cita.getCreadaEn().atOffset(ZoneOffset.UTC));
        return r;
    }

    public List<CitaResponse> toResponseList(List<Cita> citas) {
        return citas.stream().map(this::toResponse).toList();
    }

    public MedicoResumen toResumen(Medico medico) {
        MedicoResumen r = new MedicoResumen();
        r.setId(medico.getId());
        r.setNombre(medico.getNombre());
        r.setEspecialidad(Especialidad.valueOf(medico.getEspecialidad().name()));
        return r;
    }

    public FranjaHoraria toFranja(DisponibilidadMedico d) {
        FranjaHoraria f = new FranjaHoraria();
        f.setId(d.getId());
        f.setMedicoId(d.getMedicoId());
        f.setFecha(d.getFecha());
        f.setHoraInicio(d.getHoraInicio() != null ? d.getHoraInicio().toString() : null);
        f.setHoraFin(d.getHoraFin() != null ? d.getHoraFin().toString() : null);
        f.setEstado(EstadoDisponibilidad.valueOf(d.getEstado().name()));
        return f;
    }
}
