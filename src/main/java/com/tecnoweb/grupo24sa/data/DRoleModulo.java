package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DRoleModulo {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DRoleModulo() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE ROLE-MODULO (tabla intermedia)
    // Representa quÃ© mÃ³dulos tiene acceso cada rol
    // Atributos: role_id (FK), modulo_id (FK)
    // -----------------------------------------------------------

    /**
     * Asignar mÃ³dulo a un rol
     */
    public String save(int roleId, int moduloId) {
        String query = "INSERT INTO ROLE_MODULO (role_id, modulo_id) VALUES (?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, roleId);
            ps.setInt(2, moduloId);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "MÃ³dulo asignado al rol exitosamente" : "Error: No se pudo asignar el mÃ³dulo";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Revocar mÃ³dulo de un rol
     */
    public String delete(int roleId, int moduloId) {
        String query = "DELETE FROM ROLE_MODULO WHERE role_id = ? AND modulo_id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, roleId);
            ps.setInt(2, moduloId);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "MÃ³dulo revocado del rol exitosamente" : "Error: No se pudo revocar el mÃ³dulo";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Listar mÃ³dulos asignados a un rol
     */
    public List<String[]> findByRole(int roleId) {
        String query = "SELECT rm.role_id, rm.modulo_id, m.name FROM ROLE_MODULO rm " +
                "JOIN MODULO m ON rm.modulo_id = m.id WHERE rm.role_id = ?";
        List<String[]> items = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] item = new String[3];
                item[0] = String.valueOf(rs.getInt("role_id"));
                item[1] = String.valueOf(rs.getInt("modulo_id"));
                item[2] = rs.getString("name");
                items.add(item);
            }

            rs.close();
            ps.close();
            System.out.println("MÃ³dulos del rol " + roleId + ": " + items.size());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return items;
    }

    /**
     * Listar roles que tienen acceso a un mÃ³dulo
     */
    public List<String[]> findByModulo(int moduloId) {
        String query = "SELECT rm.role_id, rm.modulo_id FROM ROLE_MODULO rm WHERE rm.modulo_id = ?";
        List<String[]> items = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, moduloId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] item = new String[2];
                item[0] = String.valueOf(rs.getInt("role_id"));
                item[1] = String.valueOf(rs.getInt("modulo_id"));
                items.add(item);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return items;
    }
}

