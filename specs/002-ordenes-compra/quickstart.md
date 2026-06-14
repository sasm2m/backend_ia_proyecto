# Quickstart: SmartInventory API Purchase Orders

## Purpose

Validate that SmartInventory API supports manual purchase orders, automatic
generation from stock alerts, lifecycle transitions, receipt side effects, and
operational reports while keeping state in memory.

## Prerequisites

- Java 25 installed
- Maven available on the command line
- Project dependencies resolvable from the current environment

## Setup

1. Open the project root.
2. Run the application:

```bash
mvn spring-boot:run
```

3. Wait until the API is listening on `http://localhost:8080`.

## Validation Scenarios

### 1. Create a manual purchase order

```bash
curl -s -X POST http://localhost:8080/api/purchase-orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "supplierId": 10,
    "quantity": 25,
    "notes": "Manual replenishment"
  }'
```

Expected outcome:

- Response is JSON
- Response includes a generated order `id`
- `status` is `DRAFT`
- `source` is `MANUAL`

### 2. List and retrieve orders

```bash
curl -s http://localhost:8080/api/purchase-orders
curl -s http://localhost:8080/api/purchase-orders/1
```

Expected outcome:

- List returns a JSON array
- Detail returns one JSON object
- Product, supplier, quantity, source, and status are clear in the response

### 3. Trigger automatic generation from a stock alert

```bash
curl -s -X POST http://localhost:8080/api/stock-alerts \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "currentStock": 2,
    "minimumStock": 10
  }'
```

Expected outcome:

- Alert is created or activated
- One automatic purchase order is generated in `DRAFT`
- The chosen supplier is the active supplier with the lowest delivery time
- `quantity` is `20`

### 4. Verify duplicate automatic orders are blocked

Repeat the alert creation for the same product while the previous order remains
active.

Expected outcome:

- No second automatic order is created for the same product
- The system returns or logs a clear controlled outcome instead of duplicating
  the order

### 5. Advance a valid lifecycle

```bash
curl -s -X PATCH http://localhost:8080/api/purchase-orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"SENT"}'

curl -s -X PATCH http://localhost:8080/api/purchase-orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"CONFIRMED"}'

curl -s -X PATCH http://localhost:8080/api/purchase-orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"RECEIVED"}'
```

Expected outcome:

- Each valid transition returns updated JSON
- The final transition creates one stock entry movement
- Product stock increases by the received quantity
- Linked alert is resolved if one existed

### 6. Reject invalid transitions

Try to move `DRAFT` directly to `RECEIVED` or cancel an order already in
`RECEIVED`.

Expected outcome:

- Response is a clear JSON error
- Order state remains unchanged

### 7. Validate no-supplier behavior

Raise an alert for a product with no active suppliers.

Expected outcome:

- No automatic order is created
- The alert remains active
- The system returns or logs a clear operational error

### 8. Validate reports

```bash
curl -s http://localhost:8080/api/purchase-orders/reports/pending
curl -s http://localhost:8080/api/purchase-orders/reports/supplier-delivery-times
```

Expected outcome:

- Pending report includes `daysElapsed`
- Supplier report includes average delivery time per supplier
- Empty state returns valid JSON without server failure

## References

- Contract: `specs/002-ordenes-compra/contracts/openapi.yaml`
- Data model: `specs/002-ordenes-compra/data-model.md`
- Clarifications: `specs/002-ordenes-compra/clarification.md`
