package com.example.citassaludservice.domain.model;

import java.util.UUID;

public class Medico {
    private final UUID id;
    private final String nombre;
    private final Especialidad especialidad;

    public Medico(UUID id, String nombre, Especialidad especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public Especialidad getEspecialidad() { return especialidad; }
}
