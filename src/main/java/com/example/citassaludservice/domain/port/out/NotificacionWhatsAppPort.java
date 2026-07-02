package com.example.citassaludservice.domain.port.out;

import com.example.citassaludservice.domain.model.Cita;

public interface NotificacionWhatsAppPort {

    /**
     * Envía la notificación de confirmación de la cita al teléfono del paciente.
     * La implementación DEBE ser asíncrona y con reintentos: la cita ya quedó
     * registrada antes de invocar este método, por lo que un fallo de mensajería
     * no debe afectar la reserva (FR-005, FR-009).
     *
     * @param cita     cita confirmada (médico, fecha, hora, número de cita)
     * @param telefono número del paciente en formato E.164 (destino WhatsApp)
     */
    void enviar(Cita cita, String telefono);
}
