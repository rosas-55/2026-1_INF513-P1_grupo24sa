package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DInventario {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DInventario() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÓN DE INVENTARIO
    // Atributos: cantidad, fecha, id, insumo_id (FK),
    //            metodo_inventario, observacion, tipo_movimiento
    // -----------------------------------------------------------

    /**
     * Registrar movimiento de inventario
     */
    public String save(double cantidad, String fecha, int insumoId,
                       String observacion, String tipoMovimiento, double costoUnitario, double valorTotal) {
        String query = "INSERT INTO INVENTARIO (cantidad, fecha, insumo_id, observacion, tipo_movimiento, costo_unitario, valor_total) " +
                "VALUES (?, ?::date, ?, ?, ?, ?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) return "Error: No se pudo obtener la conexion a la base de datos";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, cantidad);
            ps.setString(2, fecha);
            ps.setInt(3, insumoId);
            ps.setString(4, observacion);
            ps.setString(5, tipoMovimiento);
            ps.setDouble(6, costoUnitario);
            ps.setDouble(7, valorTotal);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Movimiento de inventario registrado exitosamente" : "Error: No se pudo registrar el movimiento";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Actualizar registro de inventario
     */
    public String update(int id, double cantidad, String observacion) {
        String query = "UPDATE INVENTARIO SET cantidad = ?, observacion = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) return "Error: No se pudo obtener la conexion a la base de datos";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, cantidad);
            ps.setString(2, observacion);
            ps.setInt(3, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Inventario actualizado exitosamente" : "Error: No se pudo actualizar el inventario";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Eliminar registro de inventario por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM INVENTARIO WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) return "Error: No se pudo obtener la conexion a la base de datos";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Registro eliminado exitosamente" : "Error: No se pudo eliminar el registro";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Listar todos los movimientos de inventario
     */
    public List<String[]> findAll() {
        String query = "SELECT id, cantidad, fecha, insumo_id, observacion, tipo_movimiento, costo_unitario, valor_total " +
                "FROM INVENTARIO ORDER BY fecha DESC";
        List<String[]> registros = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) return registros;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] registro = new String[8];
                registro[0] = String.valueOf(rs.getInt("id"));
                registro[1] = String.valueOf(rs.getDouble("cantidad"));
                registro[2] = rs.getString("fecha");
                registro[3] = String.valueOf(rs.getInt("insumo_id"));
                registro[4] = String.valueOf(rs.getDouble("costo_unitario"));
                registro[5] = rs.getString("observacion");
                registro[6] = rs.getString("tipo_movimiento");
                registro[7] = String.valueOf(rs.getDouble("valor_total"));
                registros.add(registro);
            }

            rs.close();
            ps.close();
            System.out.println("Total movimientos de inventario: " + registros.size());
        } catch (SQLException e) {
            System.err.println("Error en DInventario: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return registros;
    }

    /**
     * Buscar registro por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, cantidad, fecha, insumo_id, observacion, tipo_movimiento, costo_unitario, valor_total " +
                "FROM INVENTARIO WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) return null;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] registro = new String[8];
                registro[0] = String.valueOf(rs.getInt("id"));
                registro[1] = String.valueOf(rs.getDouble("cantidad"));
                registro[2] = rs.getString("fecha");
                registro[3] = String.valueOf(rs.getInt("insumo_id"));
                registro[4] = String.valueOf(rs.getDouble("costo_unitario"));
                registro[5] = rs.getString("observacion");
                registro[6] = rs.getString("tipo_movimiento");
                registro[7] = String.valueOf(rs.getDouble("valor_total"));

                rs.close();
                ps.close();
                return registro;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DInventario: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Listar movimientos por insumo
     */
    public List<String[]> findByInsumo(int insumoId) {
        String query = "SELECT id, cantidad, fecha, insumo_id, observacion, tipo_movimiento, costo_unitario, valor_total " +
                "FROM INVENTARIO WHERE insumo_id = ? ORDER BY fecha DESC";
        List<String[]> registros = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) return registros;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, insumoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] registro = new String[8];
                registro[0] = String.valueOf(rs.getInt("id"));
                registro[1] = String.valueOf(rs.getDouble("cantidad"));
                registro[2] = rs.getString("fecha");
                registro[3] = String.valueOf(rs.getInt("insumo_id"));
                registro[4] = String.valueOf(rs.getDouble("costo_unitario"));
                registro[5] = rs.getString("observacion");
                registro[6] = rs.getString("tipo_movimiento");
                registro[7] = String.valueOf(rs.getDouble("valor_total"));
                registros.add(registro);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error en DInventario: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return registros;
    }
}
