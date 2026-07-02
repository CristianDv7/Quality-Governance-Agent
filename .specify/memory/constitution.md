<!--
SYNC IMPACT REPORT
==================
Version change: [unversioned] → 1.0.0
Bump rationale: MAJOR — initial ratification, all principles newly established.

Added sections:
  - Core Principles (I–V)
  - Quality Gates & Metrics
  - Development Workflow
  - Governance

Modified principles: N/A (initial ratification)
Removed sections: N/A

Templates updated:
  ✅ .specify/memory/constitution.md (this file)
  ✅ .specify/templates/plan-template.md (Constitution Check gates aligned)
  ✅ .specify/templates/spec-template.md (BDD acceptance criteria format enforced)
  ✅ .specify/templates/tasks-template.md (test-first and OpenAPI tasks added)

Deferred TODOs: None
-->

# CitasSalud Schedule Service Constitution

## Core Principles

### I. Clean Architecture (NON-NEGOTIABLE)

The project MUST follow Clean Architecture as defined by Robert C. Martin. The codebase
MUST be organized into four concentric layers with strict dependency rules:

- **Domain** layer: entities and business rules — MUST have zero external dependencies.
- **Application** layer: use cases and ports (interfaces) — MUST depend only on Domain.
- **Infrastructure** layer: adapters, repositories, DB, messaging — MUST depend only on
  Application interfaces (ports), never on other Infrastructure components directly.
- **Interface** layer: controllers, DTOs, API handlers — MUST depend only on Application.

Dependency direction MUST always point inward (toward Domain). Framework and library
imports are FORBIDDEN in the Domain and Application layers. Violations of dependency
direction MUST block merge.

**Rationale**: Isolation of business logic from infrastructure enables independent
testability, replaceability of external components, and long-term maintainability.

### II. BDD Testing (NON-NEGOTIABLE)

All behavior MUST be specified and verified using Behavior-Driven Development (BDD)
with Given/When/Then scenarios written before implementation.

- **Unit tests**: MUST cover every domain entity, use case, and service in isolation.
  Mocks or stubs MUST be used for all external dependencies.
- **Integration tests**: MUST verify the interaction between layers (e.g., use case →
  repository adapter → real DB). MUST use real infrastructure in a controlled test
  environment (test containers or equivalent).
- **Functional/acceptance tests**: MUST exercise full API endpoints end-to-end using
  BDD scenario definitions (e.g., Cucumber, Karate, or equivalent).
- Tests MUST be written FIRST and MUST fail before implementation begins
  (Red → Green → Refactor).
- Test names MUST follow the BDD pattern:
  `given_<context>_when_<action>_then_<expected_outcome>`.

**Rationale**: BDD ensures that tests document business intent, are readable by
non-technical stakeholders, and catch regressions at every architectural boundary.

### III. Programming Best Practices (NON-NEGOTIABLE)

All code MUST comply with the following principles:

- **SOLID**:
  - Single Responsibility: each class/module MUST have one reason to change.
  - Open/Closed: classes MUST be open for extension, closed for modification.
  - Liskov Substitution: subtypes MUST be substitutable for their base types.
  - Interface Segregation: interfaces MUST be fine-grained; no client MUST depend on
    methods it does not use.
  - Dependency Inversion: high-level modules MUST NOT depend on low-level modules;
    both MUST depend on abstractions.
- **YAGNI** (You Aren't Gonna Need It): features and abstractions MUST NOT be
  implemented until they are actually required. Speculative generality is forbidden.
- **DRY** (Don't Repeat Yourself): every piece of knowledge MUST have a single,
  authoritative representation. Duplication MUST be extracted into shared components.

**Rationale**: These principles reduce accidental complexity, prevent premature
abstraction, and keep the codebase maintainable as requirements evolve.

### IV. API First with OpenAPI (NON-NEGOTIABLE)

All APIs MUST be designed contract-first using the OpenAPI Specification (OAS 3.x):

- An `openapi.yml` contract MUST be authored and approved BEFORE any implementation
  begins. No endpoint may exist without a corresponding contract definition.
- Server stubs and client SDKs MUST be generated from the contract using
  `openapi-generator`. Hand-written boilerplate that duplicates contract definitions
  is forbidden.
- The generated code MUST be regenerated whenever the contract changes; generated
  files MUST NOT be manually edited.
- Contract changes that break existing consumers (removed fields, changed types,
  renamed paths) MUST follow semantic versioning and require a new API version.
- The contract file MUST be kept under version control and reviewed as part of every
  API-affecting PR.

**Rationale**: API First decouples interface design from implementation, enables
parallel frontend/backend development, and provides a single source of truth for
all consumers.

### V. Coverage Quality Gates (NON-NEGOTIABLE)

Every build MUST enforce the following code coverage thresholds. Builds that do not
meet these thresholds MUST fail and MUST NOT be merged:

- **Per-class coverage**: MUST be ≥ 80% (line or branch coverage, whichever is lower).
- **Global coverage**: MUST be ≥ 80% (aggregate across all classes in the project).
- Coverage reports MUST be generated on every CI run and stored as artifacts.
- New classes introduced in a PR MUST meet the per-class threshold independently;
  a high global average MUST NOT mask under-tested new code.
- Exclusions from coverage (e.g., generated code, configuration classes) MUST be
  explicitly declared in the build configuration and reviewed during PR.

**Rationale**: Coverage thresholds create a measurable, enforceable quality floor that
prevents the accumulation of untested code paths over time.

## Quality Gates & Metrics

The following gates MUST be checked at every pull request and MUST all pass before merge:

| Gate | Threshold | Tool |
|------|-----------|------|
| Global test coverage | ≥ 80% | JaCoCo / language-equivalent |
| Per-class test coverage | ≥ 80% | JaCoCo / language-equivalent |
| Dependency direction violations | 0 | ArchUnit or equivalent |
| Open BDD scenarios without implementation | 0 | CI step |
| OpenAPI contract present for every endpoint | 100% | openapi-validator |
| SOLID / DRY violations (critical severity) | 0 | SonarQube or equivalent |

Coverage exclusions (generated code, framework configs) MUST be declared in
`build-config/coverage-exclusions.txt` and reviewed on first inclusion.

## Development Workflow

1. **Contract first**: Author or update `openapi.yml` → peer review → approve.
2. **Generate stubs**: Run `openapi-generator` to produce server stubs and DTOs.
3. **Write BDD scenarios**: Define Given/When/Then tests — all MUST fail initially.
4. **Implement** using Clean Architecture layers, applying SOLID/YAGNI/DRY.
5. **Verify** coverage gates locally before pushing (`./gradlew test jacocoCheck` or
   language equivalent).
6. **CI pipeline** MUST run: lint → unit tests → integration tests → functional tests
   → coverage report → dependency-direction check → merge gate.
7. **PR checklist** MUST confirm: contract updated, tests written first, coverage
   thresholds met, no dependency inversions, no speculative abstractions.

Feature branches MUST be created via `/speckit-git-feature` and named
`###-short-description`. Direct commits to `main`/`master` are forbidden.

## Governance

This constitution supersedes all other practices, conventions, and prior agreements
within this project. Any exception requires explicit amendment.

**Amendment procedure**:
1. Propose change in a PR with justification and migration plan.
2. At least one peer review required.
3. Update `CONSTITUTION_VERSION` following semantic versioning:
   - MAJOR: principle removal, redefinition, or backward-incompatible governance change.
   - MINOR: new principle or section added.
   - PATCH: clarification or wording refinement.
4. Record `LAST_AMENDED_DATE` as the merge date.
5. Propagate changes to all dependent templates and this Sync Impact Report.

**Compliance review**: Every sprint retrospective MUST include a constitution compliance
check. Recurring violations MUST trigger an amendment discussion rather than permanent
exceptions.

All PRs and code reviews MUST verify compliance with Principles I–V before approval.
Use `.specify/memory/constitution.md` as the authoritative reference during reviews.

**Version**: 1.0.0 | **Ratified**: 2026-06-27 | **Last Amended**: 2026-06-27
