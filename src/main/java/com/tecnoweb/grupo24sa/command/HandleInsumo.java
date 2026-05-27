package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BInsumo;

import java.util.List;

/**
 * Handler para comandos de la entidad 'insumo'
 */
public class HandleInsumo {

    public static String execute(String command, String params) {
        BInsumo bInsumo = new BInsumo();
        try {
            switch (command) {
                case "registrar":      return registrar(bInsumo, params);
                case "actualizar":     return actualizar(bInsumo, params);
                case "eliminar":       return eliminar(bInsumo, params);
                case "listar":         return listar(bInsumo);
                case "buscar":         return buscar(bInsumo, params);
                case "listarStockBajo": return listarStockBajo(bInsumo);
                default:               return "Comando no implementado: " + command;
            }
        } catch (NumberFormatException e) {
            return "Error: Parámetro numérico inválido - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /** registrar(costo_unitario, descripcion, estado, nombre, stock_actual, stock_minimo, unidad_medida) */
    private static String registrar(BInsumo b, String params) {
        String[] p = params.split(",");
        if (p.length < 7) return "Error: Uso: registrar(costo_unitario,descripcion,estado,nombre,stock_actual,stock_minimo,unidad_medida)";
        return b.registrarInsumo(
                Double.parseDouble(p[0].trim()), p[1].trim(), p[2].trim(),
                p[3].trim(), Double.parseDouble(p[4].trim()),
                Double.parseDouble(p[5].trim()), p[6].trim());
    }

    /** actualizar(id, costo_unitario, descripcion, estado, nombre, stock_actual, stock_minimo, unidad_medida) */
    private static String actualizar(BInsumo b, String params) {
        String[] p = params.split(",");
        if (p.length < 8) return "Error: Uso: actualizar(id,costo_unitario,descripcion,estado,nombre,stock_actual,stock_minimo,unidad_medida)";
        return b.actualizarInsumo(
                Integer.parseInt(p[0].trim()), Double.parseDouble(p[1].trim()),
                p[2].trim(), p[3].trim(), p[4].trim(),
                Double.parseDouble(p[5].trim()), Double.parseDouble(p[6].trim()), p[7].trim());
    }

    /** eliminar(id) */
    private static String eliminar(BInsumo b, String params) {
        return b.eliminarInsumo(Integer.parseInt(params.trim()));
    }

    private static String listar(BInsumo b) {
        List<String[]> lista = b.listarInsumos();
        if (lista.isEmpty()) return "No hay insumos registrados";
        StringBuilder sb = new StringBuilder("=== INSUMOS ===\n");
        for (String[] i : lista) {
            sb.append("ID:").append(i[0])
              .append(" | Nombre:").append(i[4])
              .append(" | Stock:").append(i[5]).append(" ").append(i[7])
              .append(" | Min:").append(i[6])
              .append(" | Costo:").append(i[1])
              .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BInsumo b, String params) {
        String[] i = b.buscarPorId(Integer.parseInt(params.trim()));
        if (i == null) return "Insumo no encontrado";
        return "ID: " + i[0] + "\nNombre: " + i[4] + "\nDescripcion: " + i[2]
                + "\nEstado: " + i[3] + "\nCosto: " + i[1]
                + "\nStock actual: " + i[5] + " " + i[7]
                + "\nStock mínimo: " + i[6] + " " + i[7];
    }

    private static String listarStockBajo(BInsumo b) {
        List<String[]> lista = b.listarStockBajo();
        if (lista.isEmpty()) return "Todos los insumos tienen stock suficiente";
        StringBuilder sb = new StringBuilder("=== INSUMOS CON STOCK BAJO MÍNIMO ===\n");
        for (String[] i : lista) {
            sb.append("ID:").append(i[0])
              .append(" | ").append(i[1])
              .append(" | Stock:").append(i[2])
              .append(" | Mínimo:").append(i[3])
              .append(" ").append(i[4]).append("\n");
        }
        return sb.toString();
    }
}
