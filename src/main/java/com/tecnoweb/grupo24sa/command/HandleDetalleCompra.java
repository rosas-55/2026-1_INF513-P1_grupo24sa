package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BDetalleCompra;

import java.util.List;

/**
 * Handler para comandos de la entidad 'detallecompra'
 */
public class HandleDetalleCompra {

    public static String execute(String command, String params) {
        BDetalleCompra bDetalleCompra = new BDetalleCompra();
        try {
            switch (command) {
                case "registrar":     return registrar(bDetalleCompra, params);
                case "actualizar":    return actualizar(bDetalleCompra, params);
                case "eliminar":      return eliminar(bDetalleCompra, params);
                case "listarPorCompra": return listarPorCompra(bDetalleCompra, params);
                case "buscar":        return buscar(bDetalleCompra, params);
                default:              return "Comando no implementado: " + command;
            }
        } catch (NumberFormatException e) {
            return "Error: Parámetro numérico inválido - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /** registrar(cantidad, compra_id, insumo_id, precio_unitario, subtotal) */
    private static String registrar(BDetalleCompra b, String params) {
        String[] p = params.split(",");
        if (p.length < 5) return "Error: Uso: registrar(cantidad,compra_id,insumo_id,precio_unitario,subtotal)";
        return b.registrarDetalle(
                Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()),
                Integer.parseInt(p[2].trim()), Double.parseDouble(p[3].trim()),
                Double.parseDouble(p[4].trim()));
    }

    /** actualizar(id, cantidad, precio_unitario, subtotal) */
    private static String actualizar(BDetalleCompra b, String params) {
        String[] p = params.split(",");
        if (p.length < 4) return "Error: Uso: actualizar(id,cantidad,precio_unitario,subtotal)";
        return b.actualizarDetalle(Integer.parseInt(p[0].trim()),
                Integer.parseInt(p[1].trim()), Double.parseDouble(p[2].trim()),
                Double.parseDouble(p[3].trim()));
    }

    /** eliminar(id) */
    private static String eliminar(BDetalleCompra b, String params) {
        return b.eliminarDetalle(Integer.parseInt(params.trim()));
    }

    /** listarPorCompra(compra_id) */
    private static String listarPorCompra(BDetalleCompra b, String params) {
        int compraId = Integer.parseInt(params.trim());
        List<String[]> lista = b.listarPorCompra(compraId);
        if (lista.isEmpty()) return "No hay ítems en la compra " + compraId;
        StringBuilder sb = new StringBuilder("=== DETALLE COMPRA " + compraId + " ===\n");
        for (String[] d : lista) {
            sb.append("ID:").append(d[0])
              .append(" | InsumoID:").append(d[3])
              .append(" | Cantidad:").append(d[1])
              .append(" | P.Unit:").append(d[4])
              .append(" | Subtotal:").append(d[5])
              .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BDetalleCompra b, String params) {
        String[] d = b.buscarPorId(Integer.parseInt(params.trim()));
        if (d == null) return "Detalle no encontrado";
        return "ID: " + d[0] + "\nCompraID: " + d[2] + "\nInsumoID: " + d[3]
                + "\nCantidad: " + d[1] + "\nPrecio unitario: " + d[4] + "\nSubtotal: " + d[5];
    }
}
