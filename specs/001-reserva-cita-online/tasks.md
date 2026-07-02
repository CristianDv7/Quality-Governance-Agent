---
description: "Task list for US-01 · Reserva de Cita en Línea 24/7"
---

# Tasks: US-01 · Reserva de Cita en Línea 24/7

**Input**: Design documents from `/specs/001-reserva-cita-online/`

**Prerequisites**: plan.md ✅ | spec.md ✅ | research.md ✅ | data-model.md ✅ | contracts/openapi.yml ✅

**Tests**: BDD tests (unit, integración, funcionales) son OBLIGATORIOS por
constitución Principio II. Las pruebas DEBEN escribirse PRIMERO y DEBEN fallar
antes de implementar (Red → Green → Refactor).

**Base package**: `com.example.citassaludservice`
**Base src**: `src/main/java/com/example/citassaludservice/`
**Base test**: `src/test/java/com/example/citassaludservice/`

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Puede ejecutarse en paralelo (archivos distintos, sin dependencias)
- **[Story]**: Historia de usuario a la que pertenece ([US1], [US2])

---

## Phase 1: Setup (Infraestructura compartida)

**Purpose**: Inicialización del proyecto y estructura base

- [X] T001 Agregar dependencias en `build.gradle`: Cucumber-JVM 7.x
  (`cucumber-java`, `cucumber-junit-platform-engine`, `cucumber-spring`),
  JaCoCo plugin, `openapi-generator-gradle-plugin` 7.x, ArchUnit, spring-retry,
  springdoc-openapi-starter-webmvc-ui, `jackson-databind-nullable`,
  `jakarta.validation-api`
- [X] T002 [P] Copiar contrato `specs/001-reserva-cita-online/contracts/openapi.yml`
  a `src/main/resources/openapi/openapi.yml`
- [X] T003 [P] Crear estructura de paquetes Clean Architecture bajo
  `src/main/java/com/example/citassaludservice/`:
  `domain/model/`, `domain/port/in/`, `domain/port/out/`, `domain/exception/`,
  `application/usecase/`, `infrastructure/persistence/entity/`,
  `infrastructure/persistence/repository/`, `infrastructure/persistence/adapter/`,
  `infrastructure/persistence/mapper/`, `infrastructure/notification/whatsapp/`,
  `interface_/rest/controller/`, `interface_/rest/mapper/`

---

## Phase 2: Foundational (Prerequisitos bloqueantes)

**Purpose**: Infraestructura transversal que DEBE completarse antes de cualquier
historia de usuario.

**⚠️ CRITICAL**: Ninguna historia puede comenzar hasta que esta fase esté completa.

- [X] T004 Configurar `openapi-generator-gradle-plugin` en `build.gradle`:
  generador `spring`, estrategia `delegate`, paquete de salida
  `com.example.citassaludservice.interface_.rest`, output `build/generated/`;
  ejecutar `./gradlew openApiGenerate` y verificar que `MedicosApiDelegate` y
  `CitasApiDelegate` existen en `build/generated/`
- [X] T005 [P] Configurar JaCoCo en `build.gradle`: task
  `jacocoTestCoverageVerification` con límites `element: CLASS, minimum: 0.80` y
  `element: BUNDLE, minimum: 0.80`; excluir patrón `**/build/generated/**`
- [X] T006 [P] Configurar `src/main/resources/application.yaml`:
  datasource H2 (`jdbc:h2:mem:testdb`), `jpa.hibernate.ddl-auto: none`,
  `spring.sql.init.schema-locations: classpath:db/schema.sql`,
  `spring.sql.init.data-locations: classpath:db/data.sql`,
  `spring.sql.init.mode: always`, consola H2 habilitada en
  `spring.h2.console.enabled: true`
- [X] T007 [P] Crear `src/main/resources/db/schema.sql` con DDL completo:
  `CREATE TABLE IF NOT EXISTS medico (id UUID PRIMARY KEY, nombre VARCHAR, especialidad VARCHAR)`,
  `paciente (id UUID PRIMARY KEY, nombre VARCHAR, telefono VARCHAR)`,
  `disponibilidad_medico (id UUID PRIMARY KEY, medico_id UUID, fecha DATE, hora_inicio TIME, hora_fin TIME, estado VARCHAR, UNIQUE(medico_id, fecha, hora_inicio))`,
  `cita (id UUID PRIMARY KEY, paciente_id UUID, medico_id UUID, disponibilidad_id UUID, fecha DATE, hora_inicio TIME, hora_fin TIME, estado VARCHAR, canal_creacion VARCHAR, creada_en TIMESTAMP, UNIQUE(paciente_id, disponibilidad_id))`,
  `notificacion_whatsapp (id UUID PRIMARY KEY, cita_id UUID, estado VARCHAR, intentos INT DEFAULT 0, ultimo_intento TIMESTAMP, error VARCHAR)`
- [X] T008 [P] Crear `src/main/resources/db/data.sql` con datos representativos:
  3 médicos (MEDICINA_GENERAL, PEDIATRIA, CARDIOLOGIA con UUIDs fijos),
  2 pacientes con teléfono en formato E.164 (ej. `+573001234567`),
  20 franjas DISPONIBLE distribuidas en los próximos 7 días (bloques de 30 min,
  franjas 08:00–12:00 para 2 médicos, ~4 franjas/día × 5 días)
- [X] T009 [P] Crear dos archivos de fixtures en `src/test/resources/db/`:
  (a) `test-data-us1.sql`: 2–3 franjas adicionales en estado DISPONIBLE con UUIDs
  fijos, para que los tests de US1 solo carguen disponibilidades válidas sin
  interferencia de datos US2;
  (b) `test-data-us2.sql`: 1 franja en estado OCUPADA con UUID fijo conocido,
  referenciando un `medico_id` de `data.sql` — fixture exclusivo para US2.
  (Nota: no crear un único `test-data.sql`; la separación evita que tests de US1
  carguen accidentalmente franjas OCUPADA y obtengan resultados inesperados.)
- [X] T010 [P] Crear excepciones de dominio en `domain/exception/`:
  `FranjaNoDisponibleException.java`, `CitaDuplicadaException.java`,
  `RecursoNoEncontradoException.java` (RuntimeException, sin dependencias externas)
- [X] T011 [P] Crear test de arquitectura en
  `src/test/.../architecture/ArchitectureTest.java` con ArchUnit: verificar (a)
  ninguna clase de `domain` importa de `infrastructure` o `interface_`; (b)
  ninguna clase de `application` importa de `infrastructure` o `interface_`;
  (c) `infrastructure` no importa de `interface_`
- [X] T012 [P] Crear `src/test/.../bdd/CucumberRunner.java` anotado con `@Suite`,
  `@SelectClasspathResource("features")` y `@ConfigurationParameter(key =
  GLUE_PROPERTY_NAME, value = "com.example.citassaludservice.bdd.steps")`
- [X] T013 [P] Crear `src/test/resources/features/reserva-cita.feature` con los
  cuatro escenarios Gherkin de spec.md:
  Scenario 1: Reserva exitosa fuera del horario telefónico,
  Scenario 2: Verificación de cita en historial del paciente,
  Scenario 3: Intento de reserva en franja no disponible,
  Scenario 4: Reserva alternativa tras rechazo de franja ocupada

**Checkpoint**: Fundación lista — las historias de usuario pueden comenzar en paralelo

---

## Phase 3: User Story 1 — Reserva exitosa (Priority: P1) 🎯 MVP

**Goal**: El paciente selecciona médico, fecha y franja disponible, confirma la
reserva y recibe confirmación. La cita queda persistida con estado CONFIRMADA.

**Independent Test**: `./gradlew bootRun` → `POST /api/v1/citas` con datos de
`data.sql` → HTTP 201 + body con `estado: CONFIRMADA` + log de notificación
WhatsApp en consola.

### Tests para US1 — MANDATORY (BDD, Principio II)

> **DEBEN escribirse primero y DEBEN fallar antes de implementar**

- [X] T014 [P] [US1] Test unitario para `DisponibilidadMedico` (invariants:
  `horaFin > horaInicio`, transición `DISPONIBLE→OCUPADA` válida,
  otras transiciones lanzan excepción) en
  `src/test/.../domain/model/DisponibilidadMedicoTest.java`
- [X] T015 [P] [US1] Test unitario para `Cita` (creación con estado CONFIRMADA,
  campo `canalCreacion = ONLINE`, `creadaEn` no nulo) en
  `src/test/.../domain/model/CitaTest.java`
- [X] T016 [P] [US1] Test unitario para `ReservarCitaService` con puertos mockeados:
  flujo feliz (franja DISPONIBLE → reserva → notificación) en
  `src/test/.../application/usecase/ReservarCitaServiceTest.java`
- [X] T017 [P] [US1] Test de integración con H2 + `@Sql("classpath:db/test-data-us1.sql")`
  para `DisponibilidadRepositoryAdapter`: verificar lock pesimista y transición de
  estado DISPONIBLE→OCUPADA en
  `src/test/.../infrastructure/persistence/DisponibilidadRepositoryAdapterTest.java`
- [X] T018 [P] [US1] Step definitions Cucumber para Scenario 1 y 2 en
  `src/test/.../bdd/steps/ReservaCitaSteps.java`
- [X] T019 [US1] Test de integración MockMvc para `POST /api/v1/citas` flujo feliz
  (HTTP 201 + `CitaResponse.estado = CONFIRMADA`) en
  `src/test/.../interface_/rest/CitaControllerTest.java`
  (depende de T018 para datos de setup)
- [X] T051 [P] [US1] Test unitario para `WhatsAppNotificacionAdapter` (D1 — cobertura
  per-clase ≥ 80%): mockear el servicio subyacente de envío para que falle; verificar
  que `@Retryable` reintenta exactamente 3 veces y que el estado final de la
  `NotificacionWhatsApp` queda en `FALLIDA` al agotar intentos; en
  `src/test/.../infrastructure/notification/whatsapp/WhatsAppNotificacionAdapterTest.java`
- [X] T052 [P] [US1] Test unitario para `ReservarCitaService` escenario de
  disponibilidad eliminada (C1): mockear `DisponibilidadRepository.findById` para que
  devuelva `Optional.empty()` → verificar que se lanza `RecursoNoEncontradoException`
  y que ningún otro puerto es invocado; en
  `src/test/.../application/usecase/ReservarCitaServiceTest.java` (extender T016)

### Implementación de US1

- [X] T020 [P] [US1] Crear modelos de dominio puros (sin anotaciones JPA/Spring):
  `Paciente.java`, `Medico.java`, `Especialidad.java` (enum) en `domain/model/`
- [X] T021 [P] [US1] Crear modelos de dominio: `DisponibilidadMedico.java`,
  `EstadoDisponibilidad.java` (enum) con método `reservar()` que valida
  transición en `domain/model/`
- [X] T022 [US1] Crear modelos de dominio: `Cita.java`, `EstadoCita.java` (enum),
  `NotificacionWhatsApp.java`, `EstadoNotificacion.java` (enum) en `domain/model/`
  (depende de T020, T021)
- [X] T023 [P] [US1] Definir puertos de salida en `domain/port/out/`:
  `MedicoRepository.java`, `DisponibilidadRepository.java`, `CitaRepository.java`
- [X] T024 [P] [US1] Definir puerto de salida `NotificacionWhatsAppPort.java` en
  `domain/port/out/`
- [X] T025 [US1] Definir puertos de entrada en `domain/port/in/`:
  `ReservarCitaUseCase.java`, `ObtenerDisponibilidadUseCase.java`
  (depende de T022, T023)
- [X] T026 [P] [US1] Crear entidades JPA en `infrastructure/persistence/entity/`:
  `MedicoJpaEntity.java`, `DisponibilidadJpaEntity.java`, `CitaJpaEntity.java`
  con `@Entity`, `@Table`, `@Enumerated(EnumType.STRING)`, UUID PK vía
  `@GeneratedValue(strategy = GenerationType.UUID)`
- [X] T027 [P] [US1] Crear repositorios Spring Data JPA en
  `infrastructure/persistence/repository/`:
  `MedicoJpaRepository.java`,
  `DisponibilidadJpaRepository.java` (con `@Lock(PESSIMISTIC_WRITE)` en
  `findByIdForUpdate`),
  `CitaJpaRepository.java` (constraint UNIQUE `paciente_id + disponibilidad_id`)
- [X] T028 [US1] Crear `PersistenceMapper.java` en
  `infrastructure/persistence/mapper/` para mapear dominio ↔ JPA entity
  (depende de T020–T022, T026)
- [X] T029 [US1] Implementar `DisponibilidadRepositoryAdapter.java` y
  `CitaRepositoryAdapter.java` en `infrastructure/persistence/adapter/`
  implementando los puertos de salida de T023 (depende de T027, T028)
- [X] T030 [US1] Implementar `WhatsAppNotificacionAdapter.java` (stub v1: log del
  mensaje + `@Retryable(maxAttempts=3, backoff=@Backoff(delay=2000, multiplier=2))`)
  en `infrastructure/notification/whatsapp/` implementando `NotificacionWhatsAppPort`
  (depende de T024)
- [X] T031 [US1] Implementar `ReservarCitaService.java` en `application/usecase/`:
  (1) cargar disponibilidad con lock, (2) validar estado DISPONIBLE,
  (3) marcar OCUPADA, (4) persistir Cita, (5) disparar notificación async
  (depende de T025, T029, T030)
- [X] T032 [US1] Implementar `ObtenerDisponibilidadService.java` en
  `application/usecase/` filtrando solo franjas DISPONIBLE (depende de T025, T029)
- [X] T033 [US1] Crear `ApiMapper.java` en `interface_/rest/mapper/` para
  `CrearCitaRequest` → comando dominio y `Cita` → `CitaResponse`
  (depende de código generado por openapi-generator, T022)
- [X] T034 [US1] Implementar `CitaController.java` en `interface_/rest/controller/`
  implementando `CitasApiDelegate` generado: métodos `crearCita` y `obtenerCita`
  (depende de T031, T032, T033)

**Checkpoint**: US1 completamente funcional e independientemente testeable.
`./gradlew test --tests "*CitaControllerTest*" --tests "*CucumberRunner*"`

---

## Phase 4: User Story 2 — Franja no disponible (Priority: P2)

**Goal**: El sistema rechaza reservas sobre franjas OCUPADA con HTTP 409 y un
mensaje orientativo. El paciente puede reintentar con otra franja.

**Independent Test**: Usar franja OCUPADA de `test-data-us2.sql` (UUID conocido) →
`POST /api/v1/citas` → HTTP 409 + `{"codigo":"FRANJA_NO_DISPONIBLE", ...}` +
verificar que no existe ningún registro nuevo en tabla `cita`.

### Tests para US2 — MANDATORY (BDD, Principio II)

> **DEBEN escribirse primero y DEBEN fallar antes de implementar**

- [X] T035 [P] [US2] Test unitario en `ReservarCitaServiceTest.java` (extender T016):
  escenario franja OCUPADA → lanza `FranjaNoDisponibleException` con mensaje
  orientativo en `src/test/.../application/usecase/ReservarCitaServiceTest.java`
- [X] T036 [P] [US2] Step definitions Cucumber para Scenario 3 y 4 en
  `src/test/.../bdd/steps/DisponibilidadSteps.java`
  usando `@Sql("classpath:db/test-data-us2.sql")` para cargar la franja OCUPADA
- [X] T037 [US2] Test de integración MockMvc para `POST /api/v1/citas` con franja
  OCUPADA → HTTP 409 + body `ErrorResponse` con `codigo: FRANJA_NO_DISPONIBLE`
  en `src/test/.../interface_/rest/CitaControllerTest.java` (extender T019)

### Implementación de US2

- [X] T038 [US2] Agregar guardia en `ReservarCitaService.java`: si estado es OCUPADA
  lanzar `FranjaNoDisponibleException("La franja horaria seleccionada ya está
  ocupada. Por favor, elige otra franja.")` (depende de T031)
- [X] T039 [US2] Crear `GlobalExceptionHandler.java` con `@RestControllerAdvice` en
  `interface_/rest/controller/`: mapear `FranjaNoDisponibleException` → HTTP 409
  `ErrorResponse`, `CitaDuplicadaException` → HTTP 409,
  `RecursoNoEncontradoException` → HTTP 404, validaciones → HTTP 400
- [X] T040 [US2] Verificar que el cuerpo de la respuesta 409 cumple el esquema
  `ErrorResponse` de `contracts/openapi.yml` (campos `codigo`, `mensaje`,
  `timestamp` presentes y tipados correctamente)

**Checkpoint**: US1 y US2 funcionales e independientemente testeables.
`./gradlew test jacocoTestCoverageVerification`

---

## Phase 5: Polish y Concerns Transversales

**Purpose**: Endpoints secundarios, verificación final de calidad, smoke tests.

- [X] T041 [P] Implementar `MedicoRepositoryAdapter.java` en
  `infrastructure/persistence/adapter/` implementando `MedicoRepository`
- [X] T042 [P] Definir puerto de entrada `BuscarMedicosUseCase.java` en
  `domain/port/in/` e implementar `BuscarMedicosService.java` en
  `application/usecase/` (filtro por especialidad y fecha)
- [X] T043 [P] Implementar `MedicoController.java` en `interface_/rest/controller/`
  implementando `MedicosApiDelegate`: `buscarMedicos` y
  `obtenerDisponibilidadMedico`
- [X] T044 [P] Definir puerto de entrada `ListarCitasPacienteUseCase.java` en
  `domain/port/in/` e implementar `ListarCitasPacienteService.java` en
  `application/usecase/`
- [X] T045 [P] Extender `CitaController.java` con `listarCitasPaciente` delegando en
  `ListarCitasPacienteService`
- [X] T046 [P] Crear `NotificacionJpaEntity.java` y `NotificacionJpaRepository.java`
  en `infrastructure/persistence/` para persistir ciclo de vida de notificaciones;
  actualizar `WhatsAppNotificacionAdapter` para registrar cada intento
- [X] T047 [P] Test unitario para `BuscarMedicosService` en
  `src/test/.../application/usecase/BuscarMedicosServiceTest.java`
- [X] T053 [P] Test de integración concurrente para `POST /api/v1/citas` (C3 — FR-003):
  lanzar dos hilos simultáneos con el mismo `disponibilidadId` (franja DISPONIBLE de
  `data.sql`); usar `CountDownLatch` para sincronizar el inicio; verificar que
  exactamente uno obtiene HTTP 201 y el otro HTTP 409, y que solo existe un registro
  en la tabla `cita` al finalizar; en
  `src/test/.../interface_/rest/CitaControllerConcurrencyTest.java`
- [X] T054 [P] [US1] Test de integración MockMvc para `GET /api/v1/pacientes/{id}/citas`
  (E6 — US1 Scenario 2): pre-crear una cita via `POST /api/v1/citas`, luego llamar
  al endpoint de historial y verificar HTTP 200 + array con la cita en estado
  `CONFIRMADA` con campos `medico`, `fecha`, `horaInicio` presentes; en
  `src/test/.../interface_/rest/CitaControllerTest.java` (extender T019)
- [X] T048 Ejecutar test de arquitectura ArchUnit:
  `./gradlew test --tests "*ArchitectureTest*"` — verificar 0 violaciones de
  dirección de dependencias
- [X] T049 Ejecutar suite completa y verificar umbrales de cobertura:
  `./gradlew test jacocoTestReport jacocoTestCoverageVerification`
  (reporte en `build/reports/jacoco/test/html/index.html`)
- [X] T050 [P] Ejecutar smoke tests del quickstart.md contra servidor local
  (`./gradlew bootRun`): flujo completo con `curl` (buscar médico → consultar
  disponibilidad → reservar → verificar historial → intentar duplicado → 409)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sin dependencias — comenzar inmediatamente
- **Foundational (Phase 2)**: Depende de Phase 1 completa — BLOQUEA todas las historias
- **US1 (Phase 3)**: Depende de Phase 2 — puede comenzar tras foundational
- **US2 (Phase 4)**: Extiende `ReservarCitaService` de US1 — depende de T031
- **Polish (Phase 5)**: Depende de que US1 y US2 estén completas

### User Story Dependencies

- **US1 (P1)**: Comienza tras Phase 2. Sin dependencias en otras historias.
- **US2 (P2)**: Extiende lógica de US1 (T031, T034) — no puede comenzar antes
  de que `ReservarCitaService` esté implementado.

### Within Each User Story

- Tests DEBEN escribirse PRIMERO (T014–T019 antes de T020–T034)
- Domain models antes de puertos de salida
- Puertos de salida antes de adaptadores de infraestructura
- Adaptadores antes de casos de uso
- Casos de uso antes de controladores

### Parallel Opportunities

- Phase 1: T002, T003 en paralelo tras T001
- Phase 2: T005–T013 en paralelo tras T004
- US1 tests: T014–T018 todos en paralelo
- US1 models: T020, T021 en paralelo; T022 espera ambos
- US1 infra: T026, T027 en paralelo
- US2 tests: T035, T036 en paralelo
- US1 tests adicionales: T051, T052 en paralelo con T014–T018
- Phase 5: T041–T047, T053, T054 en paralelo

---

## Parallel Example: User Story 1

```bash
# Tests de US1 en paralelo (deben fallar primero):
Task: "T014 — DisponibilidadMedicoTest.java"
Task: "T015 — CitaTest.java"
Task: "T016 — ReservarCitaServiceTest.java"
Task: "T017 — DisponibilidadRepositoryAdapterTest.java"
Task: "T018 — ReservaCitaSteps.java"

# Modelos de dominio en paralelo:
Task: "T020 — Paciente, Medico, Especialidad"
Task: "T021 — DisponibilidadMedico, EstadoDisponibilidad"
```

---

## Implementation Strategy

### MVP First (User Story 1 only)

1. Completar Phase 1: Setup
2. Completar Phase 2: Foundational — **incluye `schema.sql` y `data.sql`**
3. Escribir tests US1 (T014–T019) — verificar que **FALLAN**
4. Completar Phase 3: User Story 1
5. **PARAR Y VALIDAR**: `./gradlew test jacocoTestCoverageVerification`
6. Demo: smoke tests del quickstart.md (datos ya precargados — no setup manual)

### Incremental Delivery

1. Setup + Foundational → base lista, datos precargados disponibles
2. US1 completa → reserva exitosa funciona → **MVP demostrable**
3. US2 completa → manejo de franja ocupada → cobertura del path de error
4. Polish → endpoints secundarios, verificación final de calidad

---

## Notes

- `[P]` = archivos distintos, sin dependencias incompletas
- `[US1]`/`[US2]` traza la tarea a su historia para rastreo
- El código generado en `build/generated/` **NUNCA** debe editarse manualmente
- `schema.sql` es la fuente única de DDL; `jpa.hibernate.ddl-auto: none`
  impide que Hibernate reescriba el esquema
- `test-data-us1.sql` carga franjas DISPONIBLE adicionales para tests de US1
  (`DisponibilidadRepositoryAdapterTest`, `DisponibilidadSteps` Scenario 1/2)
- `test-data-us2.sql` carga la franja OCUPADA para tests de US2
  (`CitaControllerTest` 409, `DisponibilidadSteps` Scenario 3/4)
- Los dos archivos son mutuamente exclusivos en sus tests — no mezclar fixtures
- La constraint `UNIQUE(paciente_id, disponibilidad_id)` en `schema.sql`
  garantiza idempotencia a nivel BD, complementando el lock pesimista
- Los UUIDs de los datos precargados en `data.sql` deben ser fijos (hardcoded)
  para que los step definitions Cucumber y los tests de integración puedan
  referenciarlos sin consultas previas
