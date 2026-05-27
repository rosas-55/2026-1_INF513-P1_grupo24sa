package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BRecetaInsumo;

import java.util.List;

/**
 * Handler para comandos de la entidad 'recetainsumo'
 */
public class HandleRecetaInsumo {

    public static String execute(String command, String params) {
        BRecetaInsumo bRecetaInsumo = new BRecetaInsumo();
        try {
            switch (command) {
                case "registrar":    return registrar(bRecetaInsumo, params);
                case "actualizar":   return actualizar(bRecetaInsumo, params);
                case "eliminar":     return eliminar(bRecetaInsumo, params);
                case "listarPorReceta": return listarPorReceta(bRecetaInsumo, params);
                default:             return "Comando no implementado: " + command;
            }
        } catch (NumberFormatException e) {
            return "Error: Parámetro numérico inválido - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /** registrar(cantidad, insumo_id, receta_id) */
    private static String registrar(BRecetaInsumo b, String params) {
        String[] p = params.split(",");
        if (p.length < 3) return "Error: Uso: registrar(cantidad,insumo_id,receta_id)";
        return b.registrarIngrediente(Double.parseDouble(p[0].trim()),
                Integer.parseInt(p[1].trim()), Integer.parseInt(p[2].trim()));
    }

    /** actualizar(receta_id, insumo_id, cantidad) */
    private static String actualizar(BRecetaInsumo b, String params) {
        String[] p = params.split(",");
        if (p.length < 3) return "Error: Uso: actualizar(receta_id,insumo_id,cantidad)";
        return b.actualizarIngrediente(Integer.parseInt(p[0].trim()),
                Integer.parseInt(p[1].trim()), Double.parseDouble(p[2].trim()));
    }

    /** eliminar(receta_id, insumo_id) */
    private static String eliminar(BRecetaInsumo b, String params) {
        String[] p = params.split(",");
        if (p.length < 2) return "Error: Uso: eliminar(receta_id,insumo_id)";
        return b.eliminarIngrediente(Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()));
    }

    /** listarPorReceta(receta_id) */
    private static String listarPorReceta(BRecetaInsumo b, String params) {
        int recetaId = Integer.parseInt(params.trim());
        List<String[]> lista = b.listarPorReceta(recetaId);
        if (lista.isEmpty()) return "No hay insumos en la receta " + recetaId;
        StringBuilder sb = new StringBuilder("=== INGREDIENTES DE RECETA " + recetaId + " ===\n");
        for (String[] i : lista) {
            // item[0]=cantidad, item[1]=insumo_id, item[2]=receta_id, item[3]=nombre
            sb.append("InsumoID:").append(i[1])
              .append(" | Nombre:").append(i[3])
              .append(" | Cantidad:").append(i[0])
              .append("\n");
        }
        return sb.toString();
    }
}
