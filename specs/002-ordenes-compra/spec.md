# Feature Specification: Modulo de Ordenes de Compra

**Feature Branch**: `002-ordenes-compra`

**Created**: 2026-06-14

**Status**: Draft

**Input**: User description: "Modulo de ordenes de compra con creacion manual,
generacion automatica por alerta de stock, ciclo de vida, recepcion con entrada
de stock y reportes operativos"

## Functional Description

El modulo de Ordenes de Compra permite registrar ordenes manuales para
reabastecimiento y generar ordenes automaticas cuando existe una alerta de stock
activa. El sistema debe controlar el ciclo de vida completo de cada orden,
evitar ordenes automaticas duplicadas para un mismo producto y registrar los
efectos operativos cuando una orden es recibida.

El modulo tambien debe ofrecer visibilidad operativa sobre ordenes pendientes y
el desempeno de entrega de los proveedores para apoyar decisiones de compra sin
introducir usuarios, autenticacion ni persistencia fuera de memoria.

## Clarifications

### Session 2026-06-14

- Q: Que pasa si se intenta cancelar una orden `RECEIVED`? → A: La operacion se
  rechaza con un error claro porque una orden recibida ya produjo efectos
  operativos irreversibles para este alcance.
- Q: La orden automatica se crea en que estado? → A: La orden automatica se crea
  en estado `DRAFT` para mantener un punto de revision consistente con las
  ordenes manuales.
- Q: Que pasa si el producto no tiene proveedores activos? → A: No se crea orden
  automatica, la alerta permanece activa y el sistema devuelve o registra un
  error operativo claro.
- Q: Que pasa si ya existe una orden activa al dispararse una alerta? → A: No se
  crea una nueva orden automatica; el evento se trata como una omision
  controlada para evitar duplicados.
- Q: La cantidad sugerida es configurable? → A: No en esta fase; la cantidad
  sugerida es una regla fija de `minimumStock × 2`.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Crear y consultar ordenes manuales (Priority: P1)

Como responsable de compras, quiero crear una orden manual indicando producto,
proveedor y cantidad para registrar una necesidad de compra directa sin esperar
una regla automatica.

**Why this priority**: La creacion manual entrega el flujo base del modulo y
permite operar compras aun cuando no intervienen alertas automáticas.

**Independent Test**: Se puede probar creando una orden manual valida y luego
consultando su detalle y el listado general de ordenes.

**Acceptance Scenarios**:

1. **Given** un producto, un proveedor y una cantidad valida, **When** se crea
   una orden manual, **Then** la orden queda registrada con estado inicial
   `DRAFT`.
2. **Given** una orden manual existente, **When** se consulta el detalle o el
   listado de ordenes, **Then** la informacion de producto, proveedor, cantidad
   y estado se devuelve de forma clara.

---

### User Story 2 - Generar ordenes automaticas desde alertas de stock (Priority: P1)

Como sistema de abastecimiento, quiero generar ordenes automaticas cuando hay
una alerta de stock activa para asegurar reposicion oportuna usando el proveedor
con menor tiempo de entrega.

**Why this priority**: La generacion automatica convierte una alerta operativa
en una accion concreta de reabastecimiento y evita quiebres de stock.

**Independent Test**: Se puede probar activando una alerta de stock para un
producto con proveedores disponibles y verificando la creacion de una sola orden
automatica con cantidad sugerida y proveedor seleccionados correctamente.

**Acceptance Scenarios**:

1. **Given** una alerta de stock activa y multiples proveedores para un
   producto, **When** el sistema genera una orden automatica, **Then** elige al
   proveedor con menor tiempo de entrega.
2. **Given** una alerta de stock activa para un producto con `minimumStock`
   definido, **When** el sistema genera una orden automatica, **Then** la
   cantidad sugerida es `minimumStock × 2`.
3. **Given** un producto con una orden activa existente, **When** se intenta
   generar otra orden automatica por alerta, **Then** el sistema no crea una
   nueva orden para ese producto.
4. **Given** una alerta activa para un producto sin proveedores activos,
   **When** el sistema intenta generar una orden automatica, **Then** no crea la
   orden y deja la alerta activa con un error operativo claro.

---

### User Story 3 - Gestionar el ciclo de vida y la recepcion de ordenes (Priority: P1)

Como responsable operativo, quiero actualizar el estado de una orden a traves de
su ciclo de vida para reflejar envio, confirmacion, recepcion o cancelacion y
disparar los efectos correspondientes al recibirla.

**Why this priority**: El valor del modulo depende de representar el progreso
real de la orden y aplicar sus efectos operativos cuando llega el inventario.

**Independent Test**: Se puede probar moviendo una orden por estados validos
y verificando que al recibirla se cree un movimiento de entrada y se resuelva la
alerta asociada si existe.

**Acceptance Scenarios**:

1. **Given** una orden en `DRAFT`, **When** se actualiza siguiendo una
   transicion valida, **Then** el nuevo estado queda registrado en el ciclo de
   vida permitido.
2. **Given** una orden que llega a `RECEIVED`, **When** se confirma la
   recepcion, **Then** el sistema crea automaticamente un movimiento de entrada
   de stock.
3. **Given** una orden recibida asociada a una alerta activa, **When** se
   completa la recepcion, **Then** la alerta queda resuelta.
4. **Given** una orden cancelada o recibida, **When** se intenta mover a un
   estado incompatible, **Then** el sistema rechaza la transicion con un error
   claro.
5. **Given** una orden en estado `RECEIVED`, **When** se intenta cancelarla,
   **Then** el sistema rechaza la operacion con un error claro.

---

### User Story 4 - Consultar reportes operativos de compras (Priority: P2)

Como responsable de compras, quiero consultar reportes de ordenes pendientes y
tiempos promedio de entrega por proveedor para priorizar seguimiento y evaluar
desempeno de abastecimiento.

**Why this priority**: Los reportes agregan visibilidad operativa despues de que
el flujo principal de ordenes ya existe.

**Independent Test**: Se puede probar consultando el reporte de ordenes
pendientes y el reporte de tiempo promedio de entrega por proveedor a partir de
ordenes con estados e historial suficientes.

**Acceptance Scenarios**:

1. **Given** ordenes no finalizadas, **When** se consulta el reporte de
   pendientes, **Then** el sistema devuelve las ordenes junto con los dias
   transcurridos desde su creacion.
2. **Given** ordenes recibidas de distintos proveedores, **When** se consulta el
   reporte de tiempos de entrega, **Then** el sistema devuelve el promedio de
   entrega por proveedor.

### Edge Cases

- Si se intenta crear una orden manual con producto, proveedor o cantidad
  faltantes o invalidos, el sistema rechaza la solicitud con un error claro.
- Si una orden intenta cambiar a un estado fuera del flujo
  `DRAFT → SENT → CONFIRMED → RECEIVED → CANCELLED`, el sistema rechaza la
  transicion con un error claro.
- Si dos alertas activas intentan generar la misma orden automatica para un
  producto que ya tiene una orden activa, el sistema conserva una sola orden
  activa y omite la nueva generacion.
- Si un producto no tiene proveedores activos para la generacion automatica, la
  alerta permanece activa y no se crea una orden automatica.
- Si se recibe una orden que no tiene alerta asociada, el movimiento de entrada
  se crea igual y no se intenta resolver una alerta inexistente.
- Si no existen ordenes pendientes o no hay historial suficiente para calcular
  promedios de entrega, el sistema devuelve resultados vacios o sin promedio
  calculado en lugar de fallar.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir crear una orden de compra manual con
  producto, proveedor y cantidad.
- **FR-002**: El sistema MUST registrar las ordenes de compra con un ciclo de
  vida compuesto por `DRAFT`, `SENT`, `CONFIRMED`, `RECEIVED` y `CANCELLED`.
- **FR-003**: El sistema MUST permitir consultar el listado de ordenes y el
  detalle de una orden individual.
- **FR-004**: El sistema MUST generar una orden automatica cuando exista una
  alerta de stock activa y el producto no tenga ya una orden activa.
- **FR-005**: El sistema MUST seleccionar para la orden automatica al proveedor
  con menor tiempo de entrega.
- **FR-006**: El sistema MUST sugerir para la orden automatica una cantidad igual
  a `minimumStock × 2`.
- **FR-007**: El sistema MUST crear la orden automatica en estado `DRAFT`.
- **FR-008**: El sistema MUST rechazar la generacion automatica de una nueva
  orden cuando ya exista una orden activa para el mismo producto.
- **FR-009**: El sistema MUST no crear una orden automatica cuando el producto no
  tenga proveedores activos y debe conservar la alerta activa.
- **FR-010**: El sistema MUST validar transiciones de estado para evitar cambios
  incompatibles con el ciclo de vida definido.
- **FR-011**: El sistema MUST rechazar la cancelacion de una orden en estado
  `RECEIVED`.
- **FR-012**: El sistema MUST crear automaticamente un movimiento de entrada de
  stock cuando una orden pase a `RECEIVED`.
- **FR-013**: El sistema MUST resolver la alerta asociada cuando una orden
  recibida provenga de una necesidad de reabastecimiento con alerta activa.
- **FR-014**: El sistema MUST ofrecer un reporte de ordenes pendientes con dias
  transcurridos.
- **FR-015**: El sistema MUST ofrecer un reporte del tiempo promedio de entrega
  por proveedor.
- **FR-016**: El sistema MUST tratar la cantidad sugerida automatica como una
  regla fija y no configurable en esta fase.
- **FR-017**: El sistema MUST devolver respuestas JSON claras para operaciones
  exitosas y errores del modulo.
- **FR-018**: El sistema MUST mantener el modulo dentro del alcance aprobado por
  la constitucion, sin introducir usuarios, autenticacion, seguridad avanzada ni
  base de datos.

### Business Rules

- **BR-001**: Una orden manual requiere producto, proveedor y cantidad.
- **BR-002**: Una orden automatica solo puede crearse si existe una alerta de
  stock activa para el producto.
- **BR-003**: La cantidad sugerida para una orden automatica es
  `minimumStock × 2`.
- **BR-004**: La orden automatica debe elegir el proveedor con menor tiempo de
  entrega.
- **BR-005**: La orden automatica siempre inicia en `DRAFT`.
- **BR-006**: No se puede crear una nueva orden automatica si ya existe una
  orden activa para el mismo producto.
- **BR-007**: Si no hay proveedores activos para el producto, no se crea orden
  automatica y la alerta continua activa.
- **BR-008**: Una orden recibida no puede cancelarse.
- **BR-009**: La cantidad sugerida no es configurable en esta fase.
- **BR-010**: Una orden recibida genera automaticamente un movimiento de entrada
  de stock.
- **BR-011**: Una orden recibida resuelve su alerta asociada cuando dicha alerta
  existe.
- **BR-012**: Las transiciones de estado invalidas deben rechazarse con un error
  claro.

### Key Entities *(include if feature involves data)*

- **Purchase Order**: Orden de compra con identificador, producto, proveedor,
  cantidad, origen manual o automatico, estado y fechas relevantes del ciclo de
  vida.
- **Supplier**: Proveedor elegible para abastecimiento con nombre y tiempo de
  entrega promedio o esperado.
- **Stock Alert**: Alerta activa asociada a un producto que habilita la
  generacion automatica de una orden de compra.
- **Stock Entry Movement**: Movimiento de entrada generado al recibir una orden
  de compra.
- **Purchase Order Report Row**: Resultado de reporte con metricas de seguimiento
  como dias transcurridos o tiempo promedio de entrega.

## Expected REST Endpoints

- `POST /api/purchase-orders`: Crear una orden manual.
- `GET /api/purchase-orders`: Listar ordenes de compra.
- `GET /api/purchase-orders/{id}`: Consultar una orden de compra por ID.
- `PATCH /api/purchase-orders/{id}/status`: Actualizar el estado de una orden.
- `POST /api/purchase-orders/auto-generation`: Generar ordenes automaticas para
  alertas activas elegibles.
- `GET /api/purchase-orders/reports/pending`: Consultar ordenes pendientes con
  dias transcurridos.
- `GET /api/purchase-orders/reports/supplier-delivery-times`: Consultar tiempo
  promedio de entrega por proveedor.

## Out of Scope *(mandatory)*

- Gestion de usuarios
- Autenticacion
- Seguridad avanzada o control de acceso
- Persistencia en base de datos
- Funcionalidades fuera de los modulos aprobados por la constitucion actual

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% de las ordenes manuales validas pueden crearse y consultarse
  con la informacion esperada del producto, proveedor, cantidad y estado.
- **SC-002**: 100% de las alertas elegibles generan como maximo una orden
  automatica activa por producto.
- **SC-003**: 100% de las ordenes automaticas seleccionan al proveedor con menor
  tiempo de entrega y calculan la cantidad sugerida segun la regla definida.
- **SC-004**: 100% de las recepciones validas crean un movimiento de entrada y
  resuelven la alerta asociada cuando existe.
- **SC-005**: Los reportes de ordenes pendientes y tiempos de entrega permiten
  identificar seguimiento y desempeno por proveedor sin revisar orden por orden.

## Assumptions

- Un producto puede tener multiples proveedores potenciales para
  reabastecimiento.
- El concepto de orden activa incluye ordenes que aun no estan en `RECEIVED` ni
  `CANCELLED`.
- El tiempo de entrega del proveedor ya esta disponible para la logica de
  seleccion automatica.
- Un proveedor sin estado activo no es elegible para la generacion automatica de
  ordenes.
- La creacion del movimiento de entrada y la resolucion de la alerta forman
  parte del mismo flujo operativo al recibir una orden.
- Los reportes se calculan sobre la informacion disponible en memoria dentro de
  la misma instancia de la aplicacion.
