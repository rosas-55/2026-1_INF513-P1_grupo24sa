package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BDetalleVenta;

import java.util.List;

/**
 * Handler para comandos de la entidad 'detalleventa'
 */
public class HandleDetalleVenta {

    public static String execute(String command, String params) {
        BDetalleVenta bDetalleVenta = new BDetalleVenta();
        try {
            switch (command) {
                case "registrar":    return registrar(bDetalleVenta, params);
                case "actualizar":   return actualizar(bDetalleVenta, params);
                case "eliminar":     return eliminar(bDetalleVenta, params);
                case "listarPorVenta": return listarPorVenta(bDetalleVenta, params);
                case "buscar":       return buscar(bDetalleVenta, params);
                default:             return "Comando no implementado: " + command;
            }
        } catch (NumberFormatException e) {
            return "Error: Parámetro numérico inválido - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /** registrar(cantidad, precio_unitario, producto_id, sub_total, venta_id) */
    private static String registrar(BDetalleVenta b, String params) {
        String[] p = params.split(",");
        if (p.length < 5) return "Error: Uso: registrar(cantidad,precio_unitario,producto_id,sub_total,venta_id)";
        return b.registrarDetalle(
                Integer.parseInt(p[0].trim()), Double.parseDouble(p[1].trim()),
                Integer.parseInt(p[2].trim()), Double.parseDouble(p[3].trim()),
                Integer.parseInt(p[4].trim()));
    }

    /** actualizar(id, cantidad, precio_unitario, producto_id, sub_total) */
    private static String actualizar(BDetalleVenta b, String params) {
        String[] p = params.split(",");
        if (p.length < 5) return "Error: Uso: actualizar(id,cantidad,precio_unitario,producto_id,sub_total)";
        return b.actualizarDetalle(Integer.parseInt(p[0].trim()),
                Integer.parseInt(p[1].trim()), Double.parseDouble(p[2].trim()),
                Integer.parseInt(p[3].trim()), Double.parseDouble(p[4].trim()));
    }

    /** eliminar(id) */
    private static String eliminar(BDetalleVenta b, String params) {
        return b.eliminarDetalle(Integer.parseInt(params.trim()));
    }

    /** listarPorVenta(venta_id) */
    private static String listarPorVenta(BDetalleVenta b, String params) {
        int ventaId = Integer.parseInt(params.trim());
        List<String[]> lista = b.listarPorVenta(ventaId);
        if (lista.isEmpty()) return "No hay ítems en la venta " + ventaId;
        StringBuilder sb = new StringBuilder("=== DETALLE VENTA " + ventaId + " ===\n");
        for (String[] d : lista) {
            sb.append("ID:").append(d[0])
              .append(" | ProductoID:").append(d[3])
              .append(" | Cant:").append(d[1])
              .append(" | P.Unit:").append(d[2])
              .append(" | Subtotal:").append(d[4])
              .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BDetalleVenta b, String params) {
        String[] d = b.buscarPorId(Integer.parseInt(params.trim()));
        if (d == null) return "Detalle no encontrado";
        return "ID: " + d[0] + "\nVentaID: " + d[5] + "\nProductoID: " + d[3]
                + "\nCantidad: " + d[1] + "\nPrecio unitario: " + d[2] + "\nSubtotal: " + d[4];
    }
}
