package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DProduccion {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DProduccion() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE PRODUCCIÃ“N
    // Atributos: cantidad_producida, fecha, id, receta_id (FK)
    // -----------------------------------------------------------

    /**
     * Registrar nueva producciÃ³n
     */
    public String save(double cantidadProducida, String fecha, int recetaId) {
        String query = "INSERT INTO PRODUCCION (cantidad_producida, fecha, receta_id) VALUES (?, ?::date, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, cantidadProducida);
            ps.setString(2, fecha);
            ps.setInt(3, recetaId);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "ProducciÃ³n registrada exitosamente" : "Error: No se pudo registrar la producciÃ³n";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Actualizar producciÃ³n existente
     */
    public String update(int id, double cantidadProducida, String fecha, int recetaId) {
        String query = "UPDATE PRODUCCION SET cantidad_producida = ?, fecha = ?, receta_id = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, cantidadProducida);
            ps.setString(2, fecha);
            ps.setInt(3, recetaId);
            ps.setInt(4, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "ProducciÃ³n actualizada exitosamente" : "Error: No se pudo actualizar la producciÃ³n";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Eliminar producciÃ³n por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM PRODUCCION WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "ProducciÃ³n eliminada exitosamente" : "Error: No se pudo eliminar la producciÃ³n";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Listar toda la producciÃ³n
     */
    public List<String[]> findAll() {
        String query = "SELECT id, cantidad_producida, fecha, receta_id FROM PRODUCCION ORDER BY fecha DESC";
        List<String[]> producciones = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] produccion = new String[4];
                produccion[0] = String.valueOf(rs.getInt("id"));
                produccion[1] = String.valueOf(rs.getDouble("cantidad_producida"));
                produccion[2] = rs.getString("fecha");
                produccion[3] = String.valueOf(rs.getInt("receta_id"));
                producciones.add(produccion);
            }

            rs.close();
            ps.close();
            System.out.println("Total registros de producciÃ³n: " + producciones.size());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return producciones;
    }

    /**
     * Buscar producciÃ³n por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, cantidad_producida, fecha, receta_id FROM PRODUCCION WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] produccion = new String[4];
                produccion[0] = String.valueOf(rs.getInt("id"));
                produccion[1] = String.valueOf(rs.getDouble("cantidad_producida"));
                produccion[2] = rs.getString("fecha");
                produccion[3] = String.valueOf(rs.getInt("receta_id"));

                rs.close();
                ps.close();
                return produccion;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Listar producciÃ³n por receta
     */
    public List<String[]> findByReceta(int recetaId) {
        String query = "SELECT id, cantidad_producida, fecha, receta_id FROM PRODUCCION WHERE receta_id = ? ORDER BY fecha DESC";
        List<String[]> producciones = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, recetaId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] produccion = new String[4];
                produccion[0] = String.valueOf(rs.getInt("id"));
                produccion[1] = String.valueOf(rs.getDouble("cantidad_producida"));
                produccion[2] = rs.getString("fecha");
                produccion[3] = String.valueOf(rs.getInt("receta_id"));
                producciones.add(produccion);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return producciones;
    }
}

