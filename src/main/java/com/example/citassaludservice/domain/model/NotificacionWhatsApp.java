package com.example.citassaludservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public class NotificacionWhatsApp {
    private final UUID id;
    private final UUID citaId;
    private EstadoNotificacion estado;
    private int intentos;
    private Instant ultimoIntento;
    private String error;

    public NotificacionWhatsApp(UUID id, UUID citaId) {
        this.id = id;
        this.citaId = citaId;
        this.estado = EstadoNotificacion.PENDIENTE;
        this.intentos = 0;
    }

    public UUID getId() { return id; }
    public UUID getCitaId() { return citaId; }
    public EstadoNotificacion getEstado() { return estado; }
    public int getIntentos() { return intentos; }
    public Instant getUltimoIntento() { return ultimoIntento; }
    public String getError() { return error; }

    public void registrarEnvio() {
        this.estado = EstadoNotificacion.ENVIADA;
        this.intentos++;
        this.ultimoIntento = Instant.now();
    }

    public void registrarFallo(String mensajeError) {
        this.intentos++;
        this.ultimoIntento = Instant.now();
        this.error = mensajeError;
        if (this.intentos >= 3) {
            this.estado = EstadoNotificacion.FALLIDA;
        }
    }
}
