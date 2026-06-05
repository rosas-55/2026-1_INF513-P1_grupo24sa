package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BVenta;
import com.tecnoweb.grupo24sa.services.pagofacil.pagoFacilService;
import com.tecnoweb.grupo24sa.services.pagofacil.dto.QrRequest;
import com.tecnoweb.grupo24sa.services.pagofacil.dto.QrResponse;
import com.tecnoweb.grupo24sa.utils.ReporteResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

/**
 * Handler para comandos de la entidad 'venta'
 */
public class HandleVenta {

    public static ReporteResponse execute(String command, String params) {
        BVenta bVenta = new BVenta();
        try {
            switch (command) {
                case "registrar":        return registrar(bVenta, params);
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

    /** registrar(cliente_id, estado, fecha, interes_mora, nro_cuotas, tipo, vendedor_id, [producto_id1;cantidad1], ...) */
    private static ReporteResponse registrar(BVenta b, String params) {
        String[] p = params.split(",");
        if (p.length < 7) return new ReporteResponse("Error: Uso: registrar(cliente_id,estado,fecha,interes_mora,nro_cuotas,tipo,vendedor_id,[producto_id1;cantidad1],...)");
        
        int clienteId = Integer.parseInt(p[0].trim());
        String estado = p[1].trim();
        String fecha = p[2].trim();
        double interesMora = Double.parseDouble(p[3].trim());
        int nroCuotas = Integer.parseInt(p[4].trim());
        String tipo = p[5].trim().toUpperCase();
        int vendedorId = Integer.parseInt(p[6].trim());
        
        List<String[]> items = new ArrayList<>();
        for (int i = 7; i < p.length; i++) {
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
            pagoFacilService pfService = new pagoFacilService();
            if (pfService.autenticar()) {
                try {
                    QrRequest req = new QrRequest();
                    req.setPaymentMethod(34); // as per docs example
                    req.setClientName("Cliente " + clienteId);
                    req.setDocumentType(1);
                    req.setDocumentId("000000"); // Placeholder
                    req.setPhoneNumber("70000000"); // Placeholder
                    req.setEmail("correo@ejemplo.com"); // Placeholder
                    req.setPaymentNumber("VTA-" + System.currentTimeMillis());
                    req.setAmount(0.1); // Test amount as requested
                    req.setCurrency(2); // BOB
                    req.setClientCode(String.valueOf(clienteId));
                    req.setCallbackUrl("https://webhook.site/callback-test");

                    // AGREGADO: Enviar el array orderDetail obligatorio para la API de PagoFacil
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
                        response.setTextoRespuesta(resDb + "\n\n(Advertencia: No se pudo generar el QR de PagoFácil)");
                    }
                } catch (Exception e) {
                    System.err.println("Error generando QR: " + e.getMessage());
                }
            } else {
                response.setTextoRespuesta(resDb + "\n\n(Advertencia: Falló la autenticación con PagoFácil, no se generó QR)");
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
