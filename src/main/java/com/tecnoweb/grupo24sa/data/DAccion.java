package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAccion {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DAccion() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE ACCIONES
    // Atributos: codigo (int), descripcion (int), estado (int),
    //            name (int)
    // Nota: Los tipos "int" del diagrama se interpretan segÃºn contexto
    // -----------------------------------------------------------

    /**
     * Crear nueva acciÃ³n
     */
    public String save(int codigo, String descripcion, int estado, String name) {
        String query = "INSERT INTO ACCION (codigo, descripcion, estado, name) VALUES (?, ?, ?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, codigo);
            ps.setString(2, descripcion);
            ps.setInt(3, estado);
            ps.setString(4, name);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "AcciÃ³n creada exitosamente" : "Error: No se pudo crear la acciÃ³n";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Actualizar acciÃ³n existente
     */
    public String update(int id, int codigo, String descripcion, int estado, String name) {
        String query = "UPDATE ACCION SET codigo = ?, descripcion = ?, estado = ?, name = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, codigo);
            ps.setString(2, descripcion);
            ps.setInt(3, estado);
            ps.setString(4, name);
            ps.setInt(5, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "AcciÃ³n actualizada exitosamente" : "Error: No se pudo actualizar la acciÃ³n";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Eliminar acciÃ³n por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM ACCION WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "AcciÃ³n eliminada exitosamente" : "Error: No se pudo eliminar la acciÃ³n";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Listar todas las acciones
     */
    public List<String[]> findAll() {
        String query = "SELECT id, codigo, descripcion, estado, name FROM ACCION ORDER BY name";
        List<String[]> acciones = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] accion = new String[5];
                accion[0] = String.valueOf(rs.getInt("id"));
                accion[1] = String.valueOf(rs.getInt("codigo"));
                accion[2] = rs.getString("descripcion");
                accion[3] = String.valueOf(rs.getInt("estado"));
                accion[4] = rs.getString("name");
                acciones.add(accion);
            }

            rs.close();
            ps.close();
            System.out.println("Total acciones: " + acciones.size());
        } catch (SQLException e) {
            System.err.println("Error en DAccion: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return acciones;
    }

    /**
     * Buscar acciÃ³n por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, codigo, descripcion, estado, name FROM ACCION WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] accion = new String[5];
                accion[0] = String.valueOf(rs.getInt("id"));
                accion[1] = String.valueOf(rs.getInt("codigo"));
                accion[2] = rs.getString("descripcion");
                accion[3] = String.valueOf(rs.getInt("estado"));
                accion[4] = rs.getString("name");

                rs.close();
                ps.close();
                return accion;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DAccion: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
    }
}

