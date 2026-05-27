package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BVenta;

import java.util.List;
import java.util.ArrayList;

/**
 * Handler para comandos de la entidad 'venta'
 */
public class HandleVenta {

    public static String execute(String command, String params) {
        BVenta bVenta = new BVenta();
        try {
            switch (command) {
                case "registrar":        return registrar(bVenta, params);
                case "actualizarEstado": return actualizarEstado(bVenta, params);
                case "eliminar":         return eliminar(bVenta, params);
                case "listar":           return listar(bVenta);
                case "buscar":           return buscar(bVenta, params);
                case "listarPorCliente": return listarPorCliente(bVenta, params);
                default:                 return "Comando no implementado: " + command;
            }
        } catch (NumberFormatException e) {
            return "Error: Parámetro numérico inválido - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /** registrar(cliente_id, estado, fecha, interes_mora, nro_cuotas, tipo, vendedor_id, [producto_id1;cantidad1], ...) */
    private static String registrar(BVenta b, String params) {
        String[] p = params.split(",");
        if (p.length < 7) return "Error: Uso: registrar(cliente_id,estado,fecha,interes_mora,nro_cuotas,tipo,vendedor_id,[producto_id1;cantidad1],...)";
        
        int clienteId = Integer.parseInt(p[0].trim());
        String estado = p[1].trim();
        String fecha = p[2].trim();
        double interesMora = Double.parseDouble(p[3].trim());
        int nroCuotas = Integer.parseInt(p[4].trim());
        String tipo = p[5].trim();
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
        
        return b.registrarVenta(clienteId, estado, fecha, interesMora, nroCuotas, tipo, vendedorId, items);
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
