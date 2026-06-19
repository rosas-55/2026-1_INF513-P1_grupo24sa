package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DRole {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DRole() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE ROLES
    // Atributos: descripcion, id, nombre
    // -----------------------------------------------------------

    /**
     * Crear nuevo rol
     */
    public String save(String descripcion, String nombre) {
        String query = "INSERT INTO ROLE (descripcion, nombre) VALUES (?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, descripcion);
            ps.setString(2, nombre);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Rol creado exitosamente" : "Error: No se pudo crear el rol";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Actualizar rol existente
     */
    public String update(int id, String descripcion, String nombre) {
        String query = "UPDATE ROLE SET descripcion = ?, nombre = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, descripcion);
            ps.setString(2, nombre);
            ps.setInt(3, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Rol actualizado exitosamente" : "Error: No se pudo actualizar el rol";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Eliminar rol por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM ROLE WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Rol eliminado exitosamente" : "Error: No se pudo eliminar el rol";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Listar todos los roles
     */
    public List<String[]> findAll() {
        String query = "SELECT id, descripcion, nombre FROM ROLE ORDER BY nombre";
        List<String[]> roles = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] role = new String[3];
                role[0] = String.valueOf(rs.getInt("id"));
                role[1] = rs.getString("descripcion");
                role[2] = rs.getString("nombre");
                roles.add(role);
            }

            rs.close();
            ps.close();
            System.out.println("Total roles: " + roles.size());
        } catch (SQLException e) {
            System.err.println("Error en DRole: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return roles;
    }

    /**
     * Buscar rol por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, descripcion, nombre FROM ROLE WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] role = new String[3];
                role[0] = String.valueOf(rs.getInt("id"));
                role[1] = rs.getString("descripcion");
                role[2] = rs.getString("nombre");

                rs.close();
                ps.close();
                return role;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DRole: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Buscar rol por nombre
     */
    public String[] findByNombre(String nombre) {
        String query = "SELECT id, descripcion, nombre FROM ROLE WHERE UPPER(nombre) = UPPER(?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] role = new String[3];
                role[0] = String.valueOf(rs.getInt("id"));
                role[1] = rs.getString("descripcion");
                role[2] = rs.getString("nombre");

                rs.close();
                ps.close();
                return role;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DRole: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
    }
}

