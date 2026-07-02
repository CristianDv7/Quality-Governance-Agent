# Implementation Plan: US-01 · Reserva de Cita en Línea 24/7

**Branch**: `001-reserva-cita-online` | **Date**: 2026-06-27 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/001-reserva-cita-online/spec.md`

---

## Summary

Microservicio REST que permite a los pacientes reservar citas médicas en línea las
24 horas del día. El sistema expone una API generada a partir del contrato OpenAPI,
organizada en Clean Architecture (Domain / Application / Infrastructure / Interface).
Garantiza reserva atómica (lock pesimista), idempotencia ante doble envío y
notificación asíncrona al paciente por WhatsApp con reintentos automáticos.

---

## Technical Context

**Language/Version**: Java 25

**Primary Dependencies**: Spring Boot 4.1.0 (Web MVC, Data JPA, Retry),
Lombok, Cucumber-JVM 7.x (BDD), JaCoCo (coverage), openapi-generator-gradle-plugin 7.x,
ArchUnit (dependency-direction tests), springdoc-openapi-starter-webmvc-ui 2.x

**Storage**: H2 in-memory (desarrollo y tests de integración);
PostgreSQL como perfil `prod` (futura historia técnica).
Inicialización SQL via `spring.sql.init`: `schema.sql` (DDL) y `data.sql` (DML)
en `src/main/resources/db/`; `test-data.sql` en `src/test/resources/db/` para
fixtures de prueba específicos (franjas OCUPADA, pacientes de prueba).

**Testing**: JUnit 5, Cucumber-JVM + cucumber-spring, AssertJ, Spring Boot Test,
ArchUnit

**Target Platform**: JVM 25 / servidor Linux (microservicio REST)

**Project Type**: web-service (REST API microservice)

**Performance Goals**:
- Reserva completada en < 3 min extremo a extremo (SC-001)
- 95% de notificaciones WhatsApp entregadas en < 60 s (SC-003)
- Disponibilidad del servicio ≥ 99,5% mensual (SC-004)

**Constraints**:
- Reserva atómica: lock pesimista (`SELECT FOR UPDATE`) sobre `DisponibilidadMedico`
- Idempotencia: constraint UNIQUE `(paciente_id, disponibilidad_id)` en tabla `Cita`
- Reintentos WhatsApp: mínimo 3 intentos con back-off exponencial (spring-retry)
- Código generado por openapi-generator excluido de cobertura JaCoCo

**Scale/Scope**: Historia universitaria — H2 in-memory es suficiente para v1;
escala de producción queda en historia técnica de infraestructura

---

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Clean Architecture**: Cuatro capas definidas (Domain / Application /
  Infrastructure / Interface). Dependencia hacia adentro — Domain no importa nada
  externo. Verificado por ArchUnit en suite de pruebas.
- [x] **BDD Testing**: Escenarios Gherkin redactados en spec.md y en
  `src/test/resources/features/reserva-cita.feature`. Test-first confirmado.
  Scope: unitarias (domain + use cases) + integración (JPA + adapters) +
  funcionales end-to-end (Cucumber + Spring Boot Test).
- [x] **SOLID/YAGNI/DRY**: Sin abstracciones especulativas. Una responsabilidad por
  clase. Puertos e interfaces granulares. No se implementa WhatsApp real en v1
  (stub suficiente).
- [x] **API First**: Contrato `specs/001-reserva-cita-online/contracts/openapi.yml`
  aprobado antes de implementar. `openapi-generator` configurado en build.gradle.
  Código generado en `build/generated/` — no editable manualmente.
- [x] **Coverage gates**: JaCoCo configurado con límites: clase ≥ 80%, global ≥ 80%.
  Exclusiones para `build/generated/` declaradas en build.gradle.
  `jacocoTestCoverageVerification` task falla el build si no se cumplen.

**Post-design re-check**: ✅ Todos los gates pasan. No hay violaciones justificadas.

---

## Project Structure

### Documentation (this feature)

```text
specs/001-reserva-cita-online/
├── plan.md              # Este archivo
├── research.md          # Decisiones tecnológicas
├── data-model.md        # Modelo de dominio y entidades
├── quickstart.md        # Guía de ejecución y smoke tests
├── contracts/
│   └── openapi.yml      # Contrato API First (fuente de verdad)
└── tasks.md             # Generado por /speckit-tasks (pendiente)
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/com/example/citassaludservice/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Paciente.java
│   │   │   │   ├── Medico.java
│   │   │   │   ├── Especialidad.java          (enum)
│   │   │   │   ├── DisponibilidadMedico.java
│   │   │   │   ├── EstadoDisponibilidad.java  (enum)
│   │   │   │   ├── Cita.java
│   │   │   │   ├── EstadoCita.java            (enum)
│   │   │   │   ├── NotificacionWhatsApp.java
│   │   │   │   └── EstadoNotificacion.java    (enum)
│   │   │   ├── port/
│   │   │   │   ├── in/
│   │   │   │   │   ├── BuscarMedicosUseCase.java
│   │   │   │   │   ├── ObtenerDisponibilidadUseCase.java
│   │   │   │   │   ├── ReservarCitaUseCase.java
│   │   │   │   │   └── ListarCitasPacienteUseCase.java
│   │   │   │   └── out/
│   │   │   │       ├── MedicoRepository.java
│   │   │   │       ├── DisponibilidadRepository.java
│   │   │   │       ├── CitaRepository.java
│   │   │   │       └── NotificacionWhatsAppPort.java
│   │   │   └── exception/
│   │   │       ├── FranjaNoDisponibleException.java
│   │   │       ├── CitaDuplicadaException.java
│   │   │       └── RecursoNoEncontradoException.java
│   │   ├── application/
│   │   │   └── usecase/
│   │   │       ├── BuscarMedicosService.java
│   │   │       ├── ObtenerDisponibilidadService.java
│   │   │       ├── ReservarCitaService.java
│   │   │       └── ListarCitasPacienteService.java
│   │   ├── infrastructure/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/
│   │   │   │   │   ├── MedicoJpaEntity.java
│   │   │   │   │   ├── DisponibilidadJpaEntity.java
│   │   │   │   │   ├── CitaJpaEntity.java
│   │   │   │   │   └── NotificacionJpaEntity.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── MedicoJpaRepository.java
│   │   │   │   │   ├── DisponibilidadJpaRepository.java
│   │   │   │   │   ├── CitaJpaRepository.java
│   │   │   │   │   └── NotificacionJpaRepository.java
│   │   │   │   ├── adapter/
│   │   │   │   │   ├── MedicoRepositoryAdapter.java
│   │   │   │   │   ├── DisponibilidadRepositoryAdapter.java
│   │   │   │   │   └── CitaRepositoryAdapter.java
│   │   │   │   └── mapper/
│   │   │   │       └── PersistenceMapper.java
│   │   │   └── notification/
│   │   │       └── whatsapp/
│   │   │           └── WhatsAppNotificacionAdapter.java  (stub v1)
│   │   └── interface_/
│   │       └── rest/
│   │           ├── controller/
│   │           │   ├── MedicoController.java
│   │           │   └── CitaController.java
│   │           └── mapper/
│   │               └── ApiMapper.java
│   └── resources/
│       ├── application.yaml
│       ├── db/
│       │   ├── schema.sql               (DDL: CREATE TABLE para todas las entidades)
│       │   └── data.sql                 (DML: datos precargados de médicos, pacientes y disponibilidad)
│       └── openapi/
│           └── openapi.yml              (contrato copiado desde specs/contracts/)
├── test/
│   ├── java/com/example/citassaludservice/
│   │   ├── domain/
│   │   │   └── model/                  (unit tests — entidades puras)
│   │   ├── application/
│   │   │   └── usecase/               (unit tests — use cases con mocks)
│   │   ├── infrastructure/
│   │   │   └── persistence/           (integration tests — H2)
│   │   ├── interface_/
│   │   │   └── rest/                  (integration tests — MockMvc)
│   │   ├── architecture/
│   │   │   └── ArchitectureTest.java  (ArchUnit — dependency direction)
│   │   └── bdd/
│   │       ├── CucumberRunner.java
│   │       └── steps/
│   │           ├── ReservaCitaSteps.java
│   │           └── DisponibilidadSteps.java
│   └── resources/
│       ├── features/
│       │   └── reserva-cita.feature   (Gherkin scenarios)
│       └── db/
│           └── test-data.sql          (DML adicional exclusivo para pruebas: pacientes de prueba, franjas ocupadas)
```

**Structure Decision**: Single-project layout con Clean Architecture estricta.
La capa `interface_` usa guión bajo para evitar colisión con palabra reservada Java.
El contrato OpenAPI se copia a `src/main/resources/openapi/` para que el generador
lo encuentre en classpath; la fuente canónica es `specs/contracts/openapi.yml`.

Los scripts SQL en `src/main/resources/db/` son cargados automáticamente por Spring
Boot al arrancar (modo `always` para desarrollo, `embedded` para tests). `schema.sql`
crea todas las tablas e índices; `data.sql` inserta datos representativos de médicos
y disponibilidades para poder usar el sistema sin configuración adicional.
`test-data.sql` en resources de test agrega fixtures específicos (ej. franja ya
OCUPADA) necesarios para los escenarios BDD negativos.

---

## Complexity Tracking

> No hay violaciones de constitución que justificar.
