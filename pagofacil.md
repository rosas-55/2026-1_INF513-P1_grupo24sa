# [cite_start]Documentación de Contexto - API PagoFácil (Versión 1.1.0) [cite: 5]

[cite_start]**Resumen General:** La API de PagoFácil permite a los comercios generar códigos QR para el cobro de servicios, consultar el estado de las transacciones y recibir notificaciones automáticas (callbacks) cuando un pago se ha completado[cite: 52, 56, 127].

## 1. Consideraciones Iniciales
* [cite_start]**Protocolo:** HTTPS[cite: 20].
* [cite_start]**URL BASE:** `https://masterqr.pagofacil.com.bo/api/services/v2`[cite: 20].
* [cite_start]**Flujo General:** 1. Autenticación para obtener token[cite: 51].
  2. [cite_start]Generación del QR enviando los datos de la venta[cite: 52].
  3. [cite_start]Mostrar el QR al cliente para su escaneo y pago[cite: 54, 55].
  4. [cite_start]Confirmación del pago mediante Callback (recomendado) o consulta manual[cite: 56, 59, 60].

---

## 2. Autenticación (Login)
[cite_start]La API requiere un token de acceso para utilizar los servicios[cite: 68].

* [cite_start]**Endpoint:** `/login`[cite: 67].
* [cite_start]**Método:** `POST`[cite: 66].
* **Cabeceras Requeridas (Headers):**
    * [cite_start]`tcTokenSecret`: Clave secreta proporcionada por PagoFácil[cite: 70, 77].
    * [cite_start]`tcTokenService`: Clave de servicio proporcionada por PagoFácil[cite: 70, 79].
* [cite_start]**Respuesta Esperada:** Devuelve un JSON donde el nodo `values` contiene el `accessToken`, el tipo de token (`bearer`) y el tiempo de expiración en minutos (`expiresInMinutes`)[cite: 87, 88, 89, 90].
* [cite_start]**Uso del Token:** En todas las solicitudes posteriores, el token debe incluirse en la cabecera como: `Authorization: Bearer <tu_access_token>`[cite: 71, 72]. [cite_start]Es responsabilidad del sistema cliente gestionar su renovación antes de que expire[cite: 74].

---

## 3. Endpoints Principales

### A. Listar Métodos Habilitados
[cite_start]Obtiene una lista detallada de los métodos de pago QR configurados y habilitados para el comercio[cite: 100].
* [cite_start]**Endpoint:** `/list-enabled-services`[cite: 97].
* [cite_start]**Método:** `POST`[cite: 96].
* [cite_start]**Datos Clave en Respuesta:** Devuelve un arreglo con el `paymentMethodId`, `currencyName` (ej. BOB) y los límites transaccionales (`maxAmountPerDay`, `maxAmountPerTransaction`)[cite: 111, 113, 115, 116, 117].

### B. Generar Código QR
[cite_start]Crea una orden en PagoFácil y obtiene la imagen del código QR[cite: 127].
* [cite_start]**Endpoint:** `/generate-qr`[cite: 124].
* [cite_start]**Método:** `POST`[cite: 123].
* **Cuerpo (Body) - Parámetros Clave:**
    * [cite_start]`paymentMethod`: ID del método de pago[cite: 133].
    * [cite_start]`clientName`, `documentType`, `documentId`, `phoneNumber`, `email`: Datos del cliente[cite: 134, 135, 136, 137, 138].
    * [cite_start]`paymentNumber`: ID de transacción de la empresa[cite: 139].
    * [cite_start]`amount` y `currency`: Monto y moneda (ej. 2)[cite: 140, 141].
    * [cite_start]`callbackUrl`: URL de notificación del comercio[cite: 143].
    * [cite_start]`orderDetail`: Arreglo con el detalle de la venta (`serial`, `product`, `quantity`, `price`, `discount`, `total`)[cite: 144, 146, 147, 148, 149, 150, 151].
* [cite_start]**Respuesta Esperada:** Retorna el `transactionId` interno de PagoFácil, la fecha de expiración (`expiration Date`), el QR en Base64 (`qrBase64`) y URLs adicionales si corresponden (`checkoutUrl`, `deepLink`, etc.)[cite: 165, 168, 169, 170, 171].

### C. Consultar Transacción (Consulta Manual)
[cite_start]Verifica el estado actual de una transacción[cite: 179].
* [cite_start]**Endpoint:** `/query-transaction`[cite: 178].
* [cite_start]**Método:** `POST`[cite: 177].
* [cite_start]**Cuerpo (Body):** Requiere enviar solo **uno** de los siguientes datos: `pagofacilTransactionId` o `companyTransactionId`[cite: 188].
* [cite_start]**Respuesta Esperada:** Retorna información del pago incluyendo `paymentStatus`, `amount`, `currencyId`, `paymentDate` y `paymentTime`[cite: 198, 199, 200, 201, 202].

### D. Consultar Bibliografía
[cite_start]Servicio en desarrollo para consultar la definición y reglas de parámetros de la API[cite: 212, 213].
* [cite_start]**Endpoint:** `/bibliography/{nombreParametro}`[cite: 218].
* [cite_start]**Método:** `GET`[cite: 208].

---

## 4. Notificaciones Webhook (URL Callback)
[cite_start]Si se especificó `tcUrlCallBack` al generar el QR, PagoFácil llamará a esa URL para notificar que el pago se realizó (método recomendado)[cite: 288, 289].
* [cite_start]**Estructura:** Notificación HTTP `POST` a la URL registrada[cite: 291].
* **Cuerpo (Payload) Recibido:**
    * [cite_start]`PedidoID`: Identificación del pedido del comercio[cite: 295].
    * [cite_start]`Fecha` y `Hora`: Cuándo se realizó el pago[cite: 296, 297].
    * [cite_start]`MetodoPago` y `Estado`: Detalles de la ejecución[cite: 298, 299].
* [cite_start]**Respuesta Obligatoria del Comercio:** El servidor debe responder con un código de estado `HTTP 200 OK` y el siguiente cuerpo JSON[cite: 302]:
    ```json
    {
      "error": 0,
      "status": 1,
      "message": "Mensaje personalizado de éxito",
      "values": true
    }
    ```
