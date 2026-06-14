# Implementation Plan: SmartInventory API Purchase Orders

**Branch**: `002-ordenes-compra` | **Date**: 2026-06-14 | **Spec**:
[/Users/espe/Documents/SASM2/ESPE/2026/CEDIA/PROG_BACKEND_IA/PROYFINAL/smartinventory/specs/002-ordenes-compra/spec.md](/Users/espe/Documents/SASM2/ESPE/2026/CEDIA/PROG_BACKEND_IA/PROYFINAL/smartinventory/specs/002-ordenes-compra/spec.md)

**Input**: Feature specification from `/specs/002-ordenes-compra/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See
`.specify/templates/plan-template.md` for the execution workflow.

## Summary

Extend the current in-memory Spring Boot API with a purchase-order module that
supports manual creation, automatic generation from active stock alerts, a
strict order lifecycle, inbound stock entry on receipt, and two operational
reports. Business rules stay in services, controllers remain thin, and the
design introduces repository-style query contracts only as planning artifacts so
the current phase can stay database-free while still defining the future
filtering logic clearly.

## Technical Context

**Language/Version**: Java 25

**Primary Dependencies**: Spring Boot 4.0.6, Spring Web, Spring Validation,
Jackson, Maven

**Storage**: In-memory application state for products, suppliers, stock alerts,
purchase orders, and stock entry movements

**Testing**: JUnit 5, Spring Boot Test, MockMvc

**Target Platform**: Local JVM application for backend practice and API testing

**Project Type**: Single-project REST web service

**Performance Goals**: Support classroom-scale testing with predictable JSON
responses and no noticeable delay for manual create, list, lifecycle, and
report flows

**Constraints**: Preserve layered architecture, keep controllers free of
business rules, use English names in code and API fields, validate inputs,
return clear JSON errors, keep persistence in memory, avoid users,
authentication, advanced security, and active database integration

**Scale/Scope**: One purchase-order aggregate with five lifecycle states,
automatic generation from stock alerts, one inbound stock side-effect flow, and
two report endpoints added to the current product API codebase

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- PASS: The plan keeps Java 25, Spring Boot 4.0.6, and Maven as the mandatory
  stack.
- PASS: The design remains layered with explicit controller, service, model,
  DTO, exception, and optional repository-contract responsibilities.
- PASS: Controllers stay thin by delegating creation, state transitions,
  reporting, and automatic generation orchestration to services.
- PASS: Request validation and JSON success/error responses are part of the
  design through request DTOs, response DTOs, and centralized error handling.
- PASS: Naming remains in English for classes, methods, fields, and JSON
  contracts.
- PASS: The feature stays within the approved modules: purchase orders, stock
  alerts, inbound stock movements, and operational reporting.
- PASS: The plan does not activate database persistence; repository JPQL is
  documented as future-ready contract logic, while the current phase remains
  in-memory.

## Project Structure

### Documentation (this feature)

```text
specs/002-ordenes-compra/
├── plan.md
├── research.md
├── data-model.md
├── clarification.md
├── quickstart.md
├── contracts/
│   └── openapi.yaml
└── tasks.md
```

### Source Code (repository root)

```text
src/
└── main/
    ├── java/
    │   └── com/smartinventory/api/
    │       ├── controller/
    │       │   ├── HealthController.java
    │       │   ├── ProductController.java
    │       │   ├── PurchaseOrderController.java            # new
    │       │   └── StockAlertController.java               # new if alerts are exposed
    │       ├── service/
    │       │   ├── ProductService.java
    │       │   ├── PurchaseOrderService.java               # new
    │       │   ├── StockAlertService.java                  # new
    │       │   ├── StockMovementService.java               # new
    │       │   └── SupplierService.java                    # new or stub provider lookup
    │       ├── model/
    │       │   ├── Product.java
    │       │   ├── PurchaseOrder.java                      # new
    │       │   ├── StockAlert.java                         # new
    │       │   ├── StockEntryMovement.java                 # new
    │       │   ├── Supplier.java                           # new
    │       │   └── OrderStatus.java                        # new
    │       ├── dto/
    │       │   ├── ErrorResponse.java
    │       │   ├── HealthResponse.java
    │       │   ├── ProductRequest.java
    │       │   ├── ProductResponse.java
    │       │   ├── PurchaseOrderRequest.java               # new
    │       │   ├── PurchaseOrderResponse.java              # new
    │       │   ├── OrderStatusUpdateRequest.java           # new
    │       │   ├── StockAlertRequest.java                  # new if alerts are exposed
    │       │   ├── PendingOrderReportResponse.java         # new
    │       │   └── SupplierDeliveryTimeReportResponse.java # new
    │       ├── exception/
    │       │   ├── GlobalExceptionHandler.java
    │       │   ├── ProductNotFoundException.java
    │       │   ├── PurchaseOrderNotFoundException.java     # new
    │       │   ├── InvalidOrderStatusTransitionException.java # new
    │       │   └── AutoOrderCreationException.java         # new
    │       ├── repository/
    │       │   ├── PurchaseOrderRepository.java            # new contract
    │       │   ├── StockAlertRepository.java               # new contract
    │       │   └── SupplierRepository.java                 # new contract
    │       └── SmartInventoryApiApplication.java
    └── resources/
        └── application.properties

src/
└── test/
    └── java/
        └── com/smartinventory/api/
            ├── controller/
            │   ├── HealthControllerTest.java
            │   └── PurchaseOrderControllerTest.java        # new
            └── service/
                ├── ProductServiceTest.java
                ├── PurchaseOrderServiceTest.java           # new
                └── StockAlertServiceTest.java              # new
```

**Structure Decision**: Keep the existing single Spring Boot module and extend
its current package layout. The purchase-order feature adds models, DTOs,
services, and exceptions in the same package root used by product management.
Repository files are documented as contracts for query logic, but the first
implementation can keep backing storage in memory.

## Purchase Order Design

### PurchaseOrder data model

`PurchaseOrder` will hold:

- `id`: unique identifier generated by the application
- `productId`: identifier of the product being replenished
- `productName`: snapshot name for readable responses and reports
- `supplierId`: identifier of the chosen supplier
- `supplierName`: snapshot name for readable responses and reports
- `quantity`: ordered units, must be greater than zero
- `status`: current lifecycle state as `OrderStatus`
- `source`: `MANUAL` or `AUTOMATIC`
- `stockAlertId`: optional alert identifier that triggered automatic creation
- `createdAt`: creation timestamp
- `sentAt`: optional timestamp for transition to `SENT`
- `confirmedAt`: optional timestamp for transition to `CONFIRMED`
- `receivedAt`: optional timestamp for transition to `RECEIVED`
- `cancelledAt`: optional timestamp for transition to `CANCELLED`
- `notes`: optional operational notes for manual context or lifecycle comments

### OrderStatus enum

`OrderStatus` includes exactly:

- `DRAFT`
- `SENT`
- `CONFIRMED`
- `RECEIVED`
- `CANCELLED`

### State machine

Valid transitions:

- `DRAFT -> SENT`
- `DRAFT -> CANCELLED`
- `SENT -> CONFIRMED`
- `SENT -> CANCELLED`
- `CONFIRMED -> RECEIVED`
- `CONFIRMED -> CANCELLED`

Invalid transitions that must be rejected with a clear error:

- Any transition from `RECEIVED`
- Any transition from `CANCELLED`
- `DRAFT -> CONFIRMED`
- `DRAFT -> RECEIVED`
- `SENT -> RECEIVED`
- `RECEIVED -> CANCELLED`

### Automatic order creation decision

Automatic generation belongs in `StockAlertService` immediately after a stock
alert is created or re-activated. The flow is:

1. Save or update the active stock alert in memory.
2. Check whether the product already has an active purchase order.
3. Resolve active suppliers for the product.
4. Select the supplier with the lowest delivery time.
5. Calculate suggested quantity as `minimumStock * 2`.
6. Delegate to `PurchaseOrderService` to create the automatic order in
   `DRAFT`.

This keeps alert detection and alert-triggered orchestration together, while
keeping purchase-order business rules in `PurchaseOrderService`.

### Reception with side effects

The `RECEIVED` transition must execute as one service-level operation:

1. Validate the current order state can move to `RECEIVED`.
2. Update the order state and set `receivedAt`.
3. Create an inbound `StockEntryMovement` record.
4. Increase the related product stock.
5. Resolve the associated stock alert when one exists.

Although this phase is in-memory, the code should be structured as one logical
transaction inside the service so the later persistence-backed version can wrap
the same orchestration in a real database transaction without changing the API
contract.

### Repository query contracts

To respect the no-database rule for this phase, these queries are planning
artifacts only. They define the filtering logic that repository abstractions
must expose now, even if the first implementation uses in-memory collections.

- `PurchaseOrderRepository.findActiveByProductId(productId)`:
  select `po` where `po.product.id = :productId` and `po.status not in
  ('RECEIVED', 'CANCELLED')`
- `PurchaseOrderRepository.findPendingOrders()`:
  select `po` where `po.status in ('DRAFT', 'SENT', 'CONFIRMED')` ordered by
  `po.createdAt asc`
- `PurchaseOrderRepository.findReceivedBySupplier()`:
  select supplier name plus average of `datediff(po.receivedAt, po.sentAt)` or
  the equivalent duration function for received orders with both timestamps
- `StockAlertRepository.findActiveByProductId(productId)`:
  select `sa` where `sa.product.id = :productId` and `sa.active = true`
- `SupplierRepository.findActiveByProductIdOrderByDeliveryTime(productId)`:
  select suppliers linked to the product where active is true ordered by
  `deliveryTimeDays asc`

## Files To Add Or Modify

### New files

- `src/main/java/com/smartinventory/api/controller/PurchaseOrderController.java`
- `src/main/java/com/smartinventory/api/service/PurchaseOrderService.java`
- `src/main/java/com/smartinventory/api/service/StockAlertService.java`
- `src/main/java/com/smartinventory/api/service/StockMovementService.java`
- `src/main/java/com/smartinventory/api/service/SupplierService.java`
- `src/main/java/com/smartinventory/api/model/PurchaseOrder.java`
- `src/main/java/com/smartinventory/api/model/OrderStatus.java`
- `src/main/java/com/smartinventory/api/model/StockAlert.java`
- `src/main/java/com/smartinventory/api/model/StockEntryMovement.java`
- `src/main/java/com/smartinventory/api/model/Supplier.java`
- `src/main/java/com/smartinventory/api/dto/PurchaseOrderRequest.java`
- `src/main/java/com/smartinventory/api/dto/PurchaseOrderResponse.java`
- `src/main/java/com/smartinventory/api/dto/OrderStatusUpdateRequest.java`
- `src/main/java/com/smartinventory/api/dto/PendingOrderReportResponse.java`
- `src/main/java/com/smartinventory/api/dto/SupplierDeliveryTimeReportResponse.java`
- `src/main/java/com/smartinventory/api/exception/PurchaseOrderNotFoundException.java`
- `src/main/java/com/smartinventory/api/exception/InvalidOrderStatusTransitionException.java`
- `src/main/java/com/smartinventory/api/exception/AutoOrderCreationException.java`
- `src/main/java/com/smartinventory/api/repository/PurchaseOrderRepository.java`
- `src/main/java/com/smartinventory/api/repository/StockAlertRepository.java`
- `src/main/java/com/smartinventory/api/repository/SupplierRepository.java`
- `src/test/java/com/smartinventory/api/controller/PurchaseOrderControllerTest.java`
- `src/test/java/com/smartinventory/api/service/PurchaseOrderServiceTest.java`
- `src/test/java/com/smartinventory/api/service/StockAlertServiceTest.java`

### Existing files to modify

- `src/main/java/com/smartinventory/api/model/Product.java`
  - add fields needed for replenishment such as `minimumStock`
- `src/main/java/com/smartinventory/api/service/ProductService.java`
  - support stock increment when an order is received
- `src/main/java/com/smartinventory/api/exception/GlobalExceptionHandler.java`
  - map order and transition errors to clear JSON responses
- `src/main/resources/application.properties`
  - add any simple config values needed for service name/version consistency
- `pom.xml`
  - no new dependency required for the plan phase; keep current stack unless a
    later amendment authorizes persistence

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | Not applicable | Not applicable |
