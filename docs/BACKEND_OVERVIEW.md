# EduPay - Backend ERP, Gestión Documental y BI

Este documento describe la arquitectura, diseño y tecnologías empleadas en el backend administrativo del ecosistema EduPay, responsable de los Módulos 9 (Gestión Documental), 14 (Business Intelligence) y 17 (ERP GraphQL).

## 1. Arquitectura del Sistema

El proyecto sigue una **Arquitectura Hexagonal (Puertos y Adaptadores)** combinada con **Domain-Driven Design (DDD) "Light"**.
- **`domain`**: Contiene las entidades puras y reglas de negocio (actualmente en transición desde un modelo anémico).
- **`application`**: Contiene los Casos de Uso (Use Cases) implementados como Servicios (`ErpApplicationService`, `DocumentApplicationService`, etc.). Es el "director de orquesta".
- **`adapters`**: Maneja la comunicación con el mundo exterior.
  - **`in` (Entrada)**: Controladores GraphQL y Webhooks REST para recibir eventos.
  - **`out` (Salida)**: Repositorios JPA para base de datos y futuros clientes de S3.

### Justificación de las Decisiones Arquitectónicas
- **Microservicios con Base de Datos Propia**: Este backend NO comparte base de datos con el Microservicio de Pagos. Esto sigue el patrón *Database-per-service*, garantizando el desacoplamiento, evitando que cambios en el esquema de pagos rompan el ERP, y protegiendo el rendimiento transaccional.
- **Integración Orientada a Eventos**: La comunicación con el sistema de pagos y motores de IA se realiza mediante eventos asíncronos (`PaymentConfirmed`, `PaymentReversed`), implementando el patrón **Inbox/Outbox** en la base de datos para garantizar la idempotencia y evitar pérdida de mensajes si un servicio falla.

## 2. Tecnologías y Librerías Utilizadas

- **Framework Principal**: Spring Boot 3.3.0 (Java 21).
- **Base de Datos / ORM**: PostgreSQL (en producción) / H2 (en memoria para pruebas). Spring Data JPA y Hibernate como ORM para mapear entidades.
- **API Pública**: Spring for GraphQL. En lugar de una API REST tradicional, se expone un endpoint GraphQL que permite a los clientes (Frontend) hacer *queries* a medida, evitando el over-fetching de datos.
- **Seguridad**: Spring Security y JJWT (`io.jsonwebtoken`) para emitir y validar tokens de acceso (Autenticación de Empleados).
- **Migraciones de BD**: Flyway, para controlar la versión del esquema de la base de datos.
- **Lombok**: Para reducir el código repetitivo en Java (getters, setters, constructores).

## 3. Funcionalidades Principales (Módulos)

### Módulo ERP (Enterprise Resource Planning)
- Gestión de Familias y cálculo en tiempo real del estado de cuenta (meses pagados vs. en mora).
- Asignación de descuentos (becas, pronto pago).
- Autenticación y gestión de usuarios administrativos (Empleados).

### Módulo Gestión Documental
- Flujo de revisión (Aprobar/Rechazar) para documentos de identidad y contratos subidos por los padres.
- Generación y asociación de facturas/recibos tras cada pago exitoso (S3 integration pendiente).

### Módulo Business Intelligence (BI)
- Recopilación de "Facts" (Hechos) diarios de recaudación.
- Endpoints GraphQL para poblar Dashboards (recaudación por método, proyecciones de mora).

## 4. Estructura de la Base de Datos
El esquema está segmentado lógicamente por prefijos para organizar el monolito modular:
- `erp_*`: `erp_family`, `erp_account_status`, `erp_discount_assignment`, `erp_employee`.
- `doc_*`: `doc_document`, `doc_document_review`, `doc_receipt_invoice`.
- `bi_*`: `bi_collection_fact_daily`, `bi_delinquency_snapshot`.
- `int_*`: `int_inbox_event`, `int_outbox_event` (para mensajería).
- `audit_*`: `audit_log` (trazabilidad de acciones de administradores).
