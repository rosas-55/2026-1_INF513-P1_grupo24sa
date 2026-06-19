package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DReceta {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DReceta() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE RECETAS
    // Atributos: descripcion, id, producto_id (FK), tiempo_preparacion
    // -----------------------------------------------------------

    /**
     * Crear nueva receta
     */
    public int save(String descripcion, int productoId, int tiempoPreparacion) {
        String query = "INSERT INTO RECETA (descripcion, producto_id, tiempo_preparacion) VALUES (?, ?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, descripcion);
            ps.setInt(2, productoId);
            ps.setInt(3, tiempoPreparacion);

            int result = ps.executeUpdate();
            int generatedId = -1;
            if (result > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
                rs.close();
            }
            ps.close();
            return generatedId;
        } catch (SQLException e) {
            System.err.println("Error al guardar receta: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Actualizar receta existente
     */
    public String update(int id, String descripcion, int productoId, int tiempoPreparacion) {
        String query = "UPDATE RECETA SET descripcion = ?, producto_id = ?, tiempo_preparacion = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, descripcion);
            ps.setInt(2, productoId);
            ps.setInt(3, tiempoPreparacion);
            ps.setInt(4, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Receta actualizada exitosamente" : "Error: No se pudo actualizar la receta";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Eliminar receta por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM RECETA WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Receta eliminada exitosamente" : "Error: No se pudo eliminar la receta";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Listar todas las recetas
     */
    public List<String[]> findAll() {
        String query = "SELECT id, descripcion, producto_id, tiempo_preparacion FROM RECETA ORDER BY id";
        List<String[]> recetas = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] receta = new String[4];
                receta[0] = String.valueOf(rs.getInt("id"));
                receta[1] = rs.getString("descripcion");
                receta[2] = String.valueOf(rs.getInt("producto_id"));
                receta[3] = String.valueOf(rs.getInt("tiempo_preparacion"));
                recetas.add(receta);
            }

            rs.close();
            ps.close();
            System.out.println("Total recetas: " + recetas.size());
        } catch (SQLException e) {
            System.err.println("Error en DReceta: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return recetas;
    }

    /**
     * Buscar receta por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, descripcion, producto_id, tiempo_preparacion FROM RECETA WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] receta = new String[4];
                receta[0] = String.valueOf(rs.getInt("id"));
                receta[1] = rs.getString("descripcion");
                receta[2] = String.valueOf(rs.getInt("producto_id"));
                receta[3] = String.valueOf(rs.getInt("tiempo_preparacion"));

                rs.close();
                ps.close();
                return receta;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DReceta: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Buscar recetas por producto
     */
    public List<String[]> findByProducto(int productoId) {
        String query = "SELECT id, descripcion, producto_id, tiempo_preparacion FROM RECETA WHERE producto_id = ?";
        List<String[]> recetas = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, productoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] receta = new String[4];
                receta[0] = String.valueOf(rs.getInt("id"));
                receta[1] = rs.getString("descripcion");
                receta[2] = String.valueOf(rs.getInt("producto_id"));
                receta[3] = String.valueOf(rs.getInt("tiempo_preparacion"));
                recetas.add(receta);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error en DReceta: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return recetas;
    }
}

