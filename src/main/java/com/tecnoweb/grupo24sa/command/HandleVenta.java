package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BVenta;
import com.tecnoweb.grupo24sa.services.pagofacil.pagoFacilService;
import com.tecnoweb.grupo24sa.services.pagofacil.dto.QrRequest;
import com.tecnoweb.grupo24sa.services.pagofacil.dto.QrResponse;
import com.tecnoweb.grupo24sa.utils.ContextoEmail;
import com.tecnoweb.grupo24sa.utils.ReporteResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

/**
 * Handler para comandos de la entidad 'venta'
 *
 * Cambios respecto a la versión anterior:
 *  - Firma: execute(command, params, ctx)  — recibe el ContextoEmail del remitente
 *  - 'fecha'      se asigna automáticamente como LocalDate.now()
 *  - 'vendedor_id' se obtiene de ctx.getUsuarioId() (el usuario que envió el correo)
 *
 * Nuevo formato de 'registrar':
 *   venta registrar(cliente_id, estado, interes_mora, nro_cuotas, tipo, [prod_id;cantidad], ...)
 */
public class HandleVenta {

    public static ReporteResponse execute(String command, String params, ContextoEmail ctx) {
        BVenta bVenta = new BVenta();
        try {
            switch (command) {
                case "registrar":        return registrar(bVenta, params, ctx);
                case "actualizarEstado": return new ReporteResponse(actualizarEstado(bVenta, params));
                case "eliminar":         return new ReporteResponse(eliminar(bVenta, params));
                case "listar":           return new ReporteResponse(listar(bVenta));
                case "buscar":           return new ReporteResponse(buscar(bVenta, params));
                case "listarPorCliente": return new ReporteResponse(listarPorCliente(bVenta, params));
                default:                 return new ReporteResponse("Comando no implementado: " + command);
            }
        } catch (NumberFormatException e) {
            return new ReporteResponse("Error: Parámetro numérico inválido - " + e.getMessage());
        } catch (Exception e) {
            return new ReporteResponse("Error: " + e.getMessage());
        }
    }

    /**
     * registrar(nro_cuotas, tipo, [prod_id1;cant1], ...)
     *
     * - 'cliente_id'  → auto: ID del remitente (ctx.getUsuarioId())
     * - 'estado'      → auto: "PENDIENTE"
     * - 'interes_mora'→ auto: 0.0
     * - 'fecha'       → auto: LocalDate.now()
     * - 'vendedor_id' → auto: ID del usuario que envió el correo (ctx.getUsuarioId())
     */
    private static ReporteResponse registrar(BVenta b, String params, ContextoEmail ctx) {
        String[] p = params.split(",");
        if (p.length < 2) return new ReporteResponse(
            "Error: Uso: venta registrar(nro_cuotas,tipo,[prod_id;cant],...)\n" +
            "  Nota: cliente_id, estado, interes_mora, fecha y vendedor se asignan automaticamente.\n" +
            "  Ejemplo: venta registrar(1,CONTADO,[1;2])\n" +
            "  Ejemplo: venta registrar(3,CREDITO,[1;1],[2;2])");

        // Auto-asignaciones
        int    clienteId   = ctx.getUsuarioId();             // ID del remitente (cliente o vendedor)
        String estado      = "PENDIENTE";                    // estado por defecto
        double interesMora = 0.0;                            // interés por defecto
        int    nroCuotas   = Integer.parseInt(p[0].trim());
        String tipo        = p[1].trim().toUpperCase();
        String fecha       = LocalDate.now().toString();      // fecha del servidor
        int    vendedorId  = ctx.getUsuarioId();              // ID del remitente

        List<String[]> items = new ArrayList<>();
        for (int i = 2; i < p.length; i++) {
            String itemStr = p[i].trim();
            if (itemStr.startsWith("[") && itemStr.endsWith("]")) {
                itemStr = itemStr.substring(1, itemStr.length() - 1);
            }
            String[] itemParts = itemStr.split(";");
            if (itemParts.length >= 2) {
                items.add(new String[]{itemParts[0].trim(), itemParts[1].trim()});
            }
        }

        String resDb = b.registrarVenta(clienteId, estado, fecha, interesMora, nroCuotas, tipo, vendedorId, items);
        ReporteResponse response = new ReporteResponse(resDb);

        // Si la venta fue exitosa y es al contado, generar el QR de PagoFacil
        if (resDb.startsWith("Venta registrada exitosamente") && tipo.equals("CONTADO")) {
            String ventaIdStr = resDb.replaceAll(".*ID:\\s*(\\d+).*", "$1");
            int ventaId = -1;
            try {
                ventaId = Integer.parseInt(ventaIdStr);
            } catch (Exception e) {
                // ignorar si no se pudo parsear
            }

            pagoFacilService pfService = new pagoFacilService();
            if (pfService.autenticar()) {
                try {
                    QrRequest req = new QrRequest();
                    req.setPaymentMethod(34);
                    req.setClientName("Cliente " + clienteId);
                    req.setDocumentType(1);
                    req.setDocumentId("000000");
                    req.setPhoneNumber("70000000");
                    req.setEmail("correo@ejemplo.com");
                    req.setPaymentNumber("VTA-" + ventaIdStr + "-" + System.currentTimeMillis());
                    req.setAmount(0.1); // Monto de prueba (proyecto académico)
                    req.setCurrency(2); // BOB
                    req.setClientCode(String.valueOf(clienteId));
                    req.setCallbackUrl(pfService.getCallbackUrl());

                    List<QrRequest.OrderDetail> detalles = new ArrayList<>();
                    detalles.add(new QrRequest.OrderDetail(1, "Pago Venta al Contado", 1, 0.1, 0.0, 0.1));
                    req.setOrderDetail(detalles);

                    QrResponse qrRes = pfService.generarQR(req);
                    if (qrRes != null && qrRes.getError() == 0 && qrRes.getValues() != null) {
                        String base64Data = qrRes.getValues().getQrBase64();
                        if (base64Data.startsWith("data:image/png;base64,")) {
                            base64Data = base64Data.substring(22);
                        }
                        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                        File qrFile = new File("qr_venta_" + System.currentTimeMillis() + ".png");
                        try (FileOutputStream fos = new FileOutputStream(qrFile)) {
                            fos.write(imageBytes);
                        }
                        response.addArchivoAdjunto(qrFile);
                        response.setTextoRespuesta(resDb + "\n\nSe ha adjuntado el código QR de PagoFácil para el pago al contado.");
                    } else {
                        // FALLO EN GENERAR QR - ROLLBACK
                        if (ventaId != -1) b.eliminarVenta(ventaId);
                        String errMsg = (qrRes != null) ? qrRes.getMessage() : "Error desconocido de red";
                        response.setTextoRespuesta("Error: No se pudo generar el QR de PagoFácil (" + errMsg + ").\nLa venta ha sido cancelada para evitar cobros pendientes.");
                    }
                } catch (Exception e) {
                    System.err.println("Error generando QR: " + e.getMessage());
                    // FALLO POR EXCEPCIÓN - ROLLBACK
                    if (ventaId != -1) b.eliminarVenta(ventaId);
                    response.setTextoRespuesta("Error: Hubo un fallo interno al conectar con PagoFácil (" + e.getMessage() + ").\nLa venta ha sido cancelada.");
                }
            } else {
                // FALLO DE AUTENTICACIÓN - ROLLBACK
                if (ventaId != -1) b.eliminarVenta(ventaId);
                response.setTextoRespuesta("Error: Falló la autenticación con PagoFácil. Verifica las credenciales en .env.\nLa venta ha sido cancelada.");
            }
        }

        return response;
    }

    /** actualizarEstado(id, estado) */
    private static String actualizarEstado(BVenta b, String params) {
        String[] p = params.split(",");
        if (p.length < 2) return "Error: Uso: actualizarEstado(id,estado)";
        return b.actualizarEstadoVenta(Integer.parseInt(p[0].trim()), p[1].trim());
    }

    /** eliminar(id) */
    private static String eliminar(BVenta b, String params) {
        return b.eliminarVenta(Integer.parseInt(params.trim()));
    }

    private static String listar(BVenta b) {
        List<String[]> lista = b.listarVentas();
        if (lista.isEmpty()) return "No hay ventas registradas";
        StringBuilder sb = new StringBuilder("=== VENTAS ===\n");
        for (String[] v : lista) {
            sb.append("ID:").append(v[0])
              .append(" | ClienteID:").append(v[1])
              .append(" | Tipo:").append(v[6])
              .append(" | Total:").append(v[7])
              .append(" | Cuotas:").append(v[5])
              .append(" | Estado:").append(v[2])
              .append(" | Fecha:").append(v[3])
              .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BVenta b, String params) {
        String[] v = b.buscarPorId(Integer.parseInt(params.trim()));
        if (v == null) return "Venta no encontrada";
        return "ID: " + v[0] + "\nClienteID: " + v[1] + "\nEstado: " + v[2]
                + "\nFecha: " + v[3] + "\nTipo: " + v[6]
                + "\nNro Cuotas: " + v[5] + "\nInteres Mora: " + v[4]
                + "\nTotal: " + v[7] + "\nVendedorID: " + v[8];
    }

    /** listarPorCliente(cliente_id) */
    private static String listarPorCliente(BVenta b, String params) {
        int clienteId = Integer.parseInt(params.trim());
        List<String[]> lista = b.listarPorCliente(clienteId);
        if (lista == null) return "Error: Cliente con ID " + clienteId + " no encontrado";
        if (lista.isEmpty()) return "No hay ventas para el cliente " + clienteId;
        StringBuilder sb = new StringBuilder("=== VENTAS DEL CLIENTE " + clienteId + " ===\n");
        for (String[] v : lista) {
            sb.append("ID:").append(v[0])
              .append(" | Tipo:").append(v[6])
              .append(" | Total:").append(v[7])
              .append(" | Estado:").append(v[2])
              .append(" | Fecha:").append(v[3])
              .append("\n");
        }
        return sb.toString();
    }
}
