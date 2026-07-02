# Quickstart: US-01 · Reserva de Cita en Línea 24/7

**Feature**: 001-reserva-cita-online
**Branch**: 001-reserva-cita-online

---

## Prerequisites

- Java 25 JDK instalado y en `PATH`
- Gradle Wrapper disponible (`./gradlew`)
- Git configurado con acceso al repositorio

---

## 1 — Clonar y verificar el branch

```bash
git checkout 001-reserva-cita-online
./gradlew clean build -x test   # Verifica que compila
```

---

## 2 — Ejecutar la aplicación localmente

```bash
./gradlew bootRun
```

El servidor inicia en `http://localhost:8080`.
Al arrancar, Spring Boot ejecuta automáticamente:
- `src/main/resources/db/schema.sql` — crea todas las tablas
- `src/main/resources/db/data.sql` — inserta 3 médicos, 2 pacientes y 20 franjas
  disponibles para los próximos 7 días

La consola H2 está disponible en `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:testdb`, user: `sa`, password: vacío).
Desde allí puedes ejecutar `SELECT * FROM medico;` para ver los datos precargados.

---

## 3 — Explorar el contrato OpenAPI

Una vez iniciado:

```
http://localhost:8080/swagger-ui.html
```

El contrato fuente está en `src/main/resources/openapi/openapi.yml`.

---

## 4 — Ejecutar todas las pruebas

```bash
./gradlew test                       # Unitarias + integración + BDD
./gradlew jacocoTestReport           # Genera reporte HTML en build/reports/jacoco/
./gradlew jacocoTestCoverageVerification  # Falla si coverage < 80%
```

El reporte de cobertura queda en:
`build/reports/jacoco/test/html/index.html`

---

## 5 — Ejecutar solo las pruebas BDD (Cucumber)

```bash
./gradlew test --tests "*CucumberRunner*"
```

Los `.feature` files están en:
`src/test/resources/features/reserva-cita.feature`

---

## 6 — Flujo de reserva de cita (smoke test manual)

### Paso 1: Buscar médicos disponibles

```bash
curl -s "http://localhost:8080/api/v1/medicos?especialidad=MEDICINA_GENERAL&fecha=2026-07-01" | jq .
```

### Paso 2: Consultar disponibilidad de un médico

```bash
# Reemplazar {medicoId} con el id del paso anterior
curl -s "http://localhost:8080/api/v1/medicos/{medicoId}/disponibilidad?fecha=2026-07-01" | jq .
```

### Paso 3: Reservar una franja

```bash
curl -s -X POST "http://localhost:8080/api/v1/citas" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "{pacienteId}",
    "disponibilidadId": "{disponibilidadId}"
  }' | jq .
```

**Respuesta esperada (HTTP 201)**:
```json
{
  "id": "...",
  "estado": "CONFIRMADA",
  "canalCreacion": "ONLINE",
  ...
}
```

### Paso 4: Intentar reservar la misma franja (debe rechazar)

Repetir el mismo `curl` del Paso 3 →
**Respuesta esperada (HTTP 409)**:
```json
{
  "codigo": "FRANJA_NO_DISPONIBLE",
  "mensaje": "La franja horaria seleccionada ya está ocupada. Por favor, elige otra franja."
}
```

### Paso 5: Verificar historial del paciente

```bash
curl -s "http://localhost:8080/api/v1/pacientes/{pacienteId}/citas?estado=CONFIRMADA" | jq .
```

---

## 7 — Regenerar código desde contrato OpenAPI

Si el contrato cambia, regenerar los stubs antes de compilar:

```bash
./gradlew openApiGenerate
./gradlew compileJava
```

El código generado vive en `build/generated/` y **nunca** debe editarse manualmente.

---

## Validación de la constitución

| Principio | Verificación |
|-----------|-------------|
| Clean Architecture | `./gradlew test --tests "*ArchitectureTest*"` (ArchUnit) |
| BDD Testing | `./gradlew test --tests "*CucumberRunner*"` |
| Coverage ≥ 80% | `./gradlew jacocoTestCoverageVerification` |
| API First | Contrato en `src/main/resources/openapi/openapi.yml` revisado antes de implementar |
