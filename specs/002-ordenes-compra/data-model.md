# Data Model: SmartInventory API Purchase Orders

## PurchaseOrder

**Purpose**: Represents a replenishment order created manually or
automatically.

### Fields

- `id`: Unique identifier assigned by the application
- `productId`: Referenced product identifier
- `productName`: Product name snapshot used in responses and reports
- `supplierId`: Referenced supplier identifier
- `supplierName`: Supplier name snapshot used in responses and reports
- `quantity`: Ordered units
- `status`: Lifecycle value from `OrderStatus`
- `source`: `MANUAL` or `AUTOMATIC`
- `stockAlertId`: Optional linked stock alert identifier
- `createdAt`: Timestamp when the order was created
- `sentAt`: Timestamp when the order moved to `SENT`
- `confirmedAt`: Timestamp when the order moved to `CONFIRMED`
- `receivedAt`: Timestamp when the order moved to `RECEIVED`
- `cancelledAt`: Timestamp when the order moved to `CANCELLED`
- `notes`: Optional comments

### Validation Rules

- `productId` must reference an existing product
- `supplierId` must reference an active supplier for manual creation
- `quantity` must be greater than zero
- `status` must always be a valid `OrderStatus`
- `stockAlertId` is required for automatic orders and optional for manual ones

### State Transitions

- Allowed:
  - `DRAFT -> SENT`
  - `DRAFT -> CANCELLED`
  - `SENT -> CONFIRMED`
  - `SENT -> CANCELLED`
  - `CONFIRMED -> RECEIVED`
  - `CONFIRMED -> CANCELLED`
- Rejected:
  - Any transition from `RECEIVED`
  - Any transition from `CANCELLED`
  - `DRAFT -> CONFIRMED`
  - `DRAFT -> RECEIVED`
  - `SENT -> RECEIVED`

## OrderStatus

**Purpose**: Defines the legal lifecycle states for a purchase order.

### Values

- `DRAFT`
- `SENT`
- `CONFIRMED`
- `RECEIVED`
- `CANCELLED`

## StockAlert

**Purpose**: Represents a low-stock condition that can trigger automatic
replenishment.

### Fields

- `id`: Unique identifier
- `productId`: Product under alert
- `currentStock`: Stock value observed when alert was raised
- `minimumStock`: Threshold used for replenishment logic
- `active`: Whether the alert still requires action
- `createdAt`: Creation timestamp
- `resolvedAt`: Optional resolution timestamp
- `resolutionOrderId`: Optional purchase order that resolved the alert

### Business Behavior

- An active alert can trigger automatic order generation after being saved.
- An alert stays active when no supplier is available.
- An alert is resolved when its associated order is received.

## Supplier

**Purpose**: Represents a product supplier candidate for manual and automatic
purchase orders.

### Fields

- `id`: Unique identifier
- `name`: Supplier name
- `active`: Whether the supplier can be selected
- `deliveryTimeDays`: Expected delivery time in days
- `productIds`: Products this supplier can supply

### Selection Rule

- Automatic generation chooses the active supplier with the smallest
  `deliveryTimeDays` for the product.

## StockEntryMovement

**Purpose**: Represents the inbound inventory effect created when an order is
received.

### Fields

- `id`: Unique identifier
- `purchaseOrderId`: Source order identifier
- `productId`: Updated product identifier
- `quantity`: Units added to stock
- `createdAt`: Creation timestamp
- `reason`: Fixed reason such as `PURCHASE_ORDER_RECEIPT`

## Product Extension

**Purpose**: Extends the current product model so replenishment rules can work.

### Additional Field Needed

- `minimumStock`: Threshold used to raise alerts and calculate automatic order
  quantity

### Existing Product Rules Still Apply

- `name` required
- `price` greater than zero
- `stock` greater than or equal to zero

## Report Projections

### PendingOrderReportRow

- `purchaseOrderId`
- `productName`
- `supplierName`
- `status`
- `daysElapsed`
- `createdAt`

### SupplierDeliveryTimeReportRow

- `supplierId`
- `supplierName`
- `averageDeliveryDays`
- `receivedOrdersCount`

## Relationships

- One `Product` can have many `StockAlert` records over time.
- One `Product` can have many `PurchaseOrder` records.
- One `Supplier` can support many `Product` entries, and one product can have
  multiple suppliers.
- One `PurchaseOrder` may reference one `StockAlert`.
- One `PurchaseOrder` creates at most one `StockEntryMovement` when received.
