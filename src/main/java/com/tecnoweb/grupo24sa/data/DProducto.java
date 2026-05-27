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
    // Atributos: estado, id, nombre, precio_venta
    // -----------------------------------------------------------

    /**
     * Crear nuevo producto
     */
    public String save(String estado, String nombre, double precioVenta) {
        String query = "INSERT INTO PRODUCTO (estado, nombre, precio_venta) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setString(1, estado);
            ps.setString(2, nombre);
            ps.setDouble(3, precioVenta);

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
    public String update(int id, String estado, String nombre, double precioVenta) {
        String query = "UPDATE PRODUCTO SET estado = ?, nombre = ?, precio_venta = ? WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setString(1, estado);
            ps.setString(2, nombre);
            ps.setDouble(3, precioVenta);
            ps.setInt(4, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Producto actualizado exitosamente" : "Error: No se pudo actualizar el producto";
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
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
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
        String query = "SELECT id, estado, nombre, precio_venta FROM PRODUCTO ORDER BY nombre";
        List<String[]> productos = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] producto = new String[4];
                producto[0] = String.valueOf(rs.getInt("id"));
                producto[1] = rs.getString("estado");
                producto[2] = rs.getString("nombre");
                producto[3] = String.valueOf(rs.getDouble("precio_venta"));
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
        String query = "SELECT id, estado, nombre, precio_venta FROM PRODUCTO WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] producto = new String[4];
                producto[0] = String.valueOf(rs.getInt("id"));
                producto[1] = rs.getString("estado");
                producto[2] = rs.getString("nombre");
                producto[3] = String.valueOf(rs.getDouble("precio_venta"));

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
