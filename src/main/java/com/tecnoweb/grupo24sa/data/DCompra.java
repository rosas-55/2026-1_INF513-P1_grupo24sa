package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DCompra {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DCompra() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE COMPRAS
    // Atributos: estado, fecha, id, proveedor (FK), total
    // -----------------------------------------------------------

    /**
     * Crear nueva compra
     */
    public int save(String estado, String fecha, int proveedorId, double total) {
        String query = "INSERT INTO Compra (estado, fecha, proveedor_id, total) VALUES (?, ?::date, ?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, estado);
            ps.setString(2, fecha);
            ps.setInt(3, proveedorId);
            ps.setDouble(4, total);

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
            System.err.println("Error al guardar compra: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Actualizar estado de una compra
     */
    public String updateEstado(int id, String estado) {
        String query = "UPDATE Compra SET estado = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, estado);
            ps.setInt(2, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Compra actualizada exitosamente" : "Error: No se pudo actualizar la compra";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Eliminar compra por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM Compra WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Compra eliminada exitosamente" : "Error: No se pudo eliminar la compra";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Listar todas las compras
     */
    public List<String[]> findAll() {
        String query = "SELECT id, estado, fecha, proveedor_id, total FROM Compra ORDER BY fecha DESC";
        List<String[]> compras = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] compra = new String[5];
                compra[0] = String.valueOf(rs.getInt("id"));
                compra[1] = rs.getString("estado");
                compra[2] = rs.getString("fecha");
                compra[3] = String.valueOf(rs.getInt("proveedor_id"));
                compra[4] = String.valueOf(rs.getDouble("total"));
                compras.add(compra);
            }

            rs.close();
            ps.close();
            System.out.println("Total compras: " + compras.size());
        } catch (SQLException e) {
            System.err.println("Error en DCompra: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return compras;
    }

    /**
     * Buscar compra por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, estado, fecha, proveedor_id, total FROM Compra WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] compra = new String[5];
                compra[0] = String.valueOf(rs.getInt("id"));
                compra[1] = rs.getString("estado");
                compra[2] = rs.getString("fecha");
                compra[3] = String.valueOf(rs.getInt("proveedor_id"));
                compra[4] = String.valueOf(rs.getDouble("total"));

                rs.close();
                ps.close();
                return compra;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DCompra: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Listar compras por proveedor
     */
    public List<String[]> findByProveedor(int proveedorId) {
        String query = "SELECT id, estado, fecha, proveedor_id, total FROM Compra WHERE proveedor_id = ? ORDER BY fecha DESC";
        List<String[]> compras = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, proveedorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] compra = new String[5];
                compra[0] = String.valueOf(rs.getInt("id"));
                compra[1] = rs.getString("estado");
                compra[2] = rs.getString("fecha");
                compra[3] = String.valueOf(rs.getInt("proveedor_id"));
                compra[4] = String.valueOf(rs.getDouble("total"));
                compras.add(compra);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error en DCompra: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return compras;
    }
}

