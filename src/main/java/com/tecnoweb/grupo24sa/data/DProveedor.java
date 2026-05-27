package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DProveedor {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DProveedor() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÓN DE PROVEEDORES
    // Atributos: direccion, id, nombre, telefono
    // -----------------------------------------------------------

    /**
     * Crear nuevo proveedor
     */
    public String save(String direccion, String nombre, String telefono) {
        String query = "INSERT INTO PROVEEDOR (direccion, nombre, telefono) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setString(1, direccion);
            ps.setString(2, nombre);
            ps.setString(3, telefono);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Proveedor creado exitosamente" : "Error: No se pudo crear el proveedor";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Actualizar proveedor existente
     */
    public String update(int id, String direccion, String nombre, String telefono) {
        String query = "UPDATE PROVEEDOR SET direccion = ?, nombre = ?, telefono = ? WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setString(1, direccion);
            ps.setString(2, nombre);
            ps.setString(3, telefono);
            ps.setInt(4, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Proveedor actualizado exitosamente" : "Error: No se pudo actualizar el proveedor";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Eliminar proveedor por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM PROVEEDOR WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Proveedor eliminado exitosamente" : "Error: No se pudo eliminar el proveedor";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Listar todos los proveedores
     */
    public List<String[]> findAll() {
        String query = "SELECT id, direccion, nombre, telefono FROM PROVEEDOR ORDER BY nombre";
        List<String[]> proveedores = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] proveedor = new String[4];
                proveedor[0] = String.valueOf(rs.getInt("id"));
                proveedor[1] = rs.getString("direccion");
                proveedor[2] = rs.getString("nombre");
                proveedor[3] = rs.getString("telefono");
                proveedores.add(proveedor);
            }

            rs.close();
            ps.close();
            System.out.println("Total proveedores: " + proveedores.size());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return proveedores;
    }

    /**
     * Buscar proveedor por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, direccion, nombre, telefono FROM PROVEEDOR WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] proveedor = new String[4];
                proveedor[0] = String.valueOf(rs.getInt("id"));
                proveedor[1] = rs.getString("direccion");
                proveedor[2] = rs.getString("nombre");
                proveedor[3] = rs.getString("telefono");

                rs.close();
                ps.close();
                return proveedor;
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
