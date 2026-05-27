package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BRole;

import java.util.List;

public class HandleRole {

    public static String execute(String command, String params) {
        BRole bRole = new BRole();

        try {
            switch (command) {
                case "registrar":
                    return registrar(bRole, params);
                case "actualizar":
                    return actualizar(bRole, params);
                case "eliminar":
                    return eliminar(bRole, params);
                case "listar":
                    return listar(bRole);
                case "buscar":
                    return buscar(bRole, params);
                default:
                    return "Comando no implementado: " + command;
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private static String registrar(BRole bRole, String params) {
        String[] parts = params.split(",");
        if (parts.length < 2) {
            return "Error: Faltan parámetros. Uso: registrar (descripcion, nombre)";
        }

        return bRole.registrarRol(parts[0].trim(), parts[1].trim());
    }

    private static String actualizar(BRole bRole, String params) {
        String[] parts = params.split(",");
        if (parts.length < 3) {
            return "Error: Faltan parámetros. Uso: actualizar (id, descripcion, nombre)";
        }

        int id = Integer.parseInt(parts[0].trim());
        return bRole.actualizarRol(id, parts[1].trim(), parts[2].trim());
    }

    private static String eliminar(BRole bRole, String params) {
        int id = Integer.parseInt(params.trim());
        return bRole.eliminarRol(id);
    }

    private static String listar(BRole bRole) {
        List<String[]> roles = bRole.listarRoles();

        if (roles.isEmpty()) {
            return "No hay roles registrados";
        }

        StringBuilder sb = new StringBuilder("=== ROLES REGISTRADOS ===\n");
        for (String[] role : roles) {
            sb.append("ID: ").append(role[0])
              .append(" | Descripcion: ").append(role[1])
              .append(" | Nombre: ").append(role[2])
              .append("\n");
        }
        return sb.toString();
    }

    private static String buscar(BRole bRole, String params) {
        int id = Integer.parseInt(params.trim());
        String[] role = bRole.buscarPorId(id);

        if (role == null) {
            return "Rol no encontrado";
        }

        return "Rol: " + role[2] + "\n" +
                "Descripcion: " + role[1];
    }
}