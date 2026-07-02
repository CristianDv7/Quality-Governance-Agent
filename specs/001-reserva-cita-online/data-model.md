# Data Model: US-01 · Reserva de Cita en Línea 24/7

**Feature**: 001-reserva-cita-online
**Date**: 2026-06-27

---

## Entities (Domain Layer — `com.example.citassaludservice.domain.model`)

### Paciente

Persona que solicita una cita médica.

| Campo | Tipo | Restricciones |
|-------|------|--------------|
| `id` | UUID | NOT NULL, immutable |
| `nombre` | String | NOT NULL, 2–100 chars |
| `telefono` | String | NOT NULL, formato E.164 (ej. +573001234567) |

**Invariants**: `telefono` MUST match `^\+[1-9]\d{7,14}$`. No se almacenan más datos
que los necesarios para la notificación (minimización de datos — YAGNI).

---

### Medico

Profesional de salud que presta el servicio de atención.

| Campo | Tipo | Restricciones |
|-------|------|--------------|
| `id` | UUID | NOT NULL, immutable |
| `nombre` | String | NOT NULL, 2–100 chars |
| `especialidad` | Especialidad (enum) | NOT NULL |

**Enum `Especialidad`**: `MEDICINA_GENERAL`, `PEDIATRIA`, `CARDIOLOGIA`,
`DERMATOLOGIA`, `GINECOLOGIA`, `ORTOPEDIA` (extensible vía nueva historia).

---

### DisponibilidadMedico

Franja horaria que un médico tiene habilitada para atención.

| Campo | Tipo | Restricciones |
|-------|------|--------------|
| `id` | UUID | NOT NULL, immutable |
| `medicoId` | UUID | NOT NULL, FK → Medico |
| `fecha` | LocalDate | NOT NULL |
| `horaInicio` | LocalTime | NOT NULL |
| `horaFin` | LocalTime | NOT NULL |
| `estado` | EstadoDisponibilidad (enum) | NOT NULL, default `DISPONIBLE` |

**Enum `EstadoDisponibilidad`**: `DISPONIBLE`, `OCUPADA`.

**Invariants**:
- `horaFin` MUST be strictly after `horaInicio`.
- Transition `DISPONIBLE → OCUPADA` is the only valid state transition.
- Transition MUST be atomic (pessimistic lock on `SELECT FOR UPDATE`).

---

### Cita

Registro de una reserva de atención médica confirmada.

| Campo | Tipo | Restricciones |
|-------|------|--------------|
| `id` | UUID | NOT NULL, immutable |
| `pacienteId` | UUID | NOT NULL, FK → Paciente |
| `medicoId` | UUID | NOT NULL, FK → Medico |
| `disponibilidadId` | UUID | NOT NULL, FK → DisponibilidadMedico |
| `fecha` | LocalDate | NOT NULL (denormalizado desde disponibilidad para queries) |
| `horaInicio` | LocalTime | NOT NULL (denormalizado) |
| `horaFin` | LocalTime | NOT NULL (denormalizado) |
| `estado` | EstadoCita (enum) | NOT NULL, default `CONFIRMADA` |
| `canalCreacion` | String | NOT NULL, `ONLINE` |
| `creadaEn` | Instant | NOT NULL, set at creation |

**Enum `EstadoCita`**: `CONFIRMADA`, `CANCELADA`, `COMPLETADA`.

**Idempotency key**: `(pacienteId, disponibilidadId)` — UNIQUE constraint prevents
double-booking by same patient in the same slot.

---

### NotificacionWhatsApp

Registro del ciclo de vida del envío de notificación al paciente.

| Campo | Tipo | Restricciones |
|-------|------|--------------|
| `id` | UUID | NOT NULL, immutable |
| `citaId` | UUID | NOT NULL, FK → Cita |
| `estado` | EstadoNotificacion (enum) | NOT NULL |
| `intentos` | int | NOT NULL, default 0, ≥ 0 |
| `ultimoIntento` | Instant | nullable |
| `error` | String | nullable, mensaje del último fallo |

**Enum `EstadoNotificacion`**: `PENDIENTE`, `ENVIADA`, `FALLIDA`.

---

## Relationships

```
Paciente ────< Cita >──── Medico
                │
         DisponibilidadMedico
                │
         NotificacionWhatsApp
```

- A `Paciente` can have many `Cita`.
- A `Medico` can have many `DisponibilidadMedico` entries.
- Each `Cita` references exactly one `DisponibilidadMedico`.
- Each `Cita` has exactly one `NotificacionWhatsApp`.

---

## State Transitions

### DisponibilidadMedico

```
DISPONIBLE ──[reservar()]──> OCUPADA
```

### Cita

```
CONFIRMADA ──[cancelar()]──> CANCELADA
CONFIRMADA ──[completar()]──> COMPLETADA
```

### NotificacionWhatsApp

```
PENDIENTE ──[enviar() OK]──> ENVIADA
PENDIENTE ──[enviar() FAIL, intentos < 3]──> PENDIENTE (reintento programado)
PENDIENTE ──[enviar() FAIL, intentos = 3]──> FALLIDA
```

---

## JPA Mapping Notes (Infrastructure Layer)

- Each domain entity maps 1:1 to a JPA `@Entity` in
  `com.example.citassaludservice.infrastructure.persistence.entity`.
- UUIDs generated with `@GeneratedValue(strategy = GenerationType.UUID)`.
- `DisponibilidadMedico` uses `@Lock(LockModeType.PESSIMISTIC_WRITE)` on the
  `findById` query used during reservation.
- `Cita` table has a `UNIQUE` constraint on `(paciente_id, disponibilidad_id)`.
- Enum columns stored as `STRING` (`@Enumerated(EnumType.STRING)`).
- Domain model classes are pure Java — no JPA annotations in domain layer.
  Infrastructure entity classes are separate and mapped by a `PersistenceMapper`.
