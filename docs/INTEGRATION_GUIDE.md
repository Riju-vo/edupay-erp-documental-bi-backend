# Guía de Integración (Para MS-Pagos y Frontend)

Este documento explica cómo otros microservicios (específicamente el MS-Pagos en NestJS) y el Frontend en Angular/React Native deben interactuar con el backend ERP.

## 1. Para el Equipo de Frontend

### Punto de Entrada
- Todas las interacciones de datos se hacen a través de **GraphQL**.
- El endpoint principal para el cliente está en `POST /graphql`.

### Flujo de Autenticación (Empleados)
Para interactuar con las mutaciones administrativas, se requiere iniciar sesión:
1. Llamar a la mutación `login` en GraphQL enviando `email` y `password`.
2. El backend responderá con un objeto `LoginResponse` que contiene un **token JWT** y los datos del empleado (incluyendo el `erpCode`).
3. Enviar este token en las cabeceras HTTP (`Authorization: Bearer <token>`) en las futuras peticiones *(Nota: Actualmente los filtros de seguridad están abiertos para facilitar pruebas locales, pero deberán habilitarse antes de ir a producción)*.

## 2. Para el Equipo de MS-Pagos (NestJS)

### Gestión de Usuarios ("El Dueño de la Verdad")
- El backend ERP es el sistema responsable de administrar las cuentas de los usuarios internos (secretarias, contadores, directores).
- **Lo que debes saber:** No necesitas crear un sistema de login o roles para los empleados en MS-Pagos. El Frontend te enviará el token JWT generado por el ERP, y dentro de ese token vendrá el `erpCode`. Simplemente debes guardar el `erpCode` en tu tabla `Employeed` como una referencia externa para la auditoría de quién realizó una acción en tu sistema de pagos.

### Sincronización de Pagos (Integración por Eventos)
Nuestras bases de datos están **completamente separadas** (Database-per-service). No debes conectarte directamente a PostgreSQL `db_erp` para marcar un pago como completado, ni el ERP leerá tu base de datos.
La integración se realiza de forma asíncrona mediante dos canales alternativos:

#### A. A través de RabbitMQ (Método Recomendado y Asíncrono)
Se utiliza un broker de mensajería para desacoplar ambos servicios. El microservicio de pagos (NestJS) debe publicar eventos hacia el exchange principal:

* **Exchange:** `edupay.exchange` (de tipo Topic)
* **Canales/Routing Keys del ERP (Spring Boot):**
  * **Confirmación de Pago:** Publicar el payload JSON a la routing key `payment.confirmed`. El ERP lo recibe en la cola `erp.payment.confirmed.queue`.
  * **Reversión de Pago:** Publicar el payload JSON a la routing key `payment.reversed`. El ERP lo recibe en la cola `erp.payment.reversed.queue`.
* **Payload requerido para Confirmaciones:**
  ```json
  {
    "eventId": "uuid-unico-del-evento",
    "paymentExternalId": "txn_12345",
    "familyId": 123,
    "paymentMethod": "STRIPE",
    "amount": 1500.00
  }
  ```
* **Payload requerido para Reversiones:**
  ```json
  {
    "eventId": "uuid-unico-del-evento-reversion",
    "paymentExternalId": "txn_12345",
    "familyId": 123,
    "reason": "FRAUD_DETECTED"
  }
  ```

#### B. A través de REST Webhooks (Alternativa Síncrona/HTTP)
Para pruebas rápidas sin broker, el ERP mantiene expuestos los endpoints HTTP directos:
1. **Confirmar Pago:** `POST http://<erp-url>/api/integration/events/payment-confirmed`
2. **Reversar Pago:** `POST http://<erp-url>/api/integration/events/payment-reversed`
*(Los payloads son idénticos a los de RabbitMQ)*

*Nota de Idempotencia:* Independientemente del canal usado (REST o RabbitMQ), el ERP registra cada `eventId` procesado de forma idempotente mediante el Transactional Inbox pattern, ignorando eventos duplicados.

## 3. Trabajo Pendiente (Deuda Técnica)
Para completar el 100% de la integración según los requerimientos del proyecto, aún falta implementar:
- **AWS S3 para Archivos:** El flujo para subir/descargar PDFs y fotos mediante Presigned URLs.
- **Seguridad Estricta:** Activar el `JwtAuthenticationFilter` en `SecurityConfig` para obligar al paso del Token.
- **Schedulers Nocturnos:** Implementar los procesos `@Scheduled` que corren a la medianoche para recalcular la mora y preparar los snapshots del Dashboard de BI.

