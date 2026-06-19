package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DDetalleVenta {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DDetalleVenta() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE DETALLE DE VENTA
    // Atributos: cantidad, precio_unitario, producto_id, sub_total, venta_id
    // -----------------------------------------------------------

    /**
     * Crear nuevo detalle de venta
     */
    public String save(int cantidad, double precioUnitario, int productoId, double subTotal, int ventaId) {
        String query = "INSERT INTO DETALLE_VENTA (cantidad, precio_unitario, producto_id, sub_total, venta_id) VALUES (?, ?, ?, ?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, cantidad);
            ps.setDouble(2, precioUnitario);
            ps.setInt(3, productoId);
            ps.setDouble(4, subTotal);
            ps.setInt(5, ventaId);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Detalle de venta creado exitosamente" : "Error: No se pudo crear el detalle de venta";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Actualizar detalle de venta existente
     */
    public String update(int id, int cantidad, double precioUnitario, int productoId, double subTotal) {
        String query = "UPDATE DETALLE_VENTA SET cantidad = ?, precio_unitario = ?, producto_id = ?, sub_total = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, cantidad);
            ps.setDouble(2, precioUnitario);
            ps.setInt(3, productoId);
            ps.setDouble(4, subTotal);
            ps.setInt(5, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Detalle actualizado exitosamente" : "Error: No se pudo actualizar el detalle";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Eliminar detalle por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM DETALLE_VENTA WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Detalle eliminado exitosamente" : "Error: No se pudo eliminar el detalle";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Listar todos los detalles de una venta
     */
    public List<String[]> findByVenta(int ventaId) {
        String query = "SELECT id, cantidad, precio_unitario, producto_id, sub_total, venta_id FROM DETALLE_VENTA WHERE venta_id = ?";
        List<String[]> detalles = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, ventaId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] detalle = new String[6];
                detalle[0] = String.valueOf(rs.getInt("id"));
                detalle[1] = String.valueOf(rs.getInt("cantidad"));
                detalle[2] = String.valueOf(rs.getDouble("precio_unitario"));
                detalle[3] = String.valueOf(rs.getInt("producto_id"));
                detalle[4] = String.valueOf(rs.getDouble("sub_total"));
                detalle[5] = String.valueOf(rs.getInt("venta_id"));
                detalles.add(detalle);
            }

            rs.close();
            ps.close();
            System.out.println("Total detalles de venta " + ventaId + ": " + detalles.size());
        } catch (SQLException e) {
            System.err.println("Error en DDetalleVenta: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return detalles;
    }

    /**
     * Buscar detalle por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, cantidad, precio_unitario, producto_id, sub_total, venta_id FROM DETALLE_VENTA WHERE id = ?";
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
                detalle[2] = String.valueOf(rs.getDouble("precio_unitario"));
                detalle[3] = String.valueOf(rs.getInt("producto_id"));
                detalle[4] = String.valueOf(rs.getDouble("sub_total"));
                detalle[5] = String.valueOf(rs.getInt("venta_id"));

                rs.close();
                ps.close();
                return detalle;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DDetalleVenta: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
    }
}

