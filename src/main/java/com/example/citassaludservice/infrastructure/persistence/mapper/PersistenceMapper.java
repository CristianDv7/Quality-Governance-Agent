package com.example.citassaludservice.infrastructure.persistence.mapper;

import com.example.citassaludservice.domain.model.*;
import com.example.citassaludservice.infrastructure.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class PersistenceMapper {

    public DisponibilidadMedico toDomain(DisponibilidadJpaEntity e) {
        return new DisponibilidadMedico(
                e.getId(), e.getMedicoId(), e.getFecha(),
                e.getHoraInicio(), e.getHoraFin(),
                EstadoDisponibilidad.valueOf(e.getEstado())
        );
    }

    public DisponibilidadJpaEntity toEntity(DisponibilidadMedico d) {
        return new DisponibilidadJpaEntity(
                d.getId(), d.getMedicoId(), d.getFecha(),
                d.getHoraInicio(), d.getHoraFin(),
                d.getEstado().name()
        );
    }

    public Cita toDomain(CitaJpaEntity e) {
        return new Cita(
                e.getId(), e.getPacienteId(), e.getMedicoId(), e.getDisponibilidadId(),
                e.getFecha(), e.getHoraInicio(), e.getHoraFin(),
                EstadoCita.valueOf(e.getEstado()), e.getCanalCreacion(), e.getCreadaEn()
        );
    }

    public CitaJpaEntity toEntity(Cita c) {
        return new CitaJpaEntity(
                c.getId(), c.getPacienteId(), c.getMedicoId(), c.getDisponibilidadId(),
                c.getFecha(), c.getHoraInicio(), c.getHoraFin(),
                c.getEstado().name(), c.getCanalCreacion(), c.getCreadaEn()
        );
    }

    public Medico toDomain(MedicoJpaEntity e) {
        return new Medico(e.getId(), e.getNombre(), e.getEspecialidad());
    }

    public Paciente toDomain(PacienteJpaEntity e) {
        return new Paciente(e.getId(), e.getNombre(), e.getTelefono());
    }
}
