---

description: "Task list for SmartInventory API Product Management implementation"
---

# Tasks: SmartInventory API Product Management

**Input**: Design documents from `/specs/001-product-api/`

**Prerequisites**: plan.md (required), spec.md (required for user stories),
research.md, data-model.md, contracts/

**Tests**: No explicit TDD or test-first requirement was requested in the
feature specification, so this task list focuses on implementation and
validation-ready delivery tasks.

**Organization**: Tasks are grouped by user story to enable independent
implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Single project**: `src/`, `src/test/` at repository root
- Package root: `src/main/java/com/smartinventory/api/`
- Resources: `src/main/resources/`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and base file structure

- [X] T001 Create Maven Spring Boot project file in `pom.xml`
- [X] T002 Create application bootstrap class in `src/main/java/com/smartinventory/api/SmartInventoryApiApplication.java`
- [X] T003 [P] Create base package directories with placeholder files in `src/main/java/com/smartinventory/api/controller/.gitkeep`, `src/main/java/com/smartinventory/api/service/.gitkeep`, `src/main/java/com/smartinventory/api/model/.gitkeep`, `src/main/java/com/smartinventory/api/dto/.gitkeep`, and `src/main/java/com/smartinventory/api/exception/.gitkeep`
- [X] T004 [P] Create base application configuration in `src/main/resources/application.properties`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can
be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T005 Create core domain model in `src/main/java/com/smartinventory/api/model/Product.java`
- [X] T006 [P] Create shared request and response DTOs in `src/main/java/com/smartinventory/api/dto/ProductRequest.java`, `src/main/java/com/smartinventory/api/dto/ProductResponse.java`, `src/main/java/com/smartinventory/api/dto/HealthResponse.java`, and `src/main/java/com/smartinventory/api/dto/ErrorResponse.java`
- [X] T007 Create not-found exception in `src/main/java/com/smartinventory/api/exception/ProductNotFoundException.java`
- [X] T008 Create centralized error handling in `src/main/java/com/smartinventory/api/exception/GlobalExceptionHandler.java`
- [X] T009 Create in-memory product service with ID generation support in `src/main/java/com/smartinventory/api/service/ProductService.java`
- [X] T010 Confirm endpoint contract and scope exclusions against `specs/001-product-api/contracts/openapi.yaml` and `specs/001-product-api/spec.md`

**Checkpoint**: Foundation ready - user story implementation can now begin in
parallel

---

## Phase 3: User Story 1 - Verify Service Availability (Priority: P1) 🎯 MVP

**Goal**: Expose a health endpoint that confirms service availability and
identity

**Independent Test**: Run the application and call `GET /api/health`; the
response must include `status`, `service`, and `version` with the expected
values.

### Implementation for User Story 1

- [X] T011 [US1] Implement health controller endpoint in `src/main/java/com/smartinventory/api/controller/HealthController.java`
- [X] T012 [US1] Align health response payload with the contract in `src/main/java/com/smartinventory/api/dto/HealthResponse.java`

**Checkpoint**: User Story 1 should be fully functional and independently
testable

---

## Phase 4: User Story 2 - Manage Product Catalog (Priority: P1)

**Goal**: Allow clients to create, list, retrieve, update, and delete products
through the API

**Independent Test**: Create a product, list products, retrieve one by ID,
update it, and delete it using the documented endpoints without relying on any
other feature area.

### Implementation for User Story 2

- [X] T013 [US2] Implement product create, list, retrieve, update, and delete operations in `src/main/java/com/smartinventory/api/service/ProductService.java`
- [X] T014 [US2] Implement product REST endpoints in `src/main/java/com/smartinventory/api/controller/ProductController.java`
- [X] T015 [US2] Map domain model to API responses in `src/main/java/com/smartinventory/api/dto/ProductResponse.java`
- [X] T016 [US2] Keep request payload structure aligned with CRUD operations in `src/main/java/com/smartinventory/api/dto/ProductRequest.java`

**Checkpoint**: User Story 2 should be fully functional and independently
testable

---

## Phase 5: User Story 3 - Receive Clear Validation and Missing-Resource Errors (Priority: P2)

**Goal**: Return clear JSON errors for invalid product input and missing product
IDs

**Independent Test**: Send invalid product data and access a non-existent
product ID for retrieve, update, and delete operations; each request must
return a clear JSON error response.

### Implementation for User Story 3

- [X] T017 [US3] Add bean validation rules for product input in `src/main/java/com/smartinventory/api/dto/ProductRequest.java`
- [X] T018 [US3] Implement validation error response formatting in `src/main/java/com/smartinventory/api/exception/GlobalExceptionHandler.java`
- [X] T019 [US3] Implement not-found error response formatting in `src/main/java/com/smartinventory/api/exception/GlobalExceptionHandler.java`
- [X] T020 [US3] Ensure missing-product flows throw domain-specific errors in `src/main/java/com/smartinventory/api/service/ProductService.java`

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final consistency updates across the implemented stories

- [X] T021 [P] Update implementation notes and run instructions in `src/main/resources/application.properties` and `specs/001-product-api/quickstart.md`
- [X] T022 Validate delivered behavior against `specs/001-product-api/contracts/openapi.yaml` and `specs/001-product-api/quickstart.md`
- [X] T023 Validate that no out-of-scope features were added by reviewing `src/main/java/com/smartinventory/api/controller/`, `src/main/java/com/smartinventory/api/service/`, and `src/main/java/com/smartinventory/api/model/`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user
  stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User Story 1 and User Story 2 can proceed in parallel after Phase 2
  - User Story 3 depends on the product flows from User Story 2 and shared
    error infrastructure from Phase 2
- **Polish (Phase 6)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependency
  on product CRUD flows
- **User Story 2 (P1)**: Can start after Foundational (Phase 2) - Uses the
  shared product model, DTOs, and service foundation
- **User Story 3 (P2)**: Depends on User Story 2 because validation and
  not-found errors must be exercised through product endpoints

### Within Each User Story

- Shared model and DTO foundations before story-specific controller behavior
- Service behavior before endpoint completion
- Validation and error handling before final story validation
- Story complete before moving to final polish

### Parallel Opportunities

- T003 and T004 can run in parallel during setup
- T006 can run in parallel with T007 during foundational work
- T011 and T013 can start independently after Phase 2
- T018 and T019 can run in parallel once the global exception handler exists
- T021 can run in parallel with T023 during the polish phase

---

## Parallel Example: User Story 2

```bash
Task: "Implement product create, list, retrieve, update, and delete operations in src/main/java/com/smartinventory/api/service/ProductService.java"
Task: "Map domain model to API responses in src/main/java/com/smartinventory/api/dto/ProductResponse.java"
Task: "Keep request payload structure aligned with CRUD operations in src/main/java/com/smartinventory/api/dto/ProductRequest.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Confirm `GET /api/health` returns the expected JSON

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Validate service availability
3. Add User Story 2 → Validate full product CRUD flow
4. Add User Story 3 → Validate clear JSON errors for invalid and missing data
5. Finish polish tasks and validate against the quickstart and contract

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
3. After User Story 2 is stable:
   - Developer C: User Story 3
4. Team completes Polish together

---

## Notes

- All tasks follow the required checkbox, ID, optional `[P]`, optional `[US#]`,
  and file-path format
- DTO usage is intentionally minimal and limited to contract clarity
- No task introduces database persistence, security, categories, users,
  inventory movements, or reports
