# Research: SmartInventory API Purchase Orders

## Decision 1: Keep purchase-order workflows in memory for this phase

- **Decision**: Model purchase orders, stock alerts, suppliers, and inbound
  stock movements as in-memory state managed by services inside the running
  application instance.
- **Rationale**: The constitution explicitly excludes active database
  persistence for this stage. The feature needs new business flows, not
  infrastructure setup.
- **Alternatives considered**:
  - Add Spring Data JPA and an embedded database: rejected because it violates
    the current scope and teaching baseline.
  - Use file storage: rejected because it introduces I/O complexity without
    improving the learning goal.

## Decision 2: Trigger automatic purchase orders from StockAlertService

- **Decision**: Create automatic purchase orders in `StockAlertService` after an
  alert is saved or marked active, then delegate order construction rules to
  `PurchaseOrderService`.
- **Rationale**: The alert is the business trigger. Putting the trigger in the
  alert service keeps the orchestration close to the event, while the order
  service remains the owner of order rules.
- **Alternatives considered**:
  - Trigger from controller: rejected because it places business orchestration
    in the HTTP layer.
  - Trigger from `PurchaseOrderService` without alert ownership: rejected
    because it hides the fact that the alert is the source of the workflow.

## Decision 3: Enforce lifecycle rules through an explicit state machine

- **Decision**: Represent the lifecycle with an `OrderStatus` enum and validate
  transitions centrally in `PurchaseOrderService`.
- **Rationale**: The feature has a small but strict process. A central state
  machine keeps validation testable and prevents scattered `if` logic.
- **Alternatives considered**:
  - Free-form string statuses: rejected because they are error-prone and harder
    to explain.
  - Controller-level transition checks: rejected because controllers must stay
    thin.

## Decision 4: Treat order receipt as one logical transaction

- **Decision**: Handle the `RECEIVED` transition, stock entry creation, product
  stock increment, and alert resolution inside one service operation.
- **Rationale**: These effects form one business outcome. Even in memory, the
  design should read like one transaction so a later persistent version can keep
  the same orchestration.
- **Alternatives considered**:
  - Split each side effect into separate endpoint calls: rejected because it
    risks partial completion and weakens the domain flow.
  - Fire-and-forget background handling: rejected because it is more advanced
    than the classroom scope requires.

## Decision 5: Use repository contracts without activating persistence

- **Decision**: Document repository interfaces and JPQL-equivalent query needs
  in the plan and data model, but keep the first implementation backed by
  in-memory collections.
- **Rationale**: The user requested repository query design, and documenting it
  now clarifies future filtering behavior without violating the no-database
  constraint.
- **Alternatives considered**:
  - Omit repository design entirely: rejected because it leaves report and
    duplicate-prevention logic underspecified.
  - Introduce real JPA repositories now: rejected because it changes the
    technical baseline prematurely.

## Decision 6: Keep report calculations simple and synchronous

- **Decision**: Compute pending-order and average-delivery-time reports on
  demand from current in-memory state.
- **Rationale**: The dataset is classroom-sized, and synchronous calculation is
  easy to explain and validate.
- **Alternatives considered**:
  - Precomputed metrics caches: rejected because they add complexity without
    need.
  - External reporting tooling: rejected because it is outside the approved
    scope.
