# Clarification Notes: Modulo de Ordenes de Compra

**Created**: 2026-06-14
**Feature**: [spec.md](/Users/espe/Documents/SASM2/ESPE/2026/CEDIA/PROG_BACKEND_IA/PROYFINAL/smartinventory/specs/002-ordenes-compra/spec.md)

## Clarified Decisions

1. **Que pasa si se intenta cancelar una orden `RECEIVED`?**  
   La operacion se rechaza con un error claro. Una orden recibida ya produjo el
   movimiento de entrada y no puede volver atras en este alcance.

2. **La orden automatica se crea en que estado?**  
   Se crea en `DRAFT` para mantener consistencia con las ordenes manuales y
   permitir una revision operativa simple.

3. **Que pasa si el producto no tiene proveedores activos?**  
   No se crea orden automatica. La alerta permanece activa y el sistema debe
   devolver o registrar un error operativo claro.

4. **Que pasa si ya existe una orden activa al dispararse una alerta?**  
   No se crea una nueva orden automatica. El evento se omite de forma
   controlada para evitar duplicados del mismo producto.

5. **La cantidad sugerida es configurable?**  
   No en esta fase. La regla queda fija en `minimumStock × 2`.

## Impact on Specification

- Se aclaro el comportamiento de cancelacion para ordenes `RECEIVED`.
- Se fijo el estado inicial de ordenes automaticas en `DRAFT`.
- Se definio la respuesta ante ausencia de proveedores activos.
- Se reforzo la regla de no duplicar ordenes activas por producto.
- Se fijo la no configurabilidad de la cantidad sugerida en esta etapa.
