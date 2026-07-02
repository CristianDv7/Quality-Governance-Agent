package com.example.citassaludservice.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Cita {
    private final UUID id;
    private final UUID pacienteId;
    private final UUID medicoId;
    private final UUID disponibilidadId;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private EstadoCita estado;
    private final String canalCreacion;
    private final Instant creadaEn;

    public Cita(UUID id, UUID pacienteId, UUID medicoId, UUID disponibilidadId,
                LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                EstadoCita estado, String canalCreacion, Instant creadaEn) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.disponibilidadId = disponibilidadId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.canalCreacion = canalCreacion;
        this.creadaEn = creadaEn;
    }

    public UUID getId() { return id; }
    public UUID getPacienteId() { return pacienteId; }
    public UUID getMedicoId() { return medicoId; }
    public UUID getDisponibilidadId() { return disponibilidadId; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public EstadoCita getEstado() { return estado; }
    public String getCanalCreacion() { return canalCreacion; }
    public Instant getCreadaEn() { return creadaEn; }
}
