package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DProducto {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DProducto() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÓN DE PRODUCTOS
    // Atributos: estado, id, nombre, precio_venta, stock_actual, insumo_id
    // -----------------------------------------------------------

    /**
     * Crear nuevo producto
     */
    public String save(String estado, String nombre, double precioVenta, int stockActual, Integer insumoId) {
        String query = "INSERT INTO PRODUCTO (estado, nombre, precio_venta, stock_actual, insumo_id) VALUES (?, ?, ?, ?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, estado);
            ps.setString(2, nombre);
            ps.setDouble(3, precioVenta);
            ps.setInt(4, stockActual);
            if (insumoId == null || insumoId <= 0) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, insumoId);
            }

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Producto creado exitosamente" : "Error: No se pudo crear el producto";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Actualizar producto existente
     */
    public String update(int id, String estado, String nombre, double precioVenta, int stockActual, Integer insumoId) {
        String query = "UPDATE PRODUCTO SET estado = ?, nombre = ?, precio_venta = ?, stock_actual = ?, insumo_id = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, estado);
            ps.setString(2, nombre);
            ps.setDouble(3, precioVenta);
            ps.setInt(4, stockActual);
            if (insumoId == null || insumoId <= 0) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, insumoId);
            }
            ps.setInt(6, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Producto actualizado exitosamente" : "Error: No se pudo actualizar el producto";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Actualizar solo el stock de un producto
     */
    public String updateStock(int id, int nuevoStock) {
        String query = "UPDATE PRODUCTO SET stock_actual = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, nuevoStock);
            ps.setInt(2, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Stock de producto actualizado" : "Error: No se pudo actualizar el stock del producto";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Eliminar producto por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM PRODUCTO WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Producto eliminado exitosamente" : "Error: No se pudo eliminar el producto";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Listar todos los productos
     */
    public List<String[]> findAll() {
        String query = "SELECT id, estado, nombre, precio_venta, stock_actual, insumo_id FROM PRODUCTO ORDER BY nombre";
        List<String[]> productos = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] producto = new String[6];
                producto[0] = String.valueOf(rs.getInt("id"));
                producto[1] = rs.getString("estado");
                producto[2] = rs.getString("nombre");
                producto[3] = String.valueOf(rs.getDouble("precio_venta"));
                producto[4] = String.valueOf(rs.getInt("stock_actual"));
                int insId = rs.getInt("insumo_id");
                producto[5] = rs.wasNull() ? "0" : String.valueOf(insId);
                productos.add(producto);
            }

            rs.close();
            ps.close();
            System.out.println("Total productos: " + productos.size());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return productos;
    }

    /**
     * Buscar producto por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, estado, nombre, precio_venta, stock_actual, insumo_id FROM PRODUCTO WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] producto = new String[6];
                producto[0] = String.valueOf(rs.getInt("id"));
                producto[1] = rs.getString("estado");
                producto[2] = rs.getString("nombre");
                producto[3] = String.valueOf(rs.getDouble("precio_venta"));
                producto[4] = String.valueOf(rs.getInt("stock_actual"));
                int insId = rs.getInt("insumo_id");
                producto[5] = rs.wasNull() ? "0" : String.valueOf(insId);

                rs.close();
                ps.close();
                return producto;
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
     * Buscar producto por insumo_id (Para productos directos)
     */
    public String[] findByInsumoId(int insumoId) {
        String query = "SELECT id, estado, nombre, precio_venta, stock_actual, insumo_id FROM PRODUCTO WHERE insumo_id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, insumoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] producto = new String[6];
                producto[0] = String.valueOf(rs.getInt("id"));
                producto[1] = rs.getString("estado");
                producto[2] = rs.getString("nombre");
                producto[3] = String.valueOf(rs.getDouble("precio_venta"));
                producto[4] = String.valueOf(rs.getInt("stock_actual"));
                int insId = rs.getInt("insumo_id");
                producto[5] = rs.wasNull() ? "0" : String.valueOf(insId);

                rs.close();
                ps.close();
                return producto;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
