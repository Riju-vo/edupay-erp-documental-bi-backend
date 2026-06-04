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

---

## 5. Colección de Postman Compartida

Hemos preparado una colección completa de Postman lista para importar. La puedes encontrar en la carpeta de documentación del proyecto:
👉 [edupay_postman_collection.json](file:///c:/Users/hp/Desktop/Ingenieria%20de%20Software%202/Segundo%20parcial/edupay-erp-documental-bi-backend/docs/edupay_postman_collection.json)

### Características de la Colección:
1. **Variables de Entorno Preconfiguradas**: Usa `{{baseUrl}}` (por defecto `http://localhost:8080`) para que puedas cambiar el host a tu entorno de desarrollo o producción (ej. tu base de Railway) en un solo clic.
2. **Autenticación Automatizada**: Al ejecutar la petición de **Iniciar Sesión (GraphQL)**, un script de Postman extrae automáticamente el token JWT de la respuesta y lo guarda en la variable `{{jwt_token}}` de la colección. Esto autoriza de forma transparente todas las consultas protegidas subsiguientes.
3. **Estructura Organizada por Módulos**:
   - **Autenticación**: Iniciar Sesión (GraphQL).
   - **Integración Pagos (REST Webhooks)**: Eventos de `Confirmar Pago` y `Reversar Pago` adaptados con `familyId` y generadores aleatorios de `$guid` y `$timestamp`.
   - **Consultas GraphQL (ERP & BI)**: Estado Financiero, Alumnos en Mora y Dashboards de BI.
   - **Mutaciones GraphQL (ERP & Documental)**: Registro de familia, asignación de descuentos, revisión de documentos y emisión de facturas/recibos digitales.

---

## 6. Pruebas de RabbitMQ (Mensajería Asíncrona)

Para probar la integración asíncrona mediante eventos locales:

### A. Levantar RabbitMQ
Asegúrate de que **Docker Desktop** esté iniciado en tu máquina y ejecuta en tu consola:
```bash
docker compose up -d
```
Esto descargará y levantará el broker con la consola de administración web habilitada.

### B. Levantar el ERP (Spring Boot)
Inicia la aplicación con:
```bash
mvn spring-boot:run
```
Verás en los logs que la aplicación se conecta exitosamente a `localhost:5672` y declara automáticamente el Exchange `edupay.exchange` y las colas correspondientes.

### C. Publicar un Mensaje de Prueba
1. Abre en tu navegador la consola web de administración: 👉 **http://localhost:15672** (Usuario: `guest`, Contraseña: `guest`).
2. Ve a la sección **Queues** y selecciona la cola `erp.payment.confirmed.queue`.
3. Despliega el bloque **Publish Message**.
4. En el campo **Payload**, introduce el siguiente JSON y presiona **Publish Message**:
   ```json
   {
     "eventId": "evt-rabbit-manual-101",
     "paymentExternalId": "PAY-TX-RABBIT-001",
     "familyId": 1,
     "paymentMethod": "RABBITMQ_DOCKER",
     "amount": 1200.00
   }
   ```
5. En la consola donde se ejecuta tu backend Spring Boot, deberías ver la traza de log indicando que el mensaje fue recibido, validado y procesado exitosamente por `RabbitMqListener`.


