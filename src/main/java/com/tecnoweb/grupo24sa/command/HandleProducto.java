package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BProducto;

import java.util.List;

/**
 * Handler para comandos de la entidad 'producto'
 */
public class HandleProducto {

    public static String execute(String command, String params) {
        BProducto bProducto = new BProducto();
        try {
            switch (command) {
                case "registrar":   return registrar(bProducto, params);
                case "actualizar":  return actualizar(bProducto, params);
                case "eliminar":    return eliminar(bProducto, params);
                case "listar":      return listar(bProducto);
                case "buscar":      return buscar(bProducto, params);
                default:            return "Comando no implementado: " + command;
            }
        } catch (NumberFormatException e) {
            return "Error: Parámetro numérico inválido - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /** registrar(estado, nombre, precio_venta, insumo_id) */
    private static String registrar(BProducto b, String params) {
        String[] p = params.split(",");
        if (p.length < 4) return "Error: Uso: registrar(estado, nombre, precio_venta, insumo_id)";
        return b.registrarProducto(p[0].trim(), p[1].trim(), Double.parseDouble(p[2].trim()), Integer.parseInt(p[3].trim()));
    }

    /** actualizar(id, estado, nombre, precio_venta, insumo_id) */
    private static String actualizar(BProducto b, String params) {
        String[] p = params.split(",");
        if (p.length < 5) return "Error: Uso: actualizar(id, estado, nombre, precio_venta, insumo_id)";
        return b.actualizarProducto(Integer.parseInt(p[0].trim()), p[1].trim(),
                p[2].trim(), Double.parseDouble(p[3].trim()), Integer.parseInt(p[4].trim()));
    }

    /** eliminar(id) */
    private static String eliminar(BProducto b, String params) {
        return b.eliminarProducto(Integer.parseInt(params.trim()));
    }

    private static String listar(BProducto b) {
        List<String[]> lista = b.listarProductos();
        if (lista.isEmpty()) return "No hay productos registrados";
        StringBuilder sb = new StringBuilder("=== PRODUCTOS ===\n");
        for (String[] p : lista) {
            sb.append("ID:").append(p[0])
              .append(" | Estado:").append(p[1])
              .append(" | Nombre:").append(p[2])
              .append(" | Precio:").append(p[3])
              .append(" | Stock:").append(p[4])
              .append(" | Insumo:").append(p[5])
              .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BProducto b, String params) {
        String[] p = b.buscarPorId(Integer.parseInt(params.trim()));
        if (p == null) return "Producto no encontrado";
        return "ID: " + p[0] + "\nNombre: " + p[2] + "\nEstado: " + p[1] + "\nPrecio: " + p[3] + "\nStock: " + p[4] + "\nInsumoID: " + p[5];
    }
}
