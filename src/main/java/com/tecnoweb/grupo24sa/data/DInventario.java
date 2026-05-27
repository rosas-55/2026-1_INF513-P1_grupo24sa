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
                       String metodoInventario, String observacion, String tipoMovimiento) {
        String query = "INSERT INTO INVENTARIO (cantidad, fecha, insumo_id, metodo_inventario, observacion, tipo_movimiento) " +
                "VALUES (?, ?::date, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setDouble(1, cantidad);
            ps.setString(2, fecha);
            ps.setInt(3, insumoId);
            ps.setString(4, metodoInventario);
            ps.setString(5, observacion);
            ps.setString(6, tipoMovimiento);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Movimiento de inventario registrado exitosamente" : "Error: No se pudo registrar el movimiento";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Actualizar registro de inventario
     */
    public String update(int id, double cantidad, String observacion) {
        String query = "UPDATE INVENTARIO SET cantidad = ?, observacion = ? WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setDouble(1, cantidad);
            ps.setString(2, observacion);
            ps.setInt(3, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Inventario actualizado exitosamente" : "Error: No se pudo actualizar el inventario";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Eliminar registro de inventario por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM INVENTARIO WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Registro eliminado exitosamente" : "Error: No se pudo eliminar el registro";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Listar todos los movimientos de inventario
     */
    public List<String[]> findAll() {
        String query = "SELECT id, cantidad, fecha, insumo_id, metodo_inventario, observacion, tipo_movimiento " +
                "FROM INVENTARIO ORDER BY fecha DESC";
        List<String[]> registros = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] registro = new String[7];
                registro[0] = String.valueOf(rs.getInt("id"));
                registro[1] = String.valueOf(rs.getDouble("cantidad"));
                registro[2] = rs.getString("fecha");
                registro[3] = String.valueOf(rs.getInt("insumo_id"));
                registro[4] = rs.getString("metodo_inventario");
                registro[5] = rs.getString("observacion");
                registro[6] = rs.getString("tipo_movimiento");
                registros.add(registro);
            }

            rs.close();
            ps.close();
            System.out.println("Total movimientos de inventario: " + registros.size());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return registros;
    }

    /**
     * Buscar registro por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, cantidad, fecha, insumo_id, metodo_inventario, observacion, tipo_movimiento " +
                "FROM INVENTARIO WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] registro = new String[7];
                registro[0] = String.valueOf(rs.getInt("id"));
                registro[1] = String.valueOf(rs.getDouble("cantidad"));
                registro[2] = rs.getString("fecha");
                registro[3] = String.valueOf(rs.getInt("insumo_id"));
                registro[4] = rs.getString("metodo_inventario");
                registro[5] = rs.getString("observacion");
                registro[6] = rs.getString("tipo_movimiento");

                rs.close();
                ps.close();
                return registro;
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
     * Listar movimientos por insumo
     */
    public List<String[]> findByInsumo(int insumoId) {
        String query = "SELECT id, cantidad, fecha, insumo_id, metodo_inventario, observacion, tipo_movimiento " +
                "FROM INVENTARIO WHERE insumo_id = ? ORDER BY fecha DESC";
        List<String[]> registros = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setInt(1, insumoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] registro = new String[7];
                registro[0] = String.valueOf(rs.getInt("id"));
                registro[1] = String.valueOf(rs.getDouble("cantidad"));
                registro[2] = rs.getString("fecha");
                registro[3] = String.valueOf(rs.getInt("insumo_id"));
                registro[4] = rs.getString("metodo_inventario");
                registro[5] = rs.getString("observacion");
                registro[6] = rs.getString("tipo_movimiento");
                registros.add(registro);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return registros;
    }
}
