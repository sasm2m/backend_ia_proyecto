# Tasks: SmartInventory API Purchase Orders

**Input**: Design documents from `/specs/002-ordenes-compra/`

**Prerequisites**: plan.md (required), spec.md (required for user stories),
research.md, data-model.md, contracts/, clarification.md, quickstart.md

**Tests**: Tests are included because the user explicitly requested service and
controller coverage for this feature.

**Organization**: Tasks are grouped by user story to enable independent
implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Prepare feature-specific packages and API documentation support

- [ ] T001 Create repository package placeholder and feature folder structure in `src/main/java/com/smartinventory/api/repository/.gitkeep`
- [ ] T002 Configure Swagger/OpenAPI annotations support for purchase-order endpoints in `pom.xml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core domain pieces that block every purchase-order story

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 Implement TASK-01 `OrderStatus` enum in `src/main/java/com/smartinventory/api/model/OrderStatus.java`
- [ ] T004 [P] Implement TASK-02 base `PurchaseOrder` entity in `src/main/java/com/smartinventory/api/model/PurchaseOrder.java`
- [ ] T005 [P] Extend replenishment support in `src/main/java/com/smartinventory/api/model/Product.java`, `src/main/java/com/smartinventory/api/model/StockAlert.java`, `src/main/java/com/smartinventory/api/model/StockEntryMovement.java`, and `src/main/java/com/smartinventory/api/model/Supplier.java`
- [ ] T006 Implement TASK-03 purchase-order DTOs in `src/main/java/com/smartinventory/api/dto/PurchaseOrderRequest.java`, `src/main/java/com/smartinventory/api/dto/PurchaseOrderResponse.java`, `src/main/java/com/smartinventory/api/dto/OrderStatusUpdateRequest.java`, `src/main/java/com/smartinventory/api/dto/PendingOrderReportResponse.java`, and `src/main/java/com/smartinventory/api/dto/SupplierDeliveryTimeReportResponse.java`
- [ ] T007 [P] Add purchase-order exception types in `src/main/java/com/smartinventory/api/exception/PurchaseOrderNotFoundException.java`, `src/main/java/com/smartinventory/api/exception/InvalidOrderStatusTransitionException.java`, and `src/main/java/com/smartinventory/api/exception/AutoOrderCreationException.java`
- [ ] T008 Update shared JSON error mapping for order and transition failures in `src/main/java/com/smartinventory/api/exception/GlobalExceptionHandler.java`
- [ ] T009 Implement TASK-04 repository contracts with JPQL query definitions in `src/main/java/com/smartinventory/api/repository/PurchaseOrderRepository.java`, `src/main/java/com/smartinventory/api/repository/StockAlertRepository.java`, and `src/main/java/com/smartinventory/api/repository/SupplierRepository.java`

**Checkpoint**: Foundation ready - purchase-order stories can now be implemented

---

## Phase 3: User Story 1 - Crear y consultar ordenes manuales (Priority: P1) 🎯 MVP

**Goal**: Allow manual purchase-order creation and retrieval with clear JSON
responses

**Independent Test**: Create a manual order in `DRAFT`, then list it and fetch
its detail successfully

### Tests for User Story 1

- [ ] T010 [P] [US1] Add manual order service tests in `src/test/java/com/smartinventory/api/service/PurchaseOrderServiceTest.java`
- [ ] T011 [P] [US1] Add manual order controller tests in `src/test/java/com/smartinventory/api/controller/PurchaseOrderControllerTest.java`

### Implementation for User Story 1

- [ ] T012 [US1] Implement TASK-05 manual creation and retrieval flows in `src/main/java/com/smartinventory/api/service/PurchaseOrderService.java`
- [ ] T013 [US1] Implement TASK-07 manual order endpoints with Swagger annotations in `src/main/java/com/smartinventory/api/controller/PurchaseOrderController.java`

**Checkpoint**: User Story 1 should be functional and testable independently

---

## Phase 4: User Story 2 - Generar ordenes automaticas desde alertas de stock (Priority: P1)

**Goal**: Generate automatic purchase orders from active stock alerts without
duplicates

**Independent Test**: Save an active stock alert and verify one automatic order
in `DRAFT` is created with the lowest-delivery supplier and `minimumStock * 2`

### Tests for User Story 2

- [ ] T014 [P] [US2] Add automatic generation service tests in `src/test/java/com/smartinventory/api/service/StockAlertServiceTest.java`

### Implementation for User Story 2

- [ ] T015 [US2] Implement automatic generation rules in `src/main/java/com/smartinventory/api/service/PurchaseOrderService.java`
- [ ] T016 [US2] Implement TASK-06 alert-trigger integration after saving alerts in `src/main/java/com/smartinventory/api/service/StockAlertService.java`

**Checkpoint**: User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - Gestionar el ciclo de vida y la recepcion de ordenes (Priority: P1)

**Goal**: Enforce valid lifecycle transitions and apply receipt side effects as
one logical operation

**Independent Test**: Move an order through `DRAFT -> SENT -> CONFIRMED -> RECEIVED`
and verify stock entry creation, stock increment, and alert resolution

### Tests for User Story 3

- [ ] T017 [P] [US3] Extend service lifecycle and receipt tests in `src/test/java/com/smartinventory/api/service/PurchaseOrderServiceTest.java`
- [ ] T018 [P] [US3] Extend controller status transition tests in `src/test/java/com/smartinventory/api/controller/PurchaseOrderControllerTest.java`

### Implementation for User Story 3

- [ ] T019 [US3] Complete TASK-05 state machine and receipt side effects in `src/main/java/com/smartinventory/api/service/PurchaseOrderService.java`
- [ ] T020 [US3] Implement inbound stock movement orchestration in `src/main/java/com/smartinventory/api/service/StockMovementService.java` and update stock increment behavior in `src/main/java/com/smartinventory/api/service/ProductService.java`

**Checkpoint**: User Stories 1, 2, and 3 should be independently functional

---

## Phase 6: User Story 4 - Consultar reportes operativos de compras (Priority: P2)

**Goal**: Expose pending-order and supplier-delivery reports as clear JSON
endpoints

**Independent Test**: Query both report endpoints and verify pending days and
average delivery time values are returned or empty JSON is returned safely

### Tests for User Story 4

- [ ] T021 [P] [US4] Add report endpoint controller tests in `src/test/java/com/smartinventory/api/controller/ReportControllerTest.java`

### Implementation for User Story 4

- [ ] T022 [US4] Implement report aggregation queries and projections in `src/main/java/com/smartinventory/api/service/PurchaseOrderService.java`
- [ ] T023 [US4] Implement TASK-08 report endpoints with Swagger annotations in `src/main/java/com/smartinventory/api/controller/ReportController.java`

**Checkpoint**: All user stories should now be independently functional

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final verification and feature-wide cleanup

- [ ] T024 [P] Implement TASK-09 final service and controller test refinements in `src/test/java/com/smartinventory/api/service/PurchaseOrderServiceTest.java`, `src/test/java/com/smartinventory/api/service/StockAlertServiceTest.java`, `src/test/java/com/smartinventory/api/controller/PurchaseOrderControllerTest.java`, and `src/test/java/com/smartinventory/api/controller/ReportControllerTest.java`
- [ ] T025 Validate quickstart scenarios and scope constraints against `specs/002-ordenes-compra/quickstart.md` and `src/main/java/com/smartinventory/api/`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: Depend on Foundational completion
- **Polish (Phase 7)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Starts after Foundational - no dependency on other stories
- **User Story 2 (P1)**: Starts after Foundational and depends on `PurchaseOrderService` base behavior from US1
- **User Story 3 (P1)**: Starts after Foundational and depends on order model/service behavior from US1
- **User Story 4 (P2)**: Starts after Foundational and depends on accumulated order data from US1-US3

### Within Each User Story

- Tests should be written before or alongside implementation and must fail before completion
- Models and DTOs before services
- Services before controllers
- Lifecycle and side effects before report aggregation
- Story complete before treating it as a dependency for later stories

### Parallel Opportunities

- `T004`, `T005`, `T006`, and `T007` can run in parallel after `T003`
- `T010` and `T011` can run in parallel
- `T017` and `T018` can run in parallel
- `T021` can run in parallel with `T022` once report contracts are clear
- `T024` can run in parallel across test files

---

## Parallel Example: User Story 1

```bash
Task: "Add manual order service tests in src/test/java/com/smartinventory/api/service/PurchaseOrderServiceTest.java"
Task: "Add manual order controller tests in src/test/java/com/smartinventory/api/controller/PurchaseOrderControllerTest.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. Validate manual creation and retrieval end to end

### Incremental Delivery

1. Finish Setup + Foundational
2. Deliver User Story 1
3. Add automatic generation in User Story 2
4. Add lifecycle and receipt side effects in User Story 3
5. Add reports in User Story 4
6. Finish with final test hardening and quickstart validation

### Parallel Team Strategy

1. One developer finishes Setup + Foundational
2. Another developer prepares US1 tests while service scaffolding is added
3. After US1, automatic generation and lifecycle work can split between
   `StockAlertService` and `PurchaseOrderService`
4. Reporting can begin once stable lifecycle data exists

---

## Notes

- All tasks follow the required checklist format
- `[P]` means different files and low coupling at the time the task starts
- User story labels provide traceability from spec to implementation
- Controllers must stay thin; business logic belongs in services
- Do not add users, authentication, advanced security, or real database persistence
