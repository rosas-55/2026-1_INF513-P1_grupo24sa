package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DInsumo {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DInsumo() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE INSUMOS
    // Atributos: costo_unitario, descripcion, estado, id, nombre,
    //            stock_actual, stock_minimo, unidad_medida
    // -----------------------------------------------------------

    /**
     * Crear nuevo insumo
     */
    public String save(double costoUnitario, String descripcion, String estado,
                       String nombre, double stockActual, double stockMinimo, String unidadMedida) {
        String query = "INSERT INTO INSUMO (costo_unitario, descripcion, estado, nombre, stock_actual, stock_minimo, unidad_medida) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, costoUnitario);
            ps.setString(2, descripcion);
            ps.setString(3, estado);
            ps.setString(4, nombre);
            ps.setDouble(5, stockActual);
            ps.setDouble(6, stockMinimo);
            ps.setString(7, unidadMedida);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Insumo creado exitosamente" : "Error: No se pudo crear el insumo";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Actualizar insumo existente
     */
    public String update(int id, double costoUnitario, String descripcion, String estado,
                         String nombre, double stockActual, double stockMinimo, String unidadMedida) {
        String query = "UPDATE INSUMO SET costo_unitario = ?, descripcion = ?, estado = ?, nombre = ?, " +
                "stock_actual = ?, stock_minimo = ?, unidad_medida = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, costoUnitario);
            ps.setString(2, descripcion);
            ps.setString(3, estado);
            ps.setString(4, nombre);
            ps.setDouble(5, stockActual);
            ps.setDouble(6, stockMinimo);
            ps.setString(7, unidadMedida);
            ps.setInt(8, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Insumo actualizado exitosamente" : "Error: No se pudo actualizar el insumo";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Eliminar insumo por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM INSUMO WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Insumo eliminado exitosamente" : "Error: No se pudo eliminar el insumo";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();`n        }
    }

    /**
     * Listar todos los insumos
     */
    public List<String[]> findAll() {
        String query = "SELECT id, costo_unitario, descripcion, estado, nombre, stock_actual, stock_minimo, unidad_medida FROM INSUMO ORDER BY nombre";
        List<String[]> insumos = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] insumo = new String[8];
                insumo[0] = String.valueOf(rs.getInt("id"));
                insumo[1] = String.valueOf(rs.getDouble("costo_unitario"));
                insumo[2] = rs.getString("descripcion");
                insumo[3] = rs.getString("estado");
                insumo[4] = rs.getString("nombre");
                insumo[5] = String.valueOf(rs.getDouble("stock_actual"));
                insumo[6] = String.valueOf(rs.getDouble("stock_minimo"));
                insumo[7] = rs.getString("unidad_medida");
                insumos.add(insumo);
            }

            rs.close();
            ps.close();
            System.out.println("Total insumos: " + insumos.size());
        } catch (SQLException e) {
            System.err.println("Error en DInsumo: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return insumos;
    }

    /**
     * Buscar insumo por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, costo_unitario, descripcion, estado, nombre, stock_actual, stock_minimo, unidad_medida FROM INSUMO WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] insumo = new String[8];
                insumo[0] = String.valueOf(rs.getInt("id"));
                insumo[1] = String.valueOf(rs.getDouble("costo_unitario"));
                insumo[2] = rs.getString("descripcion");
                insumo[3] = rs.getString("estado");
                insumo[4] = rs.getString("nombre");
                insumo[5] = String.valueOf(rs.getDouble("stock_actual"));
                insumo[6] = String.valueOf(rs.getDouble("stock_minimo"));
                insumo[7] = rs.getString("unidad_medida");

                rs.close();
                ps.close();
                return insumo;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DInsumo: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Buscar insumo por nombre (case-insensitive)
     */
    public String[] findOneByName(String nombre) {
        String query = "SELECT id, costo_unitario, descripcion, estado, nombre, stock_actual, stock_minimo, unidad_medida FROM INSUMO WHERE LOWER(nombre) = LOWER(?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, nombre.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] insumo = new String[8];
                insumo[0] = String.valueOf(rs.getInt("id"));
                insumo[1] = String.valueOf(rs.getDouble("costo_unitario"));
                insumo[2] = rs.getString("descripcion");
                insumo[3] = rs.getString("estado");
                insumo[4] = rs.getString("nombre");
                insumo[5] = String.valueOf(rs.getDouble("stock_actual"));
                insumo[6] = String.valueOf(rs.getDouble("stock_minimo"));
                insumo[7] = rs.getString("unidad_medida");

                rs.close();
                ps.close();
                return insumo;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DInsumo: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Listar insumos con stock bajo el mÃ­nimo
     */
    public List<String[]> findStockBajoMinimo() {
        String query = "SELECT id, nombre, stock_actual, stock_minimo, unidad_medida FROM INSUMO WHERE stock_actual < stock_minimo ORDER BY nombre";
        List<String[]> insumos = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] insumo = new String[5];
                insumo[0] = String.valueOf(rs.getInt("id"));
                insumo[1] = rs.getString("nombre");
                insumo[2] = String.valueOf(rs.getDouble("stock_actual"));
                insumo[3] = String.valueOf(rs.getDouble("stock_minimo"));
                insumo[4] = rs.getString("unidad_medida");
                insumos.add(insumo);
            }

            rs.close();
            ps.close();
            System.out.println("Insumos bajo stock mÃ­nimo: " + insumos.size());
        } catch (SQLException e) {
            System.err.println("Error en DInsumo: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return insumos;
    }
}

