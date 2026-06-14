# Data Model: SmartInventory API Product Management

## Product

**Purpose**: Represents a catalog item managed through the API.

### Fields

- `id`: Unique identifier assigned by the application
- `name`: Required product name
- `description`: Optional descriptive text for the product
- `price`: Monetary amount greater than zero
- `stock`: Available units, zero or greater

### Validation Rules

- `name` must be present and not blank
- `price` must be greater than zero
- `stock` must be greater than or equal to zero

### State Behavior

- A product is created after valid input is accepted and an ID is assigned.
- A product remains available for list and retrieve operations until deleted.
- A product can be updated only if it already exists.
- A deleted product is removed from the in-memory collection and is no longer
  retrievable.

## Health Status

**Purpose**: Represents the operational status of the API.

### Fields

- `status`: Current service status, expected value `UP`
- `service`: Service display name, expected value `SmartInventory API`
- `version`: Service version, expected value `1.0.0`

## Error Response

**Purpose**: Represents a clear JSON error returned to clients.

### Fields

- `message`: Human-readable explanation of the error
- `path`: Request path associated with the failure
- `timestamp`: Time when the error response was produced
- `errors`: Optional field-level validation details when input validation fails

### Error Categories

- Validation error for invalid product input
- Not-found error for unknown product ID

## Relationships

- There are no cross-entity relationships in this phase.
- Health Status and Error Response are response-only structures.
