package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BProveedor;

import java.util.List;

/**
 * Handler para comandos de la entidad 'proveedor'
 */
public class HandleProveedor {

    public static String execute(String command, String params) {
        BProveedor bProveedor = new BProveedor();
        try {
            switch (command) {
                case "registrar":  return registrar(bProveedor, params);
                case "actualizar": return actualizar(bProveedor, params);
                case "eliminar":   return eliminar(bProveedor, params);
                case "listar":     return listar(bProveedor);
                case "buscar":     return buscar(bProveedor, params);
                default:           return "Comando no implementado: " + command;
            }
        } catch (NumberFormatException e) {
            return "Error: Parámetro numérico inválido - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /** registrar(direccion, nombre, telefono) */
    private static String registrar(BProveedor b, String params) {
        String[] p = params.split(",");
        if (p.length < 3) return "Error: Uso: registrar(direccion, nombre, telefono)";
        return b.registrarProveedor(p[0].trim(), p[1].trim(), p[2].trim());
    }

    /** actualizar(id, direccion, nombre, telefono) */
    private static String actualizar(BProveedor b, String params) {
        String[] p = params.split(",");
        if (p.length < 4) return "Error: Uso: actualizar(id, direccion, nombre, telefono)";
        return b.actualizarProveedor(Integer.parseInt(p[0].trim()),
                p[1].trim(), p[2].trim(), p[3].trim());
    }

    /** eliminar(id) */
    private static String eliminar(BProveedor b, String params) {
        return b.eliminarProveedor(Integer.parseInt(params.trim()));
    }

    private static String listar(BProveedor b) {
        List<String[]> lista = b.listarProveedores();
        if (lista.isEmpty()) return "No hay proveedores registrados";
        StringBuilder sb = new StringBuilder("=== PROVEEDORES ===\n");
        for (String[] p : lista) {
            sb.append("ID:").append(p[0])
              .append(" | Nombre:").append(p[2])
              .append(" | Dirección:").append(p[1])
              .append(" | Tel:").append(p[3])
              .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BProveedor b, String params) {
        String[] p = b.buscarPorId(Integer.parseInt(params.trim()));
        if (p == null) return "Proveedor no encontrado";
        return "ID: " + p[0] + "\nNombre: " + p[2] + "\nDirección: " + p[1] + "\nTeléfono: " + p[3];
    }
}
