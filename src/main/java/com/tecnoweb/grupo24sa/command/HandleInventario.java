package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BInventario;

import java.util.List;

/**
 * Handler para comandos de la entidad 'inventario'
 */
public class HandleInventario {

    public static String execute(String command, String params) {
        BInventario bInventario = new BInventario();
        try {
            switch (command) {
                case "registrar":    return registrar(bInventario, params);
                case "actualizar":   return actualizar(bInventario, params);
                case "eliminar":     return eliminar(bInventario, params);
                case "listar":       return listar(bInventario);
                case "buscar":       return buscar(bInventario, params);
                case "listarPorInsumo": return listarPorInsumo(bInventario, params);
                default:             return "Comando no implementado: " + command;
            }
        } catch (NumberFormatException e) {
            return "Error: Parámetro numérico inválido - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /** registrar(cantidad, fecha, insumo_id, costo_unitario, observacion, tipo_movimiento) */
    private static String registrar(BInventario b, String params) {
        String[] p = params.split(",");
        if (p.length < 6) return "Error: Uso: registrar(cantidad,fecha,insumo_id,costo_unitario,observacion,tipo_movimiento)";
        return b.registrarMovimiento(
                Double.parseDouble(p[0].trim()), p[1].trim(),
                Integer.parseInt(p[2].trim()), Double.parseDouble(p[3].trim()), p[4].trim(), p[5].trim());
    }

    /** actualizar(id, cantidad, observacion) */
    private static String actualizar(BInventario b, String params) {
        String[] p = params.split(",");
        if (p.length < 3) return "Error: Uso: actualizar(id,cantidad,observacion)";
        return b.actualizarMovimiento(Integer.parseInt(p[0].trim()),
                Double.parseDouble(p[1].trim()), p[2].trim());
    }

    /** eliminar(id) */
    private static String eliminar(BInventario b, String params) {
        return b.eliminarMovimiento(Integer.parseInt(params.trim()));
    }

    private static String listar(BInventario b) {
        List<String[]> lista = b.listarMovimientos();
        if (lista.isEmpty()) return "No hay movimientos de inventario registrados";
        StringBuilder sb = new StringBuilder("=== MOVIMIENTOS DE INVENTARIO ===\n");
        for (String[] m : lista) {
            sb.append("ID:").append(m[0])
              .append(" | InsumoID:").append(m[3])
              .append(" | Tipo:").append(m[6])
              .append(" | Cant:").append(m[1])
              .append(" | Costo:").append(m[4])
              .append(" | Total:").append(m[7])
              .append(" | Fecha:").append(m[2])
              .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BInventario b, String params) {
        String[] m = b.buscarPorId(Integer.parseInt(params.trim()));
        if (m == null) return "Movimiento no encontrado";
        return "ID: " + m[0] + "\nInsumoID: " + m[3] + "\nTipo: " + m[6]
                + "\nCantidad: " + m[1] + "\nCosto Unitario: " + m[4]
                + "\nValor Total: " + m[7]
                + "\nFecha: " + m[2] + "\nObservacion: " + m[5];
    }

    /** listarPorInsumo(insumo_id) */
    private static String listarPorInsumo(BInventario b, String params) {
        int insumoId = Integer.parseInt(params.trim());
        List<String[]> lista = b.listarPorInsumo(insumoId);
        if (lista.isEmpty()) return "No hay movimientos para el insumo " + insumoId;
        StringBuilder sb = new StringBuilder("=== MOVIMIENTOS DEL INSUMO " + insumoId + " ===\n");
        for (String[] m : lista) {
            sb.append("ID:").append(m[0])
              .append(" | Tipo:").append(m[6])
              .append(" | Cant:").append(m[1])
              .append(" | Costo:").append(m[4])
              .append(" | Total:").append(m[7])
              .append(" | Fecha:").append(m[2])
              .append("\n");
        }
        return sb.toString();
    }
}
