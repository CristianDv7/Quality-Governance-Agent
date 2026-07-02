package com.example.citassaludservice.domain.model;

import java.util.UUID;

public class Paciente {
    private final UUID id;
    private final String nombre;
    private final String telefono;

    public Paciente(UUID id, String nombre, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
}
