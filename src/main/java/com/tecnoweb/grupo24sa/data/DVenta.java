package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DVenta {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DVenta() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÓN DE VENTAS
    // -----------------------------------------------------------

    /**
     * Crear nueva venta
     * @param clienteId  FK -> USUARIO.id
     * @param estado     estado de la venta
     * @param fecha      fecha de la venta
     * @param interesMora tasa de interés por mora
     * @param nroCuotas  número de cuotas
     * @param tipo       tipo de venta
     * @param total      monto total
     * @param vendedorId FK -> USUARIO.id
     */
    public int save(int clienteId, String estado, String fecha, double interesMora,
                       int nroCuotas, String tipo, double total, int vendedorId) {
        String query = "INSERT INTO VENTA (cliente_id, estado, fecha, interes_mora, nro_cuotas, tipo, total, vendedor_id) " +
                "VALUES (?, ?, ?::date, ?, ?, ?, ?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) {
                System.err.println("Error: No se pudo obtener la conexion a la base de datos.");
                return -1;
            }
            PreparedStatement ps = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, clienteId);
            ps.setString(2, estado);
            ps.setString(3, fecha);
            ps.setDouble(4, interesMora);
            ps.setInt(5, nroCuotas);
            ps.setString(6, tipo);
            ps.setDouble(7, total);
            ps.setInt(8, vendedorId);

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
            System.err.println("Error al guardar venta: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Actualizar estado de una venta
     */
    public String updateEstado(int id, String estado) {
        String query = "UPDATE VENTA SET estado = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) return "Error: No se pudo obtener la conexion a la base de datos";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, estado);
            ps.setInt(2, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Venta actualizada exitosamente" : "Error: No se pudo actualizar la venta";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Eliminar venta por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM VENTA WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) return "Error: No se pudo obtener la conexion a la base de datos";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Venta eliminada exitosamente" : "Error: No se pudo eliminar la venta";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Listar todas las ventas
     */
    public List<String[]> findAll() {
        String query = "SELECT id, cliente_id, estado, fecha, interes_mora, nro_cuotas, tipo, total, vendedor_id FROM VENTA ORDER BY fecha DESC";
        List<String[]> ventas = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) return ventas;
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] venta = new String[9];
                venta[0] = String.valueOf(rs.getInt("id"));
                venta[1] = String.valueOf(rs.getInt("cliente_id"));
                venta[2] = rs.getString("estado");
                venta[3] = rs.getString("fecha");
                venta[4] = String.valueOf(rs.getDouble("interes_mora"));
                venta[5] = String.valueOf(rs.getInt("nro_cuotas"));
                venta[6] = rs.getString("tipo");
                venta[7] = String.valueOf(rs.getDouble("total"));
                venta[8] = String.valueOf(rs.getInt("vendedor_id"));
                ventas.add(venta);
            }

            rs.close();
            ps.close();
            System.out.println("Total ventas: " + ventas.size());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return ventas;
    }

    /**
     * Buscar venta por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, cliente_id, estado, fecha, interes_mora, nro_cuotas, tipo, total, vendedor_id FROM VENTA WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) return null;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] venta = new String[9];
                venta[0] = String.valueOf(rs.getInt("id"));
                venta[1] = String.valueOf(rs.getInt("cliente_id"));
                venta[2] = rs.getString("estado");
                venta[3] = rs.getString("fecha");
                venta[4] = String.valueOf(rs.getDouble("interes_mora"));
                venta[5] = String.valueOf(rs.getInt("nro_cuotas"));
                venta[6] = rs.getString("tipo");
                venta[7] = String.valueOf(rs.getDouble("total"));
                venta[8] = String.valueOf(rs.getInt("vendedor_id"));

                rs.close();
                ps.close();
                return venta;
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
     * Listar ventas por cliente
     */
    public List<String[]> findByCliente(int clienteId) {
        String query = "SELECT id, cliente_id, estado, fecha, interes_mora, nro_cuotas, tipo, total, vendedor_id FROM VENTA WHERE cliente_id = ? ORDER BY fecha DESC";
        List<String[]> ventas = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) return ventas;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] venta = new String[9];
                venta[0] = String.valueOf(rs.getInt("id"));
                venta[1] = String.valueOf(rs.getInt("cliente_id"));
                venta[2] = rs.getString("estado");
                venta[3] = rs.getString("fecha");
                venta[4] = String.valueOf(rs.getDouble("interes_mora"));
                venta[5] = String.valueOf(rs.getInt("nro_cuotas"));
                venta[6] = rs.getString("tipo");
                venta[7] = String.valueOf(rs.getDouble("total"));
                venta[8] = String.valueOf(rs.getInt("vendedor_id"));
                ventas.add(venta);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return ventas;
    }
}
