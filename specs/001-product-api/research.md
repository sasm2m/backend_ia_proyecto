# Research: SmartInventory API Product Management

## Decision 1: Keep product storage in memory

- **Decision**: Store products in an in-memory collection managed by the
  service layer, using generated IDs within the running application instance.
- **Rationale**: The constitution explicitly excludes database persistence for
  this phase, and the feature is intended for a first backend practice with
  minimal setup overhead.
- **Alternatives considered**:
  - Embedded database: rejected because it adds persistence complexity that is
    out of scope.
  - File-based storage: rejected because it introduces I/O concerns without
    adding value to the practice goals.

## Decision 2: Use DTOs only for API clarity

- **Decision**: Use request and response DTOs for product input, product output,
  health output, and error output, while keeping the core product state in a
  model object.
- **Rationale**: This preserves layered separation and keeps validation rules
  and external JSON contracts easy to explain without overcomplicating the
  domain model.
- **Alternatives considered**:
  - Expose the model directly: rejected because it makes contract evolution and
    validation concerns less explicit for students.
  - Create many specialized DTOs: rejected because it adds unnecessary surface
    area for a small CRUD exercise.

## Decision 3: Use simple centralized error handling

- **Decision**: Use a lightweight global exception handler plus a single error
  response shape for validation failures and product-not-found cases.
- **Rationale**: The user requested simple error handling and clear JSON
  responses. Centralizing these responses avoids business logic in controllers
  and keeps error behavior consistent.
- **Alternatives considered**:
  - Per-controller error handling: rejected because it duplicates behavior.
  - Rich problem-details framework customization: rejected because it is more
    advanced than the learning goals require.

## Decision 4: Prefer standard Spring Boot test tooling

- **Decision**: Use JUnit 5, Spring Boot Test, and MockMvc for controller-level
  endpoint validation, with unit tests for service rules where helpful.
- **Rationale**: These are standard Spring tools, require no extra testing
  stack decisions, and let the team validate JSON responses and validation
  behavior directly.
- **Alternatives considered**:
  - Full end-to-end external API testing only: rejected because it gives weaker
    feedback during implementation.
  - Additional third-party testing libraries: rejected because they are not
    needed for this scope.

## Decision 5: Keep endpoint contract explicit in OpenAPI

- **Decision**: Define the expected API in a single OpenAPI contract file for
  health and product endpoints.
- **Rationale**: The feature is a public REST API surface, and an explicit
  contract makes validation scenarios and future implementation tasks concrete.
- **Alternatives considered**:
  - Plain Markdown endpoint notes only: rejected because OpenAPI is clearer for
    request/response shape and status coverage.
  - Code-first contract generation: rejected because the plan phase should stay
    implementation-independent.
