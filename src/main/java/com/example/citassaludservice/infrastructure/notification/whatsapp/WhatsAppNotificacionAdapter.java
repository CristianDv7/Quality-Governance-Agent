package com.example.citassaludservice.infrastructure.notification.whatsapp;

import com.example.citassaludservice.domain.model.Cita;
import com.example.citassaludservice.domain.port.out.NotificacionWhatsAppPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Adaptador stub v1 de notificación WhatsApp.
 *
 * <p>El envío es asíncrono ({@link Async}) para que un fallo de mensajería nunca
 * haga rollback de la reserva ya confirmada (FR-005). Ante fallos del proveedor se
 * reintenta hasta 3 veces con back-off exponencial ({@link Retryable}, FR-009); si
 * se agotan los intentos, {@link #recuperar} registra el fallo definitivo sin
 * propagar la excepción.
 */
@Component
public class WhatsAppNotificacionAdapter implements NotificacionWhatsAppPort {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificacionAdapter.class);

    @Override
    @Async
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void enviar(Cita cita, String telefono) {
        log.info("[WhatsApp-stub] Enviando confirmación al {} → cita {}, médico {}, {} {}-{}",
                telefono, cita.getId(), cita.getMedicoId(),
                cita.getFecha(), cita.getHoraInicio(), cita.getHoraFin());
    }

    /**
     * Se invoca cuando se agotan los reintentos. Registra el fallo definitivo
     * (la notificación queda como FALLIDA) sin propagar la excepción, para no
     * afectar la reserva ya confirmada.
     */
    @Recover
    public void recuperar(Exception ex, Cita cita, String telefono) {
        log.error("[WhatsApp-stub] Notificación FALLIDA tras agotar reintentos para cita {} ({}): {}",
                cita.getId(), telefono, ex.getMessage());
    }
}
