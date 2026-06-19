package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DRecetaInsumo {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DRecetaInsumo() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE RECETA-INSUMO (tabla intermedia)
    // Atributos: cantidad, insumo_id (FK), receta_id (FK)
    // -----------------------------------------------------------

    /**
     * Asociar insumo a receta con cantidad
     */
    public String save(double cantidad, int insumoId, int recetaId) {
        String query = "INSERT INTO RECETA_INSUMO (cantidad, insumo_id, receta_id) VALUES (?, ?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, cantidad);
            ps.setInt(2, insumoId);
            ps.setInt(3, recetaId);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Insumo asociado a receta exitosamente" : "Error: No se pudo asociar el insumo";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Actualizar cantidad de un insumo en una receta
     */
    public String update(int recetaId, int insumoId, double cantidad) {
        String query = "UPDATE RECETA_INSUMO SET cantidad = ? WHERE receta_id = ? AND insumo_id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, cantidad);
            ps.setInt(2, recetaId);
            ps.setInt(3, insumoId);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "RecetaInsumo actualizado exitosamente" : "Error: No se pudo actualizar";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Eliminar insumo de una receta
     */
    public String delete(int recetaId, int insumoId) {
        String query = "DELETE FROM RECETA_INSUMO WHERE receta_id = ? AND insumo_id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, recetaId);
            ps.setInt(2, insumoId);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Insumo removido de receta exitosamente" : "Error: No se pudo remover el insumo";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Listar insumos de una receta
     */
    public List<String[]> findByReceta(int recetaId) {
        String query = "SELECT ri.cantidad, ri.insumo_id, ri.receta_id, i.nombre " +
                "FROM RECETA_INSUMO ri JOIN INSUMO i ON ri.insumo_id = i.id WHERE ri.receta_id = ?";
        List<String[]> items = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, recetaId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] item = new String[4];
                item[0] = String.valueOf(rs.getDouble("cantidad"));
                item[1] = String.valueOf(rs.getInt("insumo_id"));
                item[2] = String.valueOf(rs.getInt("receta_id"));
                item[3] = rs.getString("nombre");
                items.add(item);
            }

            rs.close();
            ps.close();
            System.out.println("Insumos de receta " + recetaId + ": " + items.size());
        } catch (SQLException e) {
            System.err.println("Error en DRecetaInsumo: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return items;
    }
}

