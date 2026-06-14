# Quickstart: SmartInventory API Product Management

## Purpose

Validate that SmartInventory API exposes the expected health and product
management behavior for the first in-memory practice.

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

Implementation note:

- Service name and version are configured in `src/main/resources/application.properties`.

## Validation Scenarios

### 1. Verify health endpoint

```bash
curl -s http://localhost:8080/api/health
```

Expected outcome:

- JSON response contains `status: "UP"`
- JSON response contains `service: "SmartInventory API"`
- JSON response contains `version: "1.0.0"`

### 2. Create a valid product

```bash
curl -s -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Keyboard",
    "description": "Mechanical keyboard",
    "price": 45.50,
    "stock": 10
  }'
```

Expected outcome:

- Response is JSON
- Response includes a generated `id`
- Returned values match submitted business data

### 3. List products

```bash
curl -s http://localhost:8080/api/products
```

Expected outcome:

- Response is a JSON array
- Array includes the created product

### 4. Retrieve a product by ID

```bash
curl -s http://localhost:8080/api/products/1
```

Expected outcome:

- Response is a JSON object for the requested product

### 5. Update a product

```bash
curl -s -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Keyboard Pro",
    "description": "Mechanical keyboard with backlight",
    "price": 55.00,
    "stock": 8
  }'
```

Expected outcome:

- Response is JSON
- Updated values are returned

### 6. Delete a product

```bash
curl -i -X DELETE http://localhost:8080/api/products/1
```

Expected outcome:

- Response status indicates successful deletion
- Product no longer appears in the list or retrieval flow

### 7. Validate business rules

Send a product request with:

- missing `name`
- `price` equal to `0` or below
- `stock` below `0`

Expected outcome:

- Response is a JSON error
- Error clearly identifies the validation problem

### 8. Validate missing-product errors

Call retrieve, update, and delete with a non-existent ID such as `999`.

Expected outcome:

- Response is a JSON not-found error for retrieve and update
- Delete returns the same not-found behavior instead of succeeding silently

## References

- Contract: `specs/001-product-api/contracts/openapi.yaml`
- Data model: `specs/001-product-api/data-model.md`
