package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DRoleUsers {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DRoleUsers() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE ROLE-USERS (tabla intermedia)
    // Atributos: role_id (FK), user_id (FK)
    // -----------------------------------------------------------

    /**
     * Asignar rol a usuario
     */
    public String save(int roleId, int userId) {
        String query = "INSERT INTO ROLE_USERS (role_id, user_id) VALUES (?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, roleId);
            ps.setInt(2, userId);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Rol asignado a usuario exitosamente" : "Error: No se pudo asignar el rol";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Revocar rol de usuario
     */
    public String delete(int roleId, int userId) {
        String query = "DELETE FROM ROLE_USERS WHERE role_id = ? AND user_id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, roleId);
            ps.setInt(2, userId);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Rol revocado exitosamente" : "Error: No se pudo revocar el rol";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Listar roles de un usuario
     */
    public List<String[]> findByUser(int userId) {
        String query = "SELECT ru.role_id, ru.user_id, r.nombre FROM ROLE_USERS ru JOIN ROLE r ON ru.role_id = r.id WHERE ru.user_id = ?";
        List<String[]> items = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] item = new String[3];
                item[0] = String.valueOf(rs.getInt("role_id"));
                item[1] = String.valueOf(rs.getInt("user_id"));
                item[2] = rs.getString("nombre");
                items.add(item);
            }

            rs.close();
            ps.close();
            System.out.println("Roles del usuario " + userId + ": " + items.size());
        } catch (SQLException e) {
            System.err.println("Error en DRoleUsers: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return items;
    }

    /**
     * Listar usuarios con un rol especÃ­fico
     */
    public List<String[]> findByRole(int roleId) {
        String query = "SELECT ru.role_id, ru.user_id FROM ROLE_USERS ru WHERE ru.role_id = ?";
        List<String[]> items = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] item = new String[2];
                item[0] = String.valueOf(rs.getInt("role_id"));
                item[1] = String.valueOf(rs.getInt("user_id"));
                items.add(item);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error en DRoleUsers: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return items;
    }
}

