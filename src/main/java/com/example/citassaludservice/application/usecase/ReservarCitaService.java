package com.example.citassaludservice.application.usecase;

import com.example.citassaludservice.domain.exception.RecursoNoEncontradoException;
import com.example.citassaludservice.domain.model.Cita;
import com.example.citassaludservice.domain.model.DisponibilidadMedico;
import com.example.citassaludservice.domain.model.EstadoCita;
import com.example.citassaludservice.domain.model.Paciente;
import com.example.citassaludservice.domain.port.in.ReservarCitaUseCase;
import com.example.citassaludservice.domain.port.out.CitaRepository;
import com.example.citassaludservice.domain.port.out.DisponibilidadRepository;
import com.example.citassaludservice.domain.port.out.NotificacionWhatsAppPort;
import com.example.citassaludservice.domain.port.out.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class ReservarCitaService implements ReservarCitaUseCase {

    private final DisponibilidadRepository disponibilidadRepository;
    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;
    private final NotificacionWhatsAppPort notificacionPort;

    public ReservarCitaService(DisponibilidadRepository disponibilidadRepository,
                               PacienteRepository pacienteRepository,
                               CitaRepository citaRepository,
                               NotificacionWhatsAppPort notificacionPort) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
        this.notificacionPort = notificacionPort;
    }

    @Override
    public Cita reservar(UUID pacienteId, UUID disponibilidadId) {
        DisponibilidadMedico disponibilidad = disponibilidadRepository
                .findByIdForUpdate(disponibilidadId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Franja horaria no encontrada: " + disponibilidadId));

        // Valida la transición DISPONIBLE→OCUPADA (lanza FranjaNoDisponibleException si ya está ocupada)
        disponibilidad.reservar();

        // Carga el paciente antes de persistir la cita: garantiza un 404 claro si no
        // existe, en lugar de una violación de FK mal etiquetada como conflicto.
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Paciente no encontrado: " + pacienteId));

        disponibilidadRepository.save(disponibilidad);

        Cita cita = new Cita(
                UUID.randomUUID(), pacienteId, disponibilidad.getMedicoId(), disponibilidadId,
                disponibilidad.getFecha(), disponibilidad.getHoraInicio(), disponibilidad.getHoraFin(),
                EstadoCita.CONFIRMADA, "ONLINE", Instant.now()
        );

        Cita saved = citaRepository.save(cita);

        // Notificación asíncrona con reintentos: no afecta la reserva ya confirmada.
        notificacionPort.enviar(saved, paciente.getTelefono());
        return saved;
    }
}
