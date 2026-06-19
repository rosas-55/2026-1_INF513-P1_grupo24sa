package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DCuota {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DCuota() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE CUOTAS
    // Atributos: estado, fecha_pago, fecha_vencimiento, id,
    //            interes_mora (int), monto_pagado, nro_cuota,
    //            plan_pago, venta_id
    // -----------------------------------------------------------

    /**
     * Crear nueva cuota
     */
    public String save(String estado, String fechaPago, String fechaVencimiento,
                       int interesMora, double montoPagado, int nroCuota,
                        String planPago, int ventaId) {
        String query = "INSERT INTO CUOTA (estado, fecha_pago, fecha_vencimiento, interes_mora, monto_pagado, nro_cuota, plan_pago, venta_id) " +
                "VALUES (?, ?::date, ?::date, ?, ?, ?, ?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, estado);
            if (fechaPago == null || fechaPago.trim().equalsIgnoreCase("null") || fechaPago.trim().isEmpty()) {
                ps.setNull(2, java.sql.Types.DATE);
            } else {
                ps.setString(2, fechaPago.trim());
            }
            ps.setString(3, fechaVencimiento);
            ps.setInt(4, interesMora);
            ps.setDouble(5, montoPagado);
            ps.setInt(6, nroCuota);
            ps.setString(7, planPago);
            ps.setInt(8, ventaId);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Cuota creada exitosamente" : "Error: No se pudo crear la cuota";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Actualizar cuota (pago)
     */
    public String update(int id, String estado, String fechaPago, double montoPagado) {
        String query = "UPDATE CUOTA SET estado = ?, fecha_pago = ?::date, monto_pagado = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, estado);
            if (fechaPago == null || fechaPago.trim().equalsIgnoreCase("null") || fechaPago.trim().isEmpty()) {
                ps.setNull(2, java.sql.Types.DATE);
            } else {
                ps.setString(2, fechaPago.trim());
            }
            ps.setDouble(3, montoPagado);
            ps.setInt(4, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Cuota actualizada exitosamente" : "Error: No se pudo actualizar la cuota";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Actualizar el pagofacilTransactionId de una cuota
     */
    public String updatePagoFacilTransactionId(int id, long pagofacilTransactionId) {
        String query = "UPDATE CUOTA SET pagofacil_transaction_id = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) return "Error: No se pudo obtener la conexion a la base de datos";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, pagofacilTransactionId);
            ps.setInt(2, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Cuota actualizada con transaction ID de PagoFácil" : "Error: No se pudo actualizar la cuota";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Eliminar cuota por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM CUOTA WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Cuota eliminada exitosamente" : "Error: No se pudo eliminar la cuota";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Listar cuotas por venta
     */
    public List<String[]> findByVenta(int ventaId) {
        String query = "SELECT id, estado, fecha_pago, fecha_vencimiento, interes_mora, monto_pagado, nro_cuota, plan_pago, venta_id, pagofacil_transaction_id " +
                "FROM CUOTA WHERE venta_id = ? ORDER BY nro_cuota";
        List<String[]> cuotas = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, ventaId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] cuota = new String[10];
                cuota[0] = String.valueOf(rs.getInt("id"));
                cuota[1] = rs.getString("estado");
                cuota[2] = rs.getString("fecha_pago");
                cuota[3] = rs.getString("fecha_vencimiento");
                cuota[4] = String.valueOf(rs.getInt("interes_mora"));
                cuota[5] = String.valueOf(rs.getDouble("monto_pagado"));
                cuota[6] = String.valueOf(rs.getInt("nro_cuota"));
                cuota[7] = rs.getString("plan_pago");
                cuota[8] = String.valueOf(rs.getInt("venta_id"));
                cuota[9] = rs.getObject("pagofacil_transaction_id") != null ? String.valueOf(rs.getLong("pagofacil_transaction_id")) : null;
                cuotas.add(cuota);
            }

            rs.close();
            ps.close();
            System.out.println("Total cuotas de venta " + ventaId + ": " + cuotas.size());
        } catch (SQLException e) {
            System.err.println("Error en DCuota: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return cuotas;
    }

    /**
     * Listar cuotas por cliente
     */
    public List<String[]> findByCliente(int clienteId) {
        String query = "SELECT c.id, c.estado, c.fecha_pago, c.fecha_vencimiento, c.interes_mora, c.monto_pagado, c.nro_cuota, c.plan_pago, c.venta_id, c.pagofacil_transaction_id " +
                "FROM CUOTA c INNER JOIN VENTA v ON c.venta_id = v.id WHERE v.cliente_id = ? ORDER BY c.fecha_vencimiento ASC";
        List<String[]> cuotas = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] cuota = new String[10];
                cuota[0] = String.valueOf(rs.getInt("id"));
                cuota[1] = rs.getString("estado");
                cuota[2] = rs.getString("fecha_pago");
                cuota[3] = rs.getString("fecha_vencimiento");
                cuota[4] = String.valueOf(rs.getInt("interes_mora"));
                cuota[5] = String.valueOf(rs.getDouble("monto_pagado"));
                cuota[6] = String.valueOf(rs.getInt("nro_cuota"));
                cuota[7] = rs.getString("plan_pago");
                cuota[8] = String.valueOf(rs.getInt("venta_id"));
                cuota[9] = rs.getObject("pagofacil_transaction_id") != null ? String.valueOf(rs.getLong("pagofacil_transaction_id")) : null;
                cuotas.add(cuota);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error en DCuota: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return cuotas;
    }

    /**
     * Buscar cuota por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, estado, fecha_pago, fecha_vencimiento, interes_mora, monto_pagado, nro_cuota, plan_pago, venta_id, pagofacil_transaction_id " +
                "FROM CUOTA WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] cuota = new String[10];
                cuota[0] = String.valueOf(rs.getInt("id"));
                cuota[1] = rs.getString("estado");
                cuota[2] = rs.getString("fecha_pago");
                cuota[3] = rs.getString("fecha_vencimiento");
                cuota[4] = String.valueOf(rs.getInt("interes_mora"));
                cuota[5] = String.valueOf(rs.getDouble("monto_pagado"));
                cuota[6] = String.valueOf(rs.getInt("nro_cuota"));
                cuota[7] = rs.getString("plan_pago");
                cuota[8] = String.valueOf(rs.getInt("venta_id"));
                cuota[9] = rs.getObject("pagofacil_transaction_id") != null ? String.valueOf(rs.getLong("pagofacil_transaction_id")) : null;

                rs.close();
                ps.close();
                return cuota;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DCuota: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
    }
}

