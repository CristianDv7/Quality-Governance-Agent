package com.example.citassaludservice.domain.model;

import com.example.citassaludservice.domain.exception.FranjaNoDisponibleException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class DisponibilidadMedico {
    private final UUID id;
    private final UUID medicoId;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private EstadoDisponibilidad estado;

    public DisponibilidadMedico(UUID id, UUID medicoId, LocalDate fecha,
                                LocalTime horaInicio, LocalTime horaFin,
                                EstadoDisponibilidad estado) {
        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException("horaFin must be after horaInicio");
        }
        this.id = id;
        this.medicoId = medicoId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
    }

    public void reservar() {
        if (estado != EstadoDisponibilidad.DISPONIBLE) {
            throw new FranjaNoDisponibleException(
                    "La franja horaria seleccionada ya está ocupada. Por favor, elige otra franja.");
        }
        this.estado = EstadoDisponibilidad.OCUPADA;
    }

    public UUID getId() { return id; }
    public UUID getMedicoId() { return medicoId; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public EstadoDisponibilidad getEstado() { return estado; }
}
