# Guía de Pruebas Rápidas (Testing Guide)

Esta guía te permitirá levantar el backend en tu máquina local y probar las funcionalidades principales de EduPay (GraphQL y Webhooks REST).

## 1. Requisitos Previos
- **Java 21** instalado.
- **Maven 3.8+** instalado.
- Un cliente para probar APIs como **Postman**, **Insomnia** o la propia consola web **GraphiQL**.

## 2. Cómo Levantar el Proyecto

Abre tu terminal en la raíz del proyecto y ejecuta:

```bash
mvn spring-boot:run
```

El servidor arrancará en el puerto **8080** y usará una base de datos en memoria (H2) que se reinicia cada vez que apagas el servidor. Flyway se encarga de crear todas las tablas automáticamente al arrancar.

> [!TIP]
> **Usuario Administrador por Defecto:**  
> Flyway inserta automáticamente un empleado para que puedas probar.
> - **Email:** `admin@edupay.com`
> - **Password:** `admin123`

---

## 3. Pruebas de GraphQL (Portal Web y App Móvil)

Spring Boot incluye una interfaz web muy útil para probar GraphQL. Abre en tu navegador:
👉 **http://localhost:8080/graphiql**

### A. Prueba de Autenticación (Login)
Para simular el inicio de sesión del Administrador, pega esto en el lado izquierdo de GraphiQL y presiona Play:

```graphql
mutation {
  login(input: {
    email: "admin@edupay.com",
    password: "admin123"
  }) {
    token
    employee {
      erpCode
      name
      role
    }
  }
}
```
**Qué deberías ver:** Te devolverá un `token` larguísimo. Ese es tu JWT.

### B. Registrar una Familia
Crea una familia para que luego podamos cobrarle:

```graphql
mutation {
  registerFamily(input: {
    externalId: "FAM-001"
    tutorName: "Juan Perez"
    tutorEmail: "juan.perez@email.com"
  }) {
    id
    tutorName
    active
  }
}
```
*Anota el `id` que te devuelve, digamos que fue `1`.*

### C. Consultar Estado Financiero
Verifiquemos cómo está financieramente la familia que acabas de crear (usa el `id` anterior):

```graphql
query {
  familyFinancialStatus(familyId: "1") {
    totalDebt
    monthsPaid
    monthsPending
    monthsInArrears
  }
}
```
*Debería devolver `totalDebt: 0` ya que es nueva y aún no hay cobros generados.*

---

## 4. Pruebas REST (Para MS-Pagos)

Para probar la comunicación de Máquina a Máquina (Webhooks), puedes usar Postman, o si tienes `curl` en tu consola, prueba los siguientes comandos.

### A. Simular un Pago Exitoso (Payment Confirmed)
Esto es lo que enviaría NestJS cuando un padre paga por Stripe o QR:

```bash
curl -X POST http://localhost:8080/api/integration/events/payment-confirmed \
-H "Content-Type: application/json" \
-d "{
  \"eventId\": \"evt_1001\",
  \"paymentExternalId\": \"stripe_ch_999\",
  \"familyId\": 1,
  \"paymentMethod\": \"STRIPE\",
  \"amount\": 1500.00
}"
```
**Qué ocurre internamente:** El backend lo anota en el `InboxEventEntity` para no cobrarlo dos veces (idempotencia) y suma `1500.00` a la recaudación del día en BI.

### B. Simular una Reversión de Pago (Payment Reversed)
Esto ocurre si la tarjeta rebotó al final o hubo fraude:

```bash
curl -X POST http://localhost:8080/api/integration/events/payment-reversed \
-H "Content-Type: application/json" \
-d "{
  \"eventId\": \"evt_1002\",
  \"paymentExternalId\": \"stripe_ch_999\",
  \"familyId\": 1,
  \"reason\": \"FRAUD_DETECTED\"
}"
```
**Qué ocurre internamente:** El backend anota el evento en el Inbox para evitar duplicados y actualiza los registros para poner la familia en mora.

---

> [!IMPORTANT]
> Si intentas mandar el mismo `eventId` dos veces en los endpoints REST, verás que la primera vez te responde `processed` y la segunda te responde `duplicate`. ¡La idempotencia funciona correctamente!
