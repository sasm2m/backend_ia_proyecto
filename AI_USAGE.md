# Uso de IA en el Proyecto Final

## Herramienta utilizada
Codex

## Flujo SDD aplicado

### Artefactos generados con IA
- constitution.md: []
Prompt
Lee .specify/memory/constitution.md del proyecto SmartInventory.
Crea specs/002-ordenes-compra/spec.md para el módulo de Órdenes de Compra con estas características:
- Crear órdenes manualmente (producto + proveedor + cantidad)
- Generar órdenes automáticamente cuando hay alerta de stock activa, seleccionando el proveedor con menor tiempo de entrega
- Ciclo de vida: DRAFT → SENT → CONFIRMED → RECEIVED → CANCELLED
- Al recibir una orden: crear automáticamente un movimiento de entrada de stock y resolver la alerta asociada si existe
- No crear orden automática si ya hay una activa para ese producto
- Cantidad sugerida en orden automática: minimumStock × 2
- Reportes: órdenes pendientes con días transcurridos, tiempo promedio de entrega por proveedor

Incluye: descripción, historias de usuario, criterios de aceptación, endpoints y casos borde.
Cambios realizados: 
    - Tomar en cuena el ciclo de vida de los eventos para la baja de inventarios, cambie las ambiguedades del archivo constitution.md para que se pueda realizar el módulo de órdenes de compra.
    - Impedir que la cantidad final sea despachada sin considerar el ultimo valor disponible, es decir si la orden requiere 6 y el stock es 5, solo hacerlo al stock y el que no pueda responder dejar para la orden de reposición (siguiente módulo).

- spec.md: []
  Se generó correctamente cada uno de las clases, historias de usuario y endpoints, se tuvo que usar la clarificación para evitar problemas sobre todo cuando se vayan a especificar las tareas. Se tuvo que afinar un poco en los requerimientos funcionales, para que tenga en efecto la sollicitud automatica de stock, los valores minimos de producto.
- plan.md: [qué decisiones técnicas validaste]
  Las reglas de negocio de PurchaseOrderService, que eran las partes fundamentales de este nuevo feature.
- tasks.md: [cómo organizó las tareas], bajo orden de precedencia, desde la generación del ENUM por tipo de servicios para el alta de stock.

### Implementación

#### Lo que el agente generó correctamente sin corrección
- [lista de clases o métodos]
PurchaseOrderController
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


#### Lo que el agente generó incorrectamente y cómo lo corregiste
- En los test para determinar la persistencia en base de datos.
- En los servicios de StockAlertService, para de alerte correctamente el producto que se queda sin stock

#### Decisiones que tomé yo que el agente no podía tomar
- elegir generar la orden automática en StockAlertService.
- definir cantidad sugerida = minimumStock × 2

## Qué aprendí sobre trabajar con agentes
Que se debe conocer claramente las reglas de negocio para poderlas especificar correctamente. 
Que las tareas deben manetener un desacoplamiento para que el agende decida si las hace en paralelo o no. 
La constitucion de al aplicación debe ser siempre revisada para que no haya ambiguedades. 
