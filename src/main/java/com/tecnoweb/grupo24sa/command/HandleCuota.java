package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BCuota;
import com.tecnoweb.grupo24sa.services.pagofacil.pagoFacilService;
import com.tecnoweb.grupo24sa.services.pagofacil.dto.QrRequest;
import com.tecnoweb.grupo24sa.services.pagofacil.dto.QrResponse;
import com.tecnoweb.grupo24sa.utils.ReporteResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.List;

/**
 * Handler para comandos de la entidad 'cuota'
 */
public class HandleCuota {

    public static ReporteResponse execute(String command, String params) {
        BCuota bCuota = new BCuota();
        try {
            switch (command) {
                case "pagar":        return new ReporteResponse(pagar(bCuota, params));
                case "eliminar":     return new ReporteResponse(eliminar(bCuota, params));
                case "listarPorVenta": return new ReporteResponse(listarPorVenta(bCuota, params));
                case "buscar":       return new ReporteResponse(buscar(bCuota, params));
                case "listarPorCliente": return listarPorCliente(bCuota, params);
                default:             return new ReporteResponse("Comando no implementado: " + command);
            }
        } catch (NumberFormatException e) {
            return new ReporteResponse("Error: Parámetro numérico inválido - " + e.getMessage());
        } catch (Exception e) {
            return new ReporteResponse("Error: " + e.getMessage());
        }
    }

    /** listarPorCliente(cliente_id) */
    private static ReporteResponse listarPorCliente(BCuota b, String params) {
        int clienteId = Integer.parseInt(params.trim());
        String resText = b.listarPorCliente(clienteId);
        ReporteResponse response = new ReporteResponse(resText);

        List<String[]> pendientes = b.obtenerCuotasPendientes(clienteId);
        if (!pendientes.isEmpty()) {
            pagoFacilService pfService = new pagoFacilService();
            if (pfService.autenticar()) {
                int count = 0;
                for (String[] c : pendientes) {
                    try {
                        String idCuota = c[0];
                        String nroCuota = c[6];
                        QrRequest req = new QrRequest();
                        req.setPaymentMethod(34); // Ej. Tigo Money / QR
                        req.setClientName("Cliente " + clienteId);
                        req.setDocumentType(1);
                        req.setDocumentId("000000");
                        req.setPhoneNumber("70000000");
                        req.setEmail("correo@ejemplo.com");
                        req.setPaymentNumber("CUOTA-" + idCuota + "-" + System.currentTimeMillis());
                        req.setAmount(0.1); // Test amount
                        req.setCurrency(2); // BOB
                        req.setClientCode(String.valueOf(clienteId));
                        req.setCallbackUrl("https://webhook.site/callback-test");

                        QrResponse qrRes = pfService.generarQR(req);
                        if (qrRes != null && qrRes.getError() == 0 && qrRes.getValues() != null) {
                            String base64Data = qrRes.getValues().getQrBase64();
                            if (base64Data.startsWith("data:image/png;base64,")) {
                                base64Data = base64Data.substring(22);
                            }
                            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                            File qrFile = new File("qr_cuota_" + nroCuota + "_" + System.currentTimeMillis() + ".png");
                            try (FileOutputStream fos = new FileOutputStream(qrFile)) {
                                fos.write(imageBytes);
                            }
                            response.addArchivoAdjunto(qrFile);
                            count++;
                        }
                    } catch (Exception e) {
                        System.err.println("Error generando QR para cuota: " + e.getMessage());
                    }
                }
                if (count > 0) {
                    response.setTextoRespuesta(resText + "\n\nSe han adjuntado " + count + " código(s) QR para pagar sus cuotas pendientes o en mora.");
                } else {
                    response.setTextoRespuesta(resText + "\n\n(No se pudo adjuntar los códigos QR de PagoFácil por un error en el servicio).");
                }
            } else {
                response.setTextoRespuesta(resText + "\n\n(Advertencia: Falló la autenticación con PagoFácil, no se generaron QRs).");
            }
        }

        return response;
    }

    /** pagar(id, fecha_pago, monto_pagado) */
    private static String pagar(BCuota b, String params) {
        String[] p = params.split(",");
        if (p.length < 3) return "Error: Uso: pagar(id,fecha_pago,monto_pagado)";
        return b.pagarCuota(Integer.parseInt(p[0].trim()), p[1].trim(),
                Double.parseDouble(p[2].trim()));
    }

    /** eliminar(id) */
    private static String eliminar(BCuota b, String params) {
        return b.eliminarCuota(Integer.parseInt(params.trim()));
    }

    /** listarPorVenta(venta_id) */
    private static String listarPorVenta(BCuota b, String params) {
        int ventaId = Integer.parseInt(params.trim());
        List<String[]> lista = b.listarPorVenta(ventaId);
        if (lista.isEmpty()) return "No hay cuotas para la venta " + ventaId;
        StringBuilder sb = new StringBuilder("=== CUOTAS DE VENTA " + ventaId + " ===\n");
        for (String[] c : lista) {
            sb.append("N°").append(c[6])
              .append(" | ID:").append(c[0])
              .append(" | Estado:").append(c[1])
              .append(" | Vence:").append(c[3])
              .append(" | Pagado:").append(c[5])
              .append(" | PlanPago:").append(c[7])
              .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BCuota b, String params) {
        String[] c = b.buscarPorId(Integer.parseInt(params.trim()));
        if (c == null) return "Cuota no encontrada";
        return "ID: " + c[0] + "\nN° Cuota: " + c[6]
                + "\nEstado: " + c[1] + "\nFecha pago: " + c[2]
                + "\nFecha vencimiento: " + c[3] + "\nMonto pagado: " + c[5]
                + "\nInterés mora: " + c[4] + "%" + "\nPlan pago: " + c[7]
                + "\nVentaID: " + c[8];
    }
}
