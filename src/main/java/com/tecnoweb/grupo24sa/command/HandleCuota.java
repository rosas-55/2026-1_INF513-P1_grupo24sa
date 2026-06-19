package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BCuota;
import com.tecnoweb.grupo24sa.services.pagofacil.pagoFacilService;
import com.tecnoweb.grupo24sa.services.pagofacil.dto.QrRequest;
import com.tecnoweb.grupo24sa.services.pagofacil.dto.QrResponse;
import com.tecnoweb.grupo24sa.utils.ContextoEmail;
import com.tecnoweb.grupo24sa.utils.ReporteResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Handler para comandos de la entidad 'cuota'
 *
 * Comportamiento por comando:
 *   listarPorCliente()       → CLIENTE ve sus cuotas (auto-detecta ID del ctx)
 *   listarPorCliente(id)     → PROPIETARIO/VENDEDOR ve cuotas de un cliente
 *   pagar(id_cuota)          → CLIENTE: genera QR para pagar UNA cuota específica
 *   pagar(id,fecha,monto)    → PROPIETARIO/VENDEDOR: registra pago manualmente
 *   eliminar(id)             → Elimina cuota
 *   listarPorVenta(venta_id) → Lista cuotas de una venta
 *   buscar(id)               → Busca cuota por ID
 */
public class HandleCuota {

    public static ReporteResponse execute(String command, String params, ContextoEmail ctx) {
        BCuota bCuota = new BCuota();
        try {
            switch (command) {
                case "pagar":
                    return pagar(bCuota, params, ctx);
                case "eliminar":
                    return new ReporteResponse(eliminar(bCuota, params));
                case "listarPorVenta":
                    return new ReporteResponse(listarPorVenta(bCuota, params));
                case "buscar":
                    return new ReporteResponse(buscar(bCuota, params));
                case "listarPorCliente":
                    return listarPorCliente(bCuota, params, ctx);
                case "verificarPago":
                    return new ReporteResponse(verificarPago(bCuota, params));
                default:
                    return new ReporteResponse("Comando no implementado: " + command);
            }
        } catch (NumberFormatException e) {
            return new ReporteResponse("Error: Parámetro numérico inválido - " + e.getMessage());
        } catch (Exception e) {
            return new ReporteResponse("Error: " + e.getMessage());
        }
    }

    /**
     * listarPorCliente() o listarPorCliente(cliente_id)
     *
     * Sin params → usa ctx.getUsuarioId() (el CLIENTE ve sus propias cuotas)
     * Con params → usa ese ID (VENDEDOR/PROPIETARIO consulta cuotas de otro cliente)
     * Solo muestra datos, NO genera QR.
     */
    private static ReporteResponse listarPorCliente(BCuota b, String params, ContextoEmail ctx) {
        int clienteId;

        if (params.trim().isEmpty()) {
            if (ctx == null) {
                return new ReporteResponse("Error: No se pudo identificar al cliente. Usa: listarPorCliente(tu_id)");
            }
            clienteId = ctx.getUsuarioId();
        } else {
            clienteId = Integer.parseInt(params.trim());
        }

        String resText = b.listarPorCliente(clienteId);
        return new ReporteResponse(resText);
    }

    /**
     * pagar(id_cuota)  → 1 param:  genera QR de PagoFácil para ESA cuota (CLIENTE)
     * pagar(id, fecha_pago, monto_pagado) → 3 params: pago manual (PROPIETARIO/VENDEDOR)
     */
    private static ReporteResponse pagar(BCuota b, String params, ContextoEmail ctx) {
        String[] p = params.split(",");
        
        if (p.length == 1) {
            // ── QR para UNA cuota ──────────────────────────────────────
            int cuotaId = Integer.parseInt(p[0].trim());
            return generarQRPagoCuota(b, cuotaId, ctx);

        } else if (p.length >= 3) {
            // ── Pago manual (solo PROPIETARIO o VENDEDOR) ──────────────
            if (ctx == null || ctx.esCliente()) {
                return new ReporteResponse(
                    "Error: Solo el propietario o un vendedor pueden registrar pagos manualmente.\n" +
                    "Como cliente, usa: cuota pagar(id_cuota) para obtener un QR de pago.");
            }
            String resultado = b.pagarCuota(
                Integer.parseInt(p[0].trim()),
                p[1].trim(),
                Double.parseDouble(p[2].trim())
            );
            return new ReporteResponse(resultado);

        } else {
            return new ReporteResponse(
                "Error: Uso:\n" +
                "  cuota pagar(id_cuota)               → genera QR para pagar ESA cuota\n" +
                "  cuota pagar(id,fecha_pago,monto)     → (propietario) registra pago manual");
        }
    }

    /**
     * Genera un QR de PagoFácil para una cuota específica.
     */
    private static ReporteResponse generarQRPagoCuota(BCuota b, int cuotaId, ContextoEmail ctx) {
        String[] cuota = b.buscarPorId(cuotaId);
        if (cuota == null) {
            return new ReporteResponse("Error: Cuota no encontrada con ID: " + cuotaId);
        }

        String estado = cuota[1];
        if (!estado.equalsIgnoreCase("PENDIENTE") && !estado.equalsIgnoreCase("EN_MORA")) {
            return new ReporteResponse("La cuota " + cuotaId + " ya está en estado: " + estado + ". No requiere pago.");
        }

        int clienteId = ctx != null ? ctx.getUsuarioId() : 0;
        String nroCuota = cuota[6];
        double monto = Double.parseDouble(cuota[5]);

        pagoFacilService pfService = new pagoFacilService();
        if (!pfService.autenticar()) {
            return new ReporteResponse("Error: Falló la autenticación con PagoFácil. Verifica las credenciales.");
        }

        try {
            QrRequest req = new QrRequest();
            req.setPaymentMethod(34);
            req.setClientName("Cliente " + clienteId);
            req.setDocumentType(1);
            req.setDocumentId("000000");
            req.setPhoneNumber("70000000");
            req.setEmail("correo@ejemplo.com");
            req.setPaymentNumber("CUOTA-" + cuotaId + "-" + System.currentTimeMillis());
            req.setAmount(monto);
            req.setCurrency(2); // BOB
            req.setClientCode(String.valueOf(clienteId));
            req.setCallbackUrl(pfService.getCallbackUrl());

            List<QrRequest.OrderDetail> detalles = new ArrayList<>();
            detalles.add(new QrRequest.OrderDetail(1, "Pago Cuota N°" + nroCuota + " (ID " + cuotaId + ")", 1, monto, 0.0, monto));
            req.setOrderDetail(detalles);

            QrResponse qrRes = pfService.generarQR(req);
            if (qrRes != null && qrRes.getError() == 0 && qrRes.getValues() != null) {
                // Guardar el transactionId de PagoFácil en la cuota
                long transactionId = qrRes.getValues().getTransactionId();
                b.actualizarPagoFacilTransactionId(cuotaId, transactionId);

                String base64Data = qrRes.getValues().getQrBase64();
                if (base64Data.startsWith("data:image/png;base64,")) {
                    base64Data = base64Data.substring(22);
                }
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                File qrFile = new File("qr_cuota_" + cuotaId + "_" + System.currentTimeMillis() + ".png");
                try (FileOutputStream fos = new FileOutputStream(qrFile)) {
                    fos.write(imageBytes);
                }
                ReporteResponse response = new ReporteResponse(
                    "QR generado para Cuota N°" + nroCuota + " (ID " + cuotaId + ")\n" +
                    "Monto: Bs. " + String.format("%.2f", monto) + "\n" +
                    "Transaction ID: " + transactionId + "\n" +
                    "Escanea el código QR adjunto para pagar."
                );
                response.addArchivoAdjunto(qrFile);
                return response;
            } else {
                String errMsg = (qrRes != null) ? qrRes.getMessage() : "Error desconocido de red";
                return new ReporteResponse("Error: No se pudo generar el QR de PagoFácil: " + errMsg);
            }
        } catch (Exception e) {
            System.err.println("Error generando QR para cuota " + cuotaId + ": " + e.getMessage());
            return new ReporteResponse("Error: Fallo al conectar con PagoFácil: " + e.getMessage());
        }
    }

    /** eliminar(id) */
    private static String eliminar(BCuota b, String params) {
        return b.eliminarCuota(Integer.parseInt(params.trim()));
    }

    /** listarPorVenta(venta_id) */
    private static String listarPorVenta(BCuota b, String params) {
        int ventaId = Integer.parseInt(params.trim());
        List<String[]> lista = b.listarPorVenta(ventaId);
        if (lista.isEmpty())
            return "No hay cuotas para la venta " + ventaId;
        StringBuilder sb = new StringBuilder("=== CUOTAS DE VENTA " + ventaId + " ===\n");
        for (String[] c : lista) {
            sb.append("N°").append(c[6])
                    .append(" | ID:").append(c[0])
                    .append(" | Estado:").append(c[1])
                    .append(" | Vence:").append(c[3])
                    .append(" | Monto:").append(c[5])
                    .append(" | PlanPago:").append(c[7])
                    .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BCuota b, String params) {
        String[] c = b.buscarPorId(Integer.parseInt(params.trim()));
        if (c == null)
            return "Cuota no encontrada";
        return "ID: " + c[0] + "\nN° Cuota: " + c[6]
                + "\nEstado: " + c[1] + "\nFecha pago: " + c[2]
                + "\nFecha vencimiento: " + c[3] + "\nMonto: " + c[5]
                + "\nInterés mora: " + c[4] + "%" + "\nPlan pago: " + c[7]
                + "\nVentaID: " + c[8];
    }

    /** verificarPago(id_cuota) */
    private static String verificarPago(BCuota b, String params) {
        return b.verificarPago(Integer.parseInt(params.trim()));
    }
}
