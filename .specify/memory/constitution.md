<!--
Sync Impact Report
- Version change: template -> 1.0.0
- Modified principles:
  - Template Principle 1 -> I. Layered REST Architecture
  - Template Principle 2 -> II. Service-Owned Business Logic
  - Template Principle 3 -> III. Explicit Models and Input Validation
  - Template Principle 4 -> IV. Clear JSON Contracts and English Naming
  - Template Principle 5 -> V. Simplicity and Scope Discipline
- Added sections:
  - Current Scope and Technical Baseline
  - Delivery and Review Workflow
- Removed sections:
  - None
- Templates requiring updates:
  - ✅ updated .specify/templates/plan-template.md
  - ✅ updated .specify/templates/spec-template.md
  - ✅ updated .specify/templates/tasks-template.md
  - ✅ no .specify/templates/commands/*.md directory present; no update required
- Follow-up TODOs:
  - None
-->
# SmartInventory API Constitution

## Core Principles

### I. Layered REST Architecture
All backend features MUST be implemented as a layered REST API using Java 25,
Spring Boot 4.0.6, and Maven. Request handling belongs in controllers,
application rules belong in services, and domain representation belongs in
models or DTOs that match their responsibility. New work MUST preserve this
structure so students can trace request flow without hidden coupling.

### II. Service-Owned Business Logic
Controllers MUST stay thin. They may receive requests, trigger validation,
delegate to services, and return HTTP responses, but they MUST NOT contain
business rules, state transitions, or data processing that belongs in the
service layer. When logic is shared by multiple endpoints, it MUST be extracted
into a service instead of duplicated in controllers.

### III. Explicit Models and Input Validation
Every API input and output with business meaning MUST be represented with clear
models or DTOs using English names for classes, methods, fields, and variables.
Incoming data MUST be validated before business logic runs, and invalid input
MUST return a clear JSON error response with actionable information. Hidden or
implicit payload rules are not allowed because they make the code harder to
teach and maintain.

### IV. Clear JSON Contracts and English Naming
Every endpoint in this project MUST return JSON responses with predictable field
names and HTTP semantics that match the outcome. Endpoint names, code
identifiers, and payload fields MUST use English so the API contract and source
code stay consistent with the Spring ecosystem and common backend references.
Response bodies MUST be easy to explain to students and easy to consume by a
client without guessing.

### V. Simplicity and Scope Discipline
The first version of SmartInventory API MUST stay intentionally small: product
management and a health check endpoint only. The project MUST avoid categories,
users, authentication, security layers, database persistence, and inventory
movement features until a later constitution or scope update explicitly allows
them. Teams MUST prefer the simplest implementation that satisfies the current
requirements and reject speculative abstractions or premature infrastructure.

## Current Scope and Technical Baseline

The approved stack for this stage is Java 25, Spring Boot 4.0.6, and Maven.
The API scope includes only:

- Product management endpoints and the supporting service/model flow.
- A health check endpoint for operational verification.

The following are explicitly out of scope and MUST NOT be introduced in this
phase:

- Categories
- Users
- Authentication
- Security configuration or access control
- Database integration or persistence frameworks that imply active storage
- Inventory movement features

Until storage is formally added, application state MUST remain in-memory or
stubbed in a way that keeps architecture clear and classroom-friendly.

## Delivery and Review Workflow

Plans, specs, and task lists MUST prove compliance with this constitution before
implementation starts. Each plan MUST confirm the layered architecture, JSON
response strategy, validation approach, and scope exclusions. Each task list
MUST map work to controller, service, model, validation, and endpoint behavior
without introducing forbidden infrastructure.

Code review or self-review MUST check at minimum:

- No business logic inside controllers
- Clear separation between controller, service, and model responsibilities
- Input validation for externally supplied data
- JSON responses for success and failure paths
- English naming in code and API contracts
- No out-of-scope features or premature infrastructure

## Governance

This constitution is the authoritative source for project engineering rules and
takes precedence over informal habits or generated defaults. Amendments MUST be
made by explicitly updating this file and synchronizing any affected templates,
plans, specs, or tasks in the same change.

Versioning policy:

- MAJOR: Remove or redefine a principle or governance rule in a
  backward-incompatible way.
- MINOR: Add a new principle, section, or materially expanded obligation.
- PATCH: Clarify wording without changing project obligations.

Compliance review expectations:

- Every new plan MUST pass the constitution check before design proceeds.
- Every specification MUST state scope boundaries consistent with this document.
- Every implementation task set MUST avoid prohibited features unless this
  constitution is amended first.

**Version**: 1.0.0 | **Ratified**: 2026-06-14 | **Last Amended**: 2026-06-14
