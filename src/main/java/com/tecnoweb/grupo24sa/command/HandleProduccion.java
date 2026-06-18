package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BProduccion;

import java.time.LocalDate;
import java.util.List;

/**
 * Handler para comandos de la entidad 'produccion'
 */
public class HandleProduccion {

    public static String execute(String command, String params) {
        BProduccion bProduccion = new BProduccion();
        try {
            switch (command) {
                case "registrar":       return registrar(bProduccion, params);
                case "actualizar":      return actualizar(bProduccion, params);
                case "eliminar":        return eliminar(bProduccion, params);
                case "listar":          return listar(bProduccion);
                case "buscar":          return buscar(bProduccion, params);
                case "listarPorReceta": return listarPorReceta(bProduccion, params);
                default:                return "Comando no implementado: " + command;
            }
        } catch (NumberFormatException e) {
            return "Error: Parámetro numérico inválido - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /** registrar(cantidad_producida, receta_id) — fecha se asigna automáticamente del servidor */
    private static String registrar(BProduccion b, String params) {
        String[] p = params.split(",");
        if (p.length < 2) return "Error: Uso: registrar(cantidad_producida,receta_id)";
        String fecha = LocalDate.now().toString();
        return b.registrarProduccion(Double.parseDouble(p[0].trim()),
                fecha, Integer.parseInt(p[1].trim()));
    }

    /** actualizar(id, cantidad_producida, fecha, receta_id) */
    private static String actualizar(BProduccion b, String params) {
        String[] p = params.split(",");
        if (p.length < 4) return "Error: Uso: actualizar(id,cantidad_producida,fecha,receta_id)";
        String fecha = p[2].trim().isEmpty() ? LocalDate.now().toString() : p[2].trim();
        return b.actualizarProduccion(Integer.parseInt(p[0].trim()),
                Double.parseDouble(p[1].trim()), fecha, Integer.parseInt(p[3].trim()));
    }

    /** eliminar(id) */
    private static String eliminar(BProduccion b, String params) {
        return b.eliminarProduccion(Integer.parseInt(params.trim()));
    }

    private static String listar(BProduccion b) {
        List<String[]> lista = b.listarProducciones();
        if (lista.isEmpty()) return "No hay registros de producción";
        StringBuilder sb = new StringBuilder("=== PRODUCCIÓN ===\n");
        for (String[] p : lista) {
            // p[0]=id, p[1]=cantidad_producida, p[2]=fecha, p[3]=receta_id
            sb.append("ID:").append(p[0])
              .append(" | RecetaID:").append(p[3])
              .append(" | Cant:").append(p[1])
              .append(" | Fecha:").append(p[2])
              .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BProduccion b, String params) {
        String[] p = b.buscarPorId(Integer.parseInt(params.trim()));
        if (p == null) return "Producción no encontrada";
        return "ID: " + p[0] + "\nRecetaID: " + p[3]
                + "\nCantidad producida: " + p[1] + "\nFecha: " + p[2];
    }

    /** listarPorReceta(receta_id) */
    private static String listarPorReceta(BProduccion b, String params) {
        int recetaId = Integer.parseInt(params.trim());
        List<String[]> lista = b.listarPorReceta(recetaId);
        if (lista.isEmpty()) return "No hay producciones para la receta " + recetaId;
        StringBuilder sb = new StringBuilder("=== PRODUCCIONES DE RECETA " + recetaId + " ===\n");
        double total = 0;
        for (String[] p : lista) {
            total += Double.parseDouble(p[1]);
            sb.append("ID:").append(p[0])
              .append(" | Cant:").append(p[1])
              .append(" | Fecha:").append(p[2]).append("\n");
        }
        sb.append("Total producido: ").append(String.format("%.2f", total));
        return sb.toString();
    }
}
