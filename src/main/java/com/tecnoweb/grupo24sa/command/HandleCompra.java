package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BCompra;

import java.util.List;
import java.util.ArrayList;

/**
 * Handler para comandos de la entidad 'compra'
 */
public class HandleCompra {

    public static String execute(String command, String params) {
        BCompra bCompra = new BCompra();
        try {
            switch (command) {
                case "registrar":        return registrar(bCompra, params);
                case "actualizarEstado": return actualizarEstado(bCompra, params);
                case "eliminar":         return eliminar(bCompra, params);
                case "listar":           return listar(bCompra);
                case "buscar":           return buscar(bCompra, params);
                case "listarPorProveedor": return listarPorProveedor(bCompra, params);
                default:                 return "Comando no implementado: " + command;
            }
        } catch (NumberFormatException e) {
            return "Error: Parámetro numérico inválido - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /** registrar(estado, fecha, proveedor_id, [insumo_id1;cantidad1;precio_unitario1], ...) */
    private static String registrar(BCompra b, String params) {
        String[] p = params.split(",");
        if (p.length < 3) return "Error: Uso: registrar(estado,fecha,proveedor_id,[insumo_id1;cantidad1;precio_unitario1],...)";
        
        String estado = p[0].trim();
        String fecha = p[1].trim();
        int proveedorId = Integer.parseInt(p[2].trim());
        
        List<String[]> items = new ArrayList<>();
        for (int i = 3; i < p.length; i++) {
            String itemStr = p[i].trim();
            if (itemStr.startsWith("[") && itemStr.endsWith("]")) {
                itemStr = itemStr.substring(1, itemStr.length() - 1);
            }
            String[] itemParts = itemStr.split(";");
            if (itemParts.length >= 3) {
                items.add(new String[]{itemParts[0].trim(), itemParts[1].trim(), itemParts[2].trim()});
            }
        }
        
        return b.registrarCompra(estado, fecha, proveedorId, items);
    }

    /** actualizarEstado(id, estado) */
    private static String actualizarEstado(BCompra b, String params) {
        String[] p = params.split(",");
        if (p.length < 2) return "Error: Uso: actualizarEstado(id,estado)";
        return b.actualizarEstadoCompra(Integer.parseInt(p[0].trim()), p[1].trim());
    }

    /** eliminar(id) */
    private static String eliminar(BCompra b, String params) {
        return b.eliminarCompra(Integer.parseInt(params.trim()));
    }

    private static String listar(BCompra b) {
        List<String[]> lista = b.listarCompras();
        if (lista.isEmpty()) return "No hay compras registradas";
        StringBuilder sb = new StringBuilder("=== COMPRAS ===\n");
        for (String[] c : lista) {
            sb.append("ID:").append(c[0])
              .append(" | Estado:").append(c[1])
              .append(" | Fecha:").append(c[2])
              .append(" | ProveedorID:").append(c[3])
              .append(" | Total:").append(c[4])
              .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BCompra b, String params) {
        String[] c = b.buscarPorId(Integer.parseInt(params.trim()));
        if (c == null) return "Compra no encontrada";
        return "ID: " + c[0] + "\nEstado: " + c[1] + "\nFecha: " + c[2]
                + "\nProveedorID: " + c[3] + "\nTotal: " + c[4];
    }

    /** listarPorProveedor(proveedor_id) */
    private static String listarPorProveedor(BCompra b, String params) {
        int provId = Integer.parseInt(params.trim());
        List<String[]> lista = b.listarPorProveedor(provId);
        if (lista == null) return "Error: Proveedor con ID " + provId + " no encontrado";
        if (lista.isEmpty()) return "No hay compras para el proveedor " + provId;
        StringBuilder sb = new StringBuilder("=== COMPRAS DEL PROVEEDOR " + provId + " ===\n");
        for (String[] c : lista) {
            sb.append("ID:").append(c[0])
              .append(" | Estado:").append(c[1])
              .append(" | Total:").append(c[4])
              .append(" | Fecha:").append(c[2])
              .append("\n");
        }
        return sb.toString();
    }
}
