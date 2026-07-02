# Research: US-01 · Reserva de Cita en Línea 24/7

**Feature**: 001-reserva-cita-online
**Date**: 2026-06-27
**Branch**: 001-reserva-cita-online

---

## R-01: Lenguaje y plataforma

**Decision**: Java 25 + Spring Boot 4.1.0 (ya configurado en build.gradle)
**Rationale**: El proyecto ya tiene el toolchain definido. Java 25 LTS + Spring Boot
4.x ofrece virtual threads (Project Loom) nativos, lo que mejora el throughput
concurrente sin cambios de paradigma.
**Alternatives considered**: Kotlin (descartado por consistencia con la base actual),
Quarkus (descartado, no configurado).

---

## R-02: Framework BDD para pruebas

**Decision**: Cucumber-JVM 7.x + JUnit 5 + AssertJ
**Rationale**: Cucumber es el estándar de facto en el ecosistema Java para BDD.
Integra nativamente con JUnit 5 via `@Suite` + `@SelectClasspathResource`. Los
`.feature` files en Gherkin son legibles por stakeholders no técnicos.
**Alternatives considered**: JBehave (más verboso, menor adopción moderna), Serenity
BDD (mayor overhead de configuración).
**Dependencies to add to build.gradle**:
```groovy
testImplementation 'io.cucumber:cucumber-java:7.22.2'
testImplementation 'io.cucumber:cucumber-junit-platform-engine:7.22.2'
testImplementation 'io.cucumber:cucumber-spring:7.22.2'
testImplementation 'org.assertj:assertj-core:3.27.3'
```

---

## R-03: Cobertura de código

**Decision**: JaCoCo Gradle Plugin con umbrales de límite de clase ≥ 80% y global ≥ 80%
**Rationale**: JaCoCo es la herramienta de cobertura estándar en el ecosistema
Spring/Gradle. El plugin `jacoco` genera reportes HTML y XML para CI.
**Configuration target**: `jacocoTestCoverageVerification` task con límites:
- `element: CLASS`, `minimum: 0.80`
- `element: BUNDLE`, `minimum: 0.80`
Exclusiones declaradas para código generado por openapi-generator.
**Dependencies to add to build.gradle**:
```groovy
plugins {
  id 'jacoco'
}
```

---

## R-04: API First — OpenAPI Generator

**Decision**: `openapi-generator-gradle-plugin` versión 7.x con generador `spring`
**Rationale**: El plugin integra en el ciclo de build Gradle y genera stubs de
controladores (interfaz) y DTOs. Se usa la estrategia `delegate` para que los
controladores generados deleguen en implementaciones propias sin editar código
generado.
**Configuration target**: Genera código en `build/generated/` (excluido de coverage).
El contrato fuente es `src/main/resources/openapi/openapi.yml`.
**Dependencies to add to build.gradle**:
```groovy
plugins {
  id 'org.openapi.generator' version '7.13.0'
}
dependencies {
  implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8'
  implementation 'jakarta.validation:jakarta.validation-api'
  implementation 'org.openapitools:jackson-databind-nullable:0.2.6'
}
```

---

## R-05: Persistencia

**Decision**: H2 (in-memory) para desarrollo y pruebas de integración; PostgreSQL
como destino de producción (a configurar como perfil `prod`)
**Rationale**: El build.gradle ya incluye H2. Para esta historia universitaria H2 es
suficiente para validar el comportamiento. La abstracción JPA permite cambiar el
driver en producción sin tocar el dominio.
**Alternatives considered**: PostgreSQL desde el inicio con Testcontainers (preferible
en entorno CI real; deferido para historia de infraestructura).

---

## R-06: Notificaciones WhatsApp

**Decision**: Puerto de salida `NotificacionWhatsAppPort` (interfaz en Application),
adaptador de infraestructura `TwilioWhatsAppAdapter` (stub para v1)
**Rationale**: La constitución prohíbe dependencias externas en Application y Domain.
El adaptador implementa el puerto y puede ser reemplazado por Meta Business API sin
cambiar el dominio. En v1 se implementa un stub que loguea el mensaje y simula el
envío; la integración real es una historia técnica separada.
**Retry strategy**: Spring `@Async` + `@Retryable` (spring-retry) con 3 intentos y
back-off exponencial de 2s base.
**Dependencies to add to build.gradle**:
```groovy
implementation 'org.springframework.retry:spring-retry'
implementation 'org.springframework:spring-aspects'
```

---

## R-07: Concurrencia y reserva atómica

**Decision**: Bloqueo pesimista (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) en el
repositorio de `DisponibilidadMedico` al confirmar reserva
**Rationale**: Garantiza que dos transacciones simultáneas no reserven la misma
franja. Spring Data JPA gestiona el lock a nivel de `SELECT FOR UPDATE`. El tiempo
de lock es acotado (duración de la transacción de confirmación, < 500ms esperado).
**Alternatives considered**: Lock optimista con `@Version` (más ligero pero requiere
lógica de reintento en el cliente ante conflicto; peor UX).

---

## R-08: Arquitectura de paquetes (Clean Architecture)

**Decision**: Cuatro capas con paquetes anidados bajo `com.example.citassaludservice`

| Capa | Paquete | Contenido |
|------|---------|-----------|
| Domain | `.domain.model` / `.domain.port` / `.domain.exception` | Entidades puras, puertos (interfaces), excepciones de negocio |
| Application | `.application.usecase` | Casos de uso que implementan puertos de entrada |
| Infrastructure | `.infrastructure.persistence` / `.infrastructure.notification` | Adaptadores JPA, adaptador WhatsApp |
| Interface | `.interface_.rest` | Controladores delegados, mapeadores de DTOs |

**Rationale**: Dependency direction enforced via ArchUnit tests en suite de integración.
Ninguna clase de `.domain` importa nada fuera de Java stdlib.

---

## R-09: Inicialización de base de datos (schema + datos precargados)

**Decision**: Scripts SQL en `src/main/resources/db/` cargados por Spring Boot
`DataSourceInitializer` via `spring.sql.init`.

| Archivo | Ubicación | Propósito |
|---------|-----------|-----------|
| `schema.sql` | `src/main/resources/db/` | DDL: `CREATE TABLE` para todas las entidades JPA |
| `data.sql` | `src/main/resources/db/` | DML: datos representativos de médicos, especialidades y franjas disponibles |
| `test-data.sql` | `src/test/resources/db/` | DML adicional exclusivo de pruebas: franja OCUPADA, paciente de prueba |

**Configuración en `application.yaml`**:
```yaml
spring:
  sql:
    init:
      schema-locations: classpath:db/schema.sql
      data-locations: classpath:db/data.sql
      mode: always        # siempre ejecuta en dev/test con H2
  jpa:
    hibernate:
      ddl-auto: none      # desactiva DDL de Hibernate; schema.sql es la fuente única
```

**Datos precargados en `data.sql`** (representativos, no exhaustivos):
- 3 médicos: un médico general, un pediatra, un cardiólogo
- 20 franjas de disponibilidad distribuidas en los próximos 7 días (todas DISPONIBLE)
- 2 pacientes de prueba con número WhatsApp válido

**Datos precargados en `test-data.sql`** (fixtures para BDD negativo):
- 1 franja explícitamente en estado OCUPADA (para escenario US2)

**Rationale**: Elimina la necesidad de configuración manual antes de arrancar o
ejecutar los smoke tests del quickstart.md. La separación `schema.sql` / `data.sql`
sigue la convención de Spring Boot y permite que en un perfil `prod` futuro solo se
aplique el schema y los datos vengan de migraciones Flyway/Liquibase.
**Alternatives considered**: `ddl-auto: create-drop` con `@Sql` en tests (más
acoplado al test framework; dificulta reutilizar datos entre tests BDD).

---

## Resolved NEEDS CLARIFICATION items

Ninguno — el stack estaba completamente determinado por el proyecto existente.
