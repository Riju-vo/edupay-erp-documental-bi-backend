**UNIVERSIDAD AUTÓNOMA GABRIEL RENE MORENO**

**FACULTAD DE INGENIERIA EN CIENCIAS DE LA COMPUTACION Y TELECOMUNICACIONES**

# Sistema de Gestión de Pagos Escolares

**Integrantes:**

- Aricari Blanco Erick Milton 220154120
- Bulacia Paz Bruno Leandro 223041866
- Vargas Osinaga Richard Junior 219072736

**Materia:** Ingeniería de Software II – SB

**Grupo:** 20

**Docente:** Ing. Rolando Antonio Martínez Canedo

**Fecha:** 05/2026

Santa Cruz – Bolivia

## Alcance del Sistema

EduPay SCZ es una plataforma digital orientada a gestionar, procesar y registrar los pagos de mensualidades escolares de un colegio privado en Santa Cruz de la Sierra, Bolivia. El sistema permite a los padres de familia realizar pagos mediante múltiples métodos (QR, tarjeta y blockchain), y a los administradores gestionar las cuentas, verificar ingresos, emitir documentos financieros y analizar el comportamiento de pago de las familias.

El sistema no gestiona calificaciones, asistencia, comunicación docente-alumno ni planificación académica. Su único núcleo es el proceso de cobro, su seguimiento y su análisis.

El sistema se compone de dos tipos de usuarios principales:

- Padre / Tutor: realiza pagos, consulta su estado de cuenta y sube documentos requeridos por el colegio.
- Administrador: gestiona las familias, verifica los pagos, emite facturas y toma decisiones basadas en los reportes del sistema.

## 1.1. Módulos del Sistema

### Módulo 1. Autenticación y acceso al sistema

Este módulo controla el ingreso seguro al sistema tanto desde el portal web como desde la aplicación móvil.

- El sistema permite el registro de nuevos usuarios asignando un rol específico (padre/tutor o administrador).
- El sistema permite iniciar sesión con correo electrónico y contraseña, generando un token de acceso seguro (JWT).
- El sistema protege todos los endpoints de la aplicación de modo que solo usuarios autenticados puedan acceder a la información.
- El sistema identifica al usuario autenticado en cada acción para asociar correctamente sus pagos, documentos y configuraciones personales.
- El sistema cierra sesión automáticamente tras un período de inactividad definido, renovando el token solo cuando el usuario está activo.
- El sistema distingue los permisos según el rol del usuario: el padre solo ve su información y la de sus hijos, mientras que el administrador tiene acceso global.

### Módulo 2. Gestión de familias y alumnos

Este módulo administra el registro de las familias vinculadas al colegio y los alumnos a su cargo, que son la base sobre la cual se generan los cobros.

- El sistema permite al administrador registrar una nueva familia indicando los datos del tutor principal (nombre, CI, teléfono, correo) y un contacto alternativo opcional.
- El sistema permite asociar uno o más alumnos a una familia, registrando el nombre del alumno, su código único en el colegio y el grado al que pertenece (solo como referencia, sin gestionar notas ni asistencia).
- El sistema permite al administrador consultar la lista completa de familias activas con filtros por nombre, código de alumno o estado de cuenta.
- El sistema permite desactivar lógicamente a una familia cuando el alumno ya no pertenece al colegio, conservando el historial de pagos sin eliminarlo.
- El sistema muestra para cada familia su estado financiero del año: qué meses están pagados, cuáles están pendientes y si existe algún monto en mora.
- El sistema calcula automáticamente si una familia tiene deuda acumulada y la muestra con claridad tanto al padre como al administrador.

### Módulo 3. Configuración de conceptos de cobro

Este módulo permite al administrador definir cuánto y cuándo debe pagar cada familia a lo largo del año escolar.

- El sistema permite al administrador configurar el monto de la mensualidad para cada año escolar.
- El sistema permite definir qué meses del año son activos para el cobro (por defecto: febrero a noviembre, 10 meses).
- El sistema permite registrar conceptos de cobro adicionales a la mensualidad, como matrícula, cuota de laboratorio o actividades extracurriculares.
- El sistema permite crear políticas de descuento aplicables por tipo: pago anual adelantado (10%), beca, segundo hijo matriculado o personal del colegio.
- El sistema permite asignar una política de descuento específica a una familia determinada.
- El sistema permite configurar cuántos días de gracia tiene una familia antes de que se aplique un recargo por mora (por defecto: hasta el día 10 del mes).
- El sistema calcula automáticamente el recargo de mora cuando un pago se registra fuera del período de gracia.

### Módulo 4. Pagos — Portal Web (padre/tutor)

Este módulo es el núcleo del sistema. Permite al padre o tutor realizar el pago de las mensualidades de sus hijos desde el navegador web.

- El sistema muestra al padre un panel con el estado de cuenta de cada uno de sus hijos: mensualidades pagadas (en verde), pendientes (en amarillo) y vencidas con mora (en rojo).
- El sistema permite al padre seleccionar uno o varios meses pendientes y proceder al pago en un flujo guiado de tres pasos: selección de meses, elección del método de pago y confirmación.
- El sistema ofrece tres métodos de pago alternativos: código QR, tarjeta de crédito/débito (Stripe) y criptomoneda (Blockchain).
- El sistema permite al padre pagar el año escolar completo de forma adelantada, aplicando automáticamente el descuento configurado y mostrando el ahorro obtenido antes de confirmar.
- El sistema genera un comprobante descargable (PDF) inmediatamente después de que el pago es confirmado.
- El sistema muestra el historial completo de pagos del padre con fecha, método utilizado, monto y estado de cada transacción.

### Módulo 5. Pago mediante código QR

Este submódulo gestiona el pago utilizando un código QR compatible con las aplicaciones bancarias bolivianas.

- El sistema genera un código QR único para cada alumno y mes a pagar, siguiendo el estándar EMV QR boliviano.
- El QR generado tiene una fecha de vencimiento configurable (por defecto: hasta el día 15 del mes).
- El padre puede escanear el código QR con su aplicación bancaria habitual (BNB, Mercantil Santa Cruz, FIE u otro banco boliviano habilitado).
- El sistema recibe automáticamente la confirmación del banco mediante un webhook y registra el pago sin intervención manual.
- Si el QR vence sin que se haya realizado el pago, el sistema lo invalida y permite generar uno nuevo.
- El código QR también puede ser mostrado en la aplicación móvil para ser escaneado directamente desde la pantalla del teléfono.

### Módulo 6. Pago mediante tarjeta (Stripe)

Este submódulo gestiona el pago con tarjeta de crédito o débito a través de la pasarela Stripe.

- El sistema presenta al padre un formulario seguro de pago con tarjeta dentro del propio portal, sin redirigirlo a otro sitio.
- El sistema procesa el pago mediante la API de Stripe, garantizando que en ningún momento los datos de la tarjeta son almacenados por EduPay SCZ.
- El sistema recibe la confirmación del pago de Stripe a través de un webhook y actualiza el estado del mes correspondiente de forma automática.
- En caso de que el pago sea rechazado, el sistema informa al padre el motivo y le permite intentarlo nuevamente o elegir otro método.
- El sistema convierte automáticamente el monto de bolivianos (BOB) a dólares (USD) aplicando el tipo de cambio oficial del Banco Central de Bolivia del día.

### Módulo 7. Pago mediante Blockchain

Este submódulo permite a los padres realizar pagos utilizando criptomonedas (USDT) sobre la red Polygon, dejando un registro inmutable de cada transacción.

- El sistema muestra al padre la dirección del contrato inteligente del colegio y el monto exacto a transferir en USDT.
- El padre realiza la transferencia desde su billetera digital (MetaMask u otra compatible con Polygon).
- El sistema escucha automáticamente los eventos del contrato inteligente desplegado en Polygon y registra el pago cuando detecta la transacción del padre.
- Cada pago blockchain queda registrado de forma permanente en la cadena de bloques, permitiendo al padre verificarlo en cualquier momento usando el hash de la transacción.
- El sistema convierte el monto en USDT al equivalente en bolivianos aplicando el tipo de cambio del día y lo registra así en los libros internos del colegio.
- El sistema espera un mínimo de confirmaciones de bloque antes de considerar el pago como válido (por defecto: 12 confirmaciones).

### Módulo 8. Verificación y conciliación de pagos (administrador)

Este módulo permite al administrador revisar, aprobar y conciliar todos los pagos recibidos en el sistema.

- El sistema muestra al administrador un panel de conciliación donde se visualizan todos los pagos del mes: los confirmados automáticamente (Stripe y QR verificados) y los que requieren revisión manual (comprobantes fotográficos).
- El sistema permite al administrador aprobar o rechazar un comprobante subido manualmente por el padre, indicando el motivo del rechazo cuando corresponda.
- Al aprobar un comprobante, el sistema actualiza el estado del mes del alumno como pagado y notifica al padre automáticamente.
- El sistema permite exportar el reporte de conciliación mensual en formato Excel para su uso en los sistemas contables del colegio.
- El sistema resalta visualmente los pagos que no coinciden con el monto esperado para que el administrador los revise con atención.

### Módulo 9. Gestión documental

Este módulo gestiona la recopilación, almacenamiento y revisión de los documentos que el colegio requiere a cada familia.

- El sistema permite al padre subir documentos desde el portal web o la aplicación móvil (CI del alumno, CI del tutor, certificado de nacimiento, foto y contrato de matrícula firmado).
- El sistema almacena cada documento de forma segura en un servicio de almacenamiento en la nube (S3), organizándolo por familia y tipo de documento.
- El sistema mantiene un registro completo de cada documento: quién lo subió, en qué fecha, cuál es su estado actual y quién lo revisó.
- El sistema genera automáticamente y almacena las facturas y recibos de cada pago confirmado, asociándolos a la familia correspondiente.
- El sistema permite al padre descargar cualquier factura o recibo previo en cualquier momento, sin necesidad de solicitarlo al administrador.
- El sistema muestra al administrador una cola de documentos pendientes de revisión para que los apruebe o rechace de forma ordenada.
- El sistema notifica al padre cuando un documento es aprobado o rechazado, indicando en este último caso el motivo y qué debe corregir.
- El sistema valida automáticamente la cédula de identidad boliviana subida por el padre mediante un servicio de inteligencia artificial que extrae y verifica los datos del documento.

### Módulo 10. Aplicación móvil para padres

Este módulo representa la experiencia del padre en su teléfono celular, aprovechando los recursos del dispositivo para simplificar el proceso de pago.

- El sistema muestra al padre, al abrir la aplicación, el estado de cuenta actualizado de cada uno de sus hijos con los meses pendientes y el monto a pagar.
- El sistema permite al padre pagar desde el celular usando cualquiera de los tres métodos disponibles (QR, tarjeta o blockchain).
- El sistema permite al padre escanear el código QR del colegio usando la cámara del teléfono para iniciar el proceso de pago directamente desde la aplicación (Recurso de dispositivo: Cámara).
- El sistema permite al padre fotografiar un comprobante de pago bancario físico usando la cámara del teléfono. La fotografía es analizada por inteligencia artificial para extraer el monto, fecha y referencia, pre-llenando el formulario sin que el padre deba escribir nada (Recurso de dispositivo: Cámara + Servicio de IA).
- El sistema utiliza la ubicación GPS del dispositivo para mostrar en un mapa los bancos y agentes de pago más cercanos a donde se encuentra el padre en ese momento (Recurso de dispositivo: GPS / Ubicación).
- El sistema muestra el historial de pagos y permite descargar los recibos directamente desde el celular.
- El sistema envía notificaciones push al celular del padre para recordarle las mensualidades próximas a vencer y confirmarle los pagos realizados.

### Módulo 11. Reconocimiento de comprobantes mediante IA (Deep Learning)

Este módulo utiliza un modelo de visión por computadora entrenado específicamente sobre comprobantes bancarios bolivianos para extraer información de forma automática.

- El sistema recibe la fotografía del comprobante bancario capturada por el padre desde su teléfono.
- El sistema procesa la imagen utilizando un modelo de reconocimiento óptico de caracteres (OCR) basado en deep learning, entrenado con comprobantes reales del BNB, Banco Mercantil Santa Cruz, Tigo Money y otros bancos bolivianos.
- El sistema identifica y extrae automáticamente los campos clave del comprobante: nombre del banco, monto pagado, fecha y hora, número de referencia de la transacción y concepto.
- El sistema devuelve un porcentaje de confianza sobre la extracción. Si la confianza supera el 80%, propone automáticamente el registro del pago pre-llenado para que el padre solo confirme. Si la confianza es menor, muestra los campos extraídos para que el padre los corrija.
- El sistema almacena la imagen del comprobante junto con los datos extraídos para que el administrador pueda revisarlos si es necesario.

### Módulo 12. Predicción de mora (Machine Learning Supervisado)

Este módulo permite al administrador anticipar qué familias tienen alta probabilidad de no pagar el mes siguiente, para tomar acciones preventivas antes de que ocurra la mora.

- El sistema calcula para cada familia un puntaje de riesgo de mora expresado como un número entre 0 y 1, donde los valores cercanos a 1 indican alta probabilidad de no pagar a tiempo.
- El sistema muestra el puntaje de riesgo en la ficha de cada familia dentro del panel del administrador.
- El sistema destaca automáticamente en el panel las familias cuyo puntaje supera 0.70, clasificándolas como alto riesgo.
- El sistema permite al administrador filtrar y exportar la lista de familias en alto riesgo para el mes siguiente.
- El modelo se actualiza automáticamente cada semana incorporando los nuevos datos de pago reales, mejorando su precisión con el tiempo.
- El sistema registra las predicciones realizadas y las compara con lo que efectivamente ocurrió, permitiendo medir la precisión del modelo mes a mes.

### Módulo 13. Segmentación de familias por comportamiento de pago (ML No Supervisado)

Este módulo agrupa automáticamente a las familias según sus patrones de pago históricos, sin necesidad de clasificarlas manualmente, para que el administrador pueda definir estrategias diferenciadas para cada grupo.

- El sistema clasifica automáticamente a cada familia en uno de cuatro grupos según su comportamiento histórico de pago:
  1. Puntual estrella: siempre paga antes del día 5, sin mora histórica.
  2. Regular: paga entre el día 5 y el 15, sin incidencias graves.
  3. Irregular: varía mucho entre meses, con episodios de puntualidad y retraso.
  4. Moroso crónico: paga después del día 15 de forma reiterada o acumula meses sin pagar.
- El sistema muestra la clasificación de cada familia en su ficha dentro del panel del administrador.
- El sistema sugiere al administrador una acción diferente según el grupo: descuento por lealtad a las puntual estrella, recordatorio proactivo a las irregulares, y plan de fraccionamiento o llamada personal a las morosas crónicas.
- El sistema muestra en el dashboard de Business Intelligence la distribución de familias por grupo y cómo ha evolucionado esa distribución a lo largo del año.
- El modelo de segmentación se recalcula mensualmente para capturar cambios recientes en el comportamiento de las familias.

### Módulo 14. Inteligencia de negocio y dashboards (Business Intelligence)

Este módulo proporciona al administrador una visión clara del estado financiero del colegio a través de paneles de indicadores actualizados automáticamente.

- El sistema muestra un dashboard de recaudación mensual con el total cobrado en el mes, desglosado por método de pago (QR, Stripe y Blockchain), y lo compara con el mismo mes del año anterior.
- El sistema muestra el porcentaje del objetivo de recaudación mensual que ha sido alcanzado hasta el momento.
- El sistema muestra un dashboard de mora con el número de familias en mora, el monto total adeudado y la distribución de la deuda por antigüedad (1-15 días, 16-30 días, 31-60 días y más de 60 días).
- El sistema muestra un dashboard de proyección anual que estima cuánto recaudará el colegio al finalizar el año, basándose en el comportamiento de cobro de los meses anteriores y en el modelo de predicción de mora.
- El sistema muestra un dashboard de segmentación con la distribución de familias por grupo de comportamiento y las familias identificadas como de alto riesgo por el modelo predictivo.
- El sistema permite al administrador exportar cualquier reporte en formato Excel o PDF con un solo clic.
- Todos los dashboards se actualizan automáticamente cada noche con los datos del día anterior.

### Módulo 15. Notificaciones automáticas

Este módulo envía comunicaciones automáticas a los padres y al administrador en los momentos clave del ciclo de cobro.

- El sistema envía un correo electrónico al padre inmediatamente después de que su pago es confirmado, adjuntando el comprobante en PDF.
- El sistema envía una notificación push al teléfono del padre el primer día de cada mes recordándole la mensualidad pendiente.
- El sistema envía un segundo recordatorio por SMS al padre que no haya pagado al llegar al día 8 del mes.
- El sistema envía una notificación de mora al padre que no haya pagado pasado el día de gracia (día 10), indicando el monto adicional por recargo.
- El sistema notifica al administrador cuando se recibe un nuevo pago o cuando un padre sube un documento para revisión.
- El sistema notifica al padre cuando el administrador aprueba o rechaza un documento subido, indicando el motivo en caso de rechazo.
- El contenido de los recordatorios se adapta automáticamente según el grupo de comportamiento de la familia: el tono es más urgente para las familias clasificadas como morosas crónicas.

### Módulo 16. Automatización de flujos (N8N y Báscula)

Este módulo conecta automáticamente todos los microservicios del sistema para que, tras cada pago, ocurra una secuencia de acciones sin intervención humana.

### Flujo principal de pago — Báscula (5 pasos)

1.  Confirmación: el sistema recibe la confirmación del pago desde el proveedor (Stripe, QR bancario o red Blockchain) y registra el pago como confirmado.
2.  Publicación de evento: el sistema publica un evento interno que informa al resto de los servicios que se produjo un pago exitoso.
3.  Generación del documento: el sistema genera automáticamente la factura en PDF, la firma digitalmente y la almacena en S3.
4.  Actualización del ERP: el sistema actualiza el estado del mes correspondiente del alumno como pagado en el módulo de gestión.
5.  Notificación: el sistema envía el correo con la factura adjunta al padre y una notificación push al administrador informando del nuevo ingreso.

### Flujo de recordatorio mensual — N8N (4 pasos)

1.  Disparador programado: el sistema se activa automáticamente el primer día de cada mes activo a las 8:00 AM.
2.  Consulta de pendientes: el sistema consulta en el ERP la lista de familias que aún no han pagado el mes en curso.
3.  Procesamiento por lotes: el sistema divide la lista en grupos para enviar las notificaciones de forma eficiente sin saturar los servicios.
4.  Envío de recordatorios: el sistema envía la notificación push y el correo de recordatorio a cada familia pendiente, registrando el envío para evitar duplicados.

### Flujo de alerta de mora — N8N (3 pasos)

1.  Disparador programado: el sistema se activa el día 11 de cada mes a las 9:00 AM.
2.  Identificación de familias en mora: el sistema identifica a quienes no pagaron dentro del período de gracia y consulta su grupo de comportamiento.
3.  Notificación diferenciada: el sistema envía SMS y correo a las familias en mora, ajustando el tono del mensaje según su grupo (irregular: recordatorio amable; moroso crónico: alerta urgente con indicación de llamar a la administración).

### Módulo 17. Sistema de gestión empresarial con GraphQL (ERP)

Este módulo representa la capa de gestión administrativa del colegio, construida sobre GraphQL para permitir consultas flexibles y eficientes desde el panel del administrador.

- El sistema expone una API GraphQL que permite al frontend solicitar exactamente los datos que necesita en cada pantalla, sin recibir información de más ni de menos.
- El sistema permite consultar el estado financiero completo de una familia (meses pagados, pendientes, en mora, descuentos aplicados, puntaje de riesgo) en una sola consulta.
- El sistema permite al administrador ejecutar mutaciones para registrar familias, asignar descuentos, aprobar documentos y emitir facturas.
- El sistema ofrece subscripciones en tiempo real para que el panel del administrador se actualice automáticamente cuando llega un nuevo pago, sin necesidad de recargar la página.
- El sistema permite listar todos los alumnos con mora activa, ordenados por monto adeudado, con una sola consulta GraphQL.

## 2. Mapeo de Requisitos Técnicos del Proyecto

La siguiente tabla indica, para cada requisito técnico exigido por el proyecto, cuál es la funcionalidad concreta del sistema que lo satisface y con qué tecnología se implementa.

| **Requisito del proyecto**         | **Módulo del sistema**                                                    | **Tecnología**                         |
|------------------------------------|---------------------------------------------------------------------------|----------------------------------------|
| Deep Learning (imágenes)           | Módulo 11 — Reconocimiento de comprobantes con IA                         | TrOCR fine-tuned · PyTorch · Azure AKS |
| ML Supervisado                     | Módulo 12 — Predicción de mora                                            | LightGBM · scikit-learn · Azure ML     |
| ML No Supervisado                  | Módulo 13 — Segmentación de familias                                      | K-Means · scikit-learn · Azure ML      |
| Business Intelligence (dashboards) | Módulo 14 — Dashboards de recaudación, mora y proyección                  | Go · BigQuery · ECharts · GCP          |
| GraphQL (gestión empresarial)      | Módulo 17 — ERP del colegio                                               | Java Spring · Spring for GraphQL · GCP |
| Blockchain (pagos)                 | Módulo 7 — Pago con criptomoneda                                          | Solidity · Polygon · ethers.js         |
| Pago QR                            | Módulo 5 — Pago con código QR                                             | Node.js · Estándar QR EMV Bolivia      |
| Pago Stripe                        | Módulo 6 — Pago con tarjeta                                               | Stripe PaymentIntents · Stripe.js      |
| Gestión documental con S3          | Módulo 9 — Gestión de documentos                                          | SpringBoot · AWS Lambda · S3           |
| App móvil — Cámara                 | Módulo 10 — Escanear QR y fotografiar comprobantes                        | React Native · expo-camera             |
| App móvil — GPS / Ubicación        | Módulo 10 — Mapa de bancos cercanos                                       | expo-location · react-native-maps      |
| App móvil — Servicio de IA         | Módulo 10 + Módulo 11 — OCR de comprobante en móvil                       | Azure AI Vision · MS-IA                |
| Automatización Báscula (3+ pasos)  | Módulo 16 — Flujo de pago de 5 pasos                                      | AWS EventBridge · N8N                  |
| Automatización N8N (3+ pasos)      | Módulo 16 — Recordatorio mensual (4 pasos) y mora (3 pasos)               | N8N · SQS · MS-Notificaciones          |
| PostgreSQL                         | Módulos 2, 3, 4, 5, 6, 7, 8, 9 — Datos operacionales                      | RDS AWS · Cloud SQL GCP                |
| DynamoDB                           | Módulos 11, 12, 13, 15 — Eventos, predicciones, logs                      | AWS DynamoDB                           |
| S3                                 | Módulo 9 — Documentos, facturas y recibos                                 | AWS S3 con URLs prefirmadas            |
| 3+ Microservicios                  | MS-Pagos · MS-Gestión · MS-Documental · MS-IA · MS-BI · MS-Notificaciones | 6 microservicios independientes        |
| 3+ Proveedores cloud               | MS-Pagos/Documental en AWS · MS-Gestión/BI en GCP · MS-IA/Notif. en Azure | AWS · GCP · Azure                      |
| 3+ Lenguajes backend               | Node.js · Java · Python · Go · Rust                                       | 5 lenguajes distintos                  |
| Frontend Angular                   | Módulos 2–9, 14, 17 — Portal web completo                                 | Angular 17 · Apollo Client · ECharts   |
| App móvil React Native             | Módulo 10 — Aplicación para padres                                        | React Native · Expo                    |

## 3. Límites del Sistema — Fuera del Alcance

Para dejar claramente definido lo que el sistema no hace, se listan a continuación las funcionalidades que quedan fuera del alcance de EduPay SCZ:

- El sistema no gestiona calificaciones, libretas de notas ni boletines escolares.
- El sistema no gestiona asistencia de alumnos ni seguimiento de ausentismo.
- El sistema no administra planillas de docentes ni recursos humanos del colegio.
- El sistema no gestiona la comunicación entre docentes y padres (mensajería interna).
- El sistema no crea ni gestiona paralelos, cursos ni materias.
- El sistema no se integra con sistemas del Ministerio de Educación de Bolivia.
- El sistema no realiza cobros de servicios externos al colegio (transporte escolar, cafetería), aunque estos pueden ser incorporados como conceptos adicionales en versiones futuras.
