package com.tecnoweb.grupo24sa.controller;

import com.tecnoweb.grupo24sa.business.BCuota;
import com.tecnoweb.grupo24sa.business.BVenta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Endpoint REST que recibe las notificaciones POST de PagoFácil
 * (callback/webhook)
 * cuando un pago QR es completado por el cliente.
 *
 * Ruta: POST /api/pagofacil/callback
 *
 * Flujo:
 * 1. PagoFácil realiza un POST a esta URL al detectar que el cliente pagó el
 * QR.
 * 2. Este controlador lee el PedidoID y el Estado del payload.
 * 3. Según el prefijo del PedidoID identifica si es una venta (VTA-) o cuota
 * (CUOTA-).
 * 4. Actualiza el estado correspondiente en la base de datos.
 * 5. Responde con HTTP 200 y el JSON que exige PagoFácil para confirmar la
 * recepción.
 *
 * Variable de entorno requerida: PAGOFACIL_CALLBACK_URL
 * Formato esperado del PedidoID al generar el QR:
 * - Ventas: "VTA-{ventaId}-{timestamp}" (ej. VTA-12-1780626590138)
 * - Cuotas: "CUOTA-{cuotaId}-{timestamp}" (ej. CUOTA-5-1780626590138)
 */
@RestController
@RequestMapping("/grupo24sa/pagofacil")
public class PagoFacilCallbackController {

    /**
     * Recibe la notificación de pago de PagoFácil.
     *
     * Payload esperado:
     * {
     * "PedidoID": "VTA-12-1780626590138",
     * "Fecha": "2026-06-14",
     * "Hora": "14:30:00",
     * "MetodoPago": "QR",
     * "Estado": "PAGADO"
     * }
     */
    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> recibirCallback(
            @RequestBody Map<String, Object> payload) {

        System.out.println("[PagoFácil Callback] Notificación recibida: " + payload);

        try {
            String pedidoId = (String) payload.getOrDefault("PedidoID", "");
            String estado = (String) payload.getOrDefault("Estado", "");
            String fecha = (String) payload.getOrDefault("Fecha", LocalDate.now().toString());

            if ("PAGADO".equalsIgnoreCase(estado) && !pedidoId.isEmpty()) {

                // ── CASO 1: Venta al contado (VTA-{ventaId}-{timestamp}) ──
                if (pedidoId.startsWith("VTA-")) {
                    String[] partes = pedidoId.split("-");
                    // partes[0] = "VTA", partes[1] = ventaId
                    if (partes.length >= 2) {
                        int ventaId = Integer.parseInt(partes[1]);
                        BVenta bVenta = new BVenta();
                        String resultado = bVenta.actualizarEstadoVenta(ventaId, "PAGADO");
                        System.out.println("[PagoFácil Callback] Venta " + ventaId
                                + " actualizada a PAGADO: " + resultado);
                    }

                    // ── CASO 2: Cuota de crédito (CUOTA-{cuotaId}-{timestamp}) ──
                } else if (pedidoId.startsWith("CUOTA-")) {
                    String[] partes = pedidoId.split("-");
                    // partes[0] = "CUOTA", partes[1] = cuotaId
                    if (partes.length >= 2) {
                        int cuotaId = Integer.parseInt(partes[1]);
                        BCuota bCuota = new BCuota();
                        // Usamos monto 0.1 (prueba) — en producción el monto real
                        // puede venir del campo "Monto" en el payload de PagoFácil
                        double monto = payload.containsKey("Monto")
                                ? Double.parseDouble(payload.get("Monto").toString())
                                : 0.1;
                        String resultado = bCuota.pagarCuota(cuotaId, fecha, monto);
                        System.out.println("[PagoFácil Callback] Cuota " + cuotaId
                                + " marcada como pagada: " + resultado);
                    }
                }
            } else {
                System.out.println("[PagoFácil Callback] Estado no es PAGADO o PedidoID vacío. "
                        + "Estado=" + estado + " | PedidoID=" + pedidoId);
            }

        } catch (Exception e) {
            // Se responde 200 de todas formas para que PagoFácil no reintente
            // indefinidamente
            System.err.println("[PagoFácil Callback] Error procesando notificación: " + e.getMessage());
        }

        // Respuesta OBLIGATORIA con HTTP 200 que exige PagoFácil
        Map<String, Object> response = new HashMap<>();
        response.put("error", 0);
        response.put("status", 1);
        response.put("message", "Notificacion de pago recibida correctamente");
        response.put("values", true);
        return ResponseEntity.ok(response);
    }
}
