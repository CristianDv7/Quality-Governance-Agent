# Feature Specification: US-01 · Reserva de Cita en Línea 24/7

**Feature Branch**: `001-reserva-cita-online`

**Epic**: E-01

**Story Points**: 8

**Created**: 2026-06-27

**Status**: Draft

**Input**: User description: "Como paciente, quiero reservar una cita en línea en cualquier momento del día, para no tener que llamar durante mi horario de almuerzo ni acumular intentos fallidos."

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 — Reserva exitosa fuera del horario telefónico (Priority: P1)

Un paciente accede al sistema de reservas en línea fuera del horario de atención
telefónica. Selecciona un médico, una fecha y una franja horaria disponibles, y
confirma la reserva. El sistema registra la cita y notifica al paciente por WhatsApp.

**Why this priority**: Es el flujo principal de valor de la funcionalidad. Sin éste,
la historia de usuario no entrega ningún beneficio.

**Independent Test**: Puede probarse de forma aislada creando un paciente de prueba,
configurando disponibilidad de médico y verificando que la cita queda persistida y
se emite la notificación.

**Acceptance Scenarios** *(BDD — mandatory per constitution Principle II)*:

1. **Dado** que el paciente accede al sistema fuera del horario de atención telefónica,
   **Cuando** elige médico, fecha y hora disponibles y confirma la reserva,
   **Entonces** la cita queda registrada en el sistema y el paciente recibe
   confirmación por WhatsApp con los detalles de la cita.

2. **Dado** que el paciente ha confirmado una cita,
   **Cuando** consulta su historial de citas,
   **Entonces** la nueva cita aparece con estado "Confirmada" y los datos completos
   del médico, fecha y hora.

---

### User Story 2 — Intento de reserva en franja no disponible (Priority: P2)

Un paciente intenta seleccionar una franja horaria que ya está ocupada por otra reserva.
El sistema le informa que esa franja no está disponible y lo guía para elegir otra.

**Why this priority**: Complementa el flujo principal evitando conflictos de agenda y
reduciendo la fricción del usuario ante un error de selección.

**Independent Test**: Puede probarse pre-cargando una franja como ocupada e intentando
reservarla; se verifica el mensaje de rechazo y que no se crea ningún registro duplicado.

**Acceptance Scenarios** *(BDD — mandatory per constitution Principle II)*:

1. **Dado** que la franja horaria seleccionada ya está ocupada por otro paciente,
   **Cuando** el paciente intenta confirmarla,
   **Entonces** el sistema muestra la franja como "No disponible" y presenta un
   mensaje que lo invita a elegir otra franja.

2. **Dado** que el paciente es informado de que la franja está ocupada,
   **Cuando** selecciona una franja alternativa disponible y confirma,
   **Entonces** la nueva cita queda registrada correctamente.

---

### Edge Cases

- ¿Qué ocurre si el médico elimina su disponibilidad mientras el paciente está
  en medio del flujo de reserva? El sistema debe detectar el conflicto al confirmar
  y rechazar la operación con un mensaje claro.
- ¿Qué ocurre si el servicio de notificaciones WhatsApp no está disponible en el
  momento de la confirmación? La cita debe quedar registrada de todas formas y el
  sistema debe reintentar la notificación de forma asíncrona (al menos 3 intentos).
- ¿Qué ocurre si el paciente envía la confirmación dos veces (doble clic / doble
  submit)? El sistema debe ser idempotente y crear únicamente una cita.
- ¿Qué sucede si no hay ninguna franja disponible para el médico seleccionado en el
  rango de fechas elegido? El sistema debe indicarlo y sugerir médicos alternativos
  o fechas distintas.

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE permitir al paciente buscar médicos disponibles
  filtrando por especialidad y fecha. La selección de la franja horaria específica
  se realiza en un paso posterior, consultando la disponibilidad del médico elegido
  (ver FR-002).
- **FR-002**: El sistema DEBE mostrar únicamente las franjas horarias disponibles
  para el médico y fecha seleccionados.
- **FR-003**: El sistema DEBE reservar la franja horaria de forma atómica al
  confirmar, evitando doble reserva concurrente.
- **FR-004**: El sistema DEBE registrar la cita con estado "Confirmada" una vez
  completada la reserva.
- **FR-005**: El sistema DEBE enviar una notificación de confirmación al paciente
  por WhatsApp con médico, fecha, hora y número de cita.
- **FR-006**: El sistema DEBE marcar una franja como "No disponible" e impedir su
  selección cuando ya está ocupada.
- **FR-007**: El sistema DEBE presentar un mensaje orientativo al paciente cuando
  intenta reservar una franja no disponible, guiándolo hacia franjas alternativas.
- **FR-008**: El sistema DEBE estar disponible para recibir reservas las 24 horas
  del día, los 7 días de la semana.
- **FR-009**: El sistema DEBE reintentar el envío de la notificación WhatsApp de
  forma asíncrona ante fallos del servicio de mensajería (mínimo 3 intentos).
- **FR-010**: El sistema DEBE ser idempotente ante envíos duplicados de confirmación
  de reserva (mismo paciente, mismo médico, misma franja).

### Key Entities

- **Paciente**: Persona que solicita la cita. Atributos clave: identificador único,
  número de teléfono (para WhatsApp), nombre completo.
- **Médico**: Profesional de salud que atiende la cita. Atributos clave:
  identificador único, nombre, especialidad.
- **DisponibilidadMedico**: Franjas horarias que el médico tiene habilitadas para
  atención. Atributos: médico, fecha, hora inicio, hora fin, estado
  (disponible / ocupada).
- **Cita**: Registro de la reserva. Atributos: paciente, médico, fecha, hora,
  estado (Confirmada / Cancelada / Completada), canal de creación (online).
- **NotificacionWhatsApp**: Registro del intento de notificación. Atributos: cita,
  estado envío, número de intentos, timestamp último intento.

---

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Un paciente puede completar el proceso de reserva (desde búsqueda hasta
  confirmación) en menos de 3 minutos en condiciones normales de uso.
- **SC-002**: El sistema rechaza el 100% de los intentos de reserva sobre franjas ya
  ocupadas sin generar registros duplicados.
- **SC-003**: Al menos el 95% de las notificaciones de confirmación llegan al paciente
  por WhatsApp en menos de 60 segundos desde la confirmación de la cita.
- **SC-004**: El sistema permanece disponible para recibir reservas el 99,5% del
  tiempo (medido mensualmente), incluyendo fuera del horario telefónico.
- **SC-005**: La tasa de reservas completadas exitosamente (sin abandono durante el
  flujo) es ≥ 80% en las primeras 4 semanas de operación.

---

## Assumptions

- Los pacientes ya tienen una cuenta activa en el sistema o existe un flujo de
  registro/identificación previo fuera del alcance de esta historia.
- El catálogo de médicos y sus especialidades está precargado en el sistema por
  personal administrativo; la gestión de disponibilidades del médico queda fuera
  del alcance de esta historia.
- El canal de notificación principal es WhatsApp; correo electrónico u otros canales
  son considerados fuera del alcance de esta historia (v1).
- La integración con el proveedor de WhatsApp (p. ej. Twilio, Meta Business API) ya
  existe o se implementará como parte de la capa de infraestructura en la historia
  técnica correspondiente.
- El tiempo máximo de retención de citas canceladas o expiradas en el sistema seguirá
  las políticas de datos de la institución de salud (a definir en historia de
  administración de datos).
- El sistema opera bajo una zona horaria fija configurada por la institución; la
  gestión de múltiples zonas horarias queda fuera del alcance de v1.
