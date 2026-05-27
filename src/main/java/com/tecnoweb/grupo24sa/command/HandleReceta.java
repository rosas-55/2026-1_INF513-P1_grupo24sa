package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BReceta;

import java.util.List;
import java.util.ArrayList;

/**
 * Handler para comandos de la entidad 'receta'
 */
public class HandleReceta {

    public static String execute(String command, String params) {
        BReceta bReceta = new BReceta();
        try {
            switch (command) {
                case "registrar":        return registrar(bReceta, params);
                case "actualizar":       return actualizar(bReceta, params);
                case "eliminar":         return eliminar(bReceta, params);
                case "listar":           return listar(bReceta);
                case "buscar":           return buscar(bReceta, params);
                case "listarPorProducto": return listarPorProducto(bReceta, params);
                default:                 return "Comando no implementado: " + command;
            }
        } catch (NumberFormatException e) {
            return "Error: Parámetro numérico inválido - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /** registrar(descripcion, producto_id, tiempo_preparacion, [insumo_id1;cantidad1], ...) */
    private static String registrar(BReceta b, String params) {
        String[] p = params.split(",");
        if (p.length < 3) return "Error: Uso: registrar(descripcion,producto_id,tiempo_preparacion,[insumo_id1;cantidad1],...)";
        
        String descripcion = p[0].trim();
        int productoId = Integer.parseInt(p[1].trim());
        int tiempoPreparacion = Integer.parseInt(p[2].trim());
        
        List<String[]> ingredients = new ArrayList<>();
        for (int i = 3; i < p.length; i++) {
            String itemStr = p[i].trim();
            if (itemStr.startsWith("[") && itemStr.endsWith("]")) {
                itemStr = itemStr.substring(1, itemStr.length() - 1);
            }
            String[] itemParts = itemStr.split(";");
            if (itemParts.length >= 2) {
                ingredients.add(new String[]{itemParts[0].trim(), itemParts[1].trim()});
            }
        }
        
        return b.registrarReceta(descripcion, productoId, tiempoPreparacion, ingredients);
    }

    /** actualizar(id, descripcion, producto_id, tiempo_preparacion) */
    private static String actualizar(BReceta b, String params) {
        String[] p = params.split(",");
        if (p.length < 4) return "Error: Uso: actualizar(id,descripcion,producto_id,tiempo_preparacion)";
        return b.actualizarReceta(Integer.parseInt(p[0].trim()), p[1].trim(),
                Integer.parseInt(p[2].trim()), Integer.parseInt(p[3].trim()));
    }

    /** eliminar(id) */
    private static String eliminar(BReceta b, String params) {
        return b.eliminarReceta(Integer.parseInt(params.trim()));
    }

    private static String listar(BReceta b) {
        List<String[]> lista = b.listarRecetas();
        if (lista.isEmpty()) return "No hay recetas registradas";
        StringBuilder sb = new StringBuilder("=== RECETAS ===\n");
        for (String[] r : lista) {
            sb.append("ID:").append(r[0])
              .append(" | ProductoID:").append(r[2])
              .append(" | Tiempo:").append(r[3]).append(" min")
              .append(" | Desc:").append(r[1])
              .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BReceta b, String params) {
        String[] r = b.buscarPorId(Integer.parseInt(params.trim()));
        if (r == null) return "Receta no encontrada";
        return "ID: " + r[0] + "\nDescripción: " + r[1]
                + "\nProductoID: " + r[2] + "\nTiempo preparación: " + r[3] + " min";
    }

    /** listarPorProducto(producto_id) */
    private static String listarPorProducto(BReceta b, String params) {
        int productoId = Integer.parseInt(params.trim());
        List<String[]> lista = b.listarPorProducto(productoId);
        if (lista.isEmpty()) return "No hay recetas para el producto " + productoId;
        StringBuilder sb = new StringBuilder("=== RECETAS DEL PRODUCTO " + productoId + " ===\n");
        for (String[] r : lista) {
            sb.append("ID:").append(r[0])
              .append(" | ").append(r[1])
              .append(" | Tiempo:").append(r[3]).append(" min\n");
        }
        return sb.toString();
    }
}
