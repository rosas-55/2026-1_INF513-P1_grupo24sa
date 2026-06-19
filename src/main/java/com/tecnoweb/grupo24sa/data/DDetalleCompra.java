package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DDetalleCompra {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DDetalleCompra() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE DETALLE DE COMPRA
    // Atributos: cantidad, compra_id, insumo_id, precio_unitario, subtotal
    // -----------------------------------------------------------

    /**
     * Crear nuevo detalle de compra
     */
    public String save(int cantidad, int compraId, int insumoId, double precioUnitario, double subtotal) {
        String query = "INSERT INTO DETALLE_COMPRA (cantidad, compra_id, insumo_id, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, cantidad);
            ps.setInt(2, compraId);
            ps.setInt(3, insumoId);
            ps.setDouble(4, precioUnitario);
            ps.setDouble(5, subtotal);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Detalle de compra creado exitosamente" : "Error: No se pudo crear el detalle de compra";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Actualizar detalle de compra existente
     */
    public String update(int id, int cantidad, double precioUnitario, double subtotal) {
        String query = "UPDATE DETALLE_COMPRA SET cantidad = ?, precio_unitario = ?, subtotal = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, cantidad);
            ps.setDouble(2, precioUnitario);
            ps.setDouble(3, subtotal);
            ps.setInt(4, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Detalle actualizado exitosamente" : "Error: No se pudo actualizar el detalle";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Eliminar detalle por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM DETALLE_COMPRA WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Detalle eliminado exitosamente" : "Error: No se pudo eliminar el detalle";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Listar detalles por compra
     */
    public List<String[]> findByCompra(int compraId) {
        String query = "SELECT id, cantidad, compra_id, insumo_id, precio_unitario, subtotal FROM DETALLE_COMPRA WHERE compra_id = ?";
        List<String[]> detalles = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, compraId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] detalle = new String[6];
                detalle[0] = String.valueOf(rs.getInt("id"));
                detalle[1] = String.valueOf(rs.getInt("cantidad"));
                detalle[2] = String.valueOf(rs.getInt("compra_id"));
                detalle[3] = String.valueOf(rs.getInt("insumo_id"));
                detalle[4] = String.valueOf(rs.getDouble("precio_unitario"));
                detalle[5] = String.valueOf(rs.getDouble("subtotal"));
                detalles.add(detalle);
            }

            rs.close();
            ps.close();
            System.out.println("Total detalles de compra " + compraId + ": " + detalles.size());
        } catch (SQLException e) {
            System.err.println("Error en DDetalleCompra: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return detalles;
    }

    /**
     * Buscar detalle por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, cantidad, compra_id, insumo_id, precio_unitario, subtotal FROM DETALLE_COMPRA WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] detalle = new String[6];
                detalle[0] = String.valueOf(rs.getInt("id"));
                detalle[1] = String.valueOf(rs.getInt("cantidad"));
                detalle[2] = String.valueOf(rs.getInt("compra_id"));
                detalle[3] = String.valueOf(rs.getInt("insumo_id"));
                detalle[4] = String.valueOf(rs.getDouble("precio_unitario"));
                detalle[5] = String.valueOf(rs.getDouble("subtotal"));

                rs.close();
                ps.close();
                return detalle;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DDetalleCompra: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
    }
}

