package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAccion {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DAccion() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÓN DE ACCIONES
    // Atributos: codigo (int), descripcion (int), estado (int),
    //            name (int)
    // Nota: Los tipos "int" del diagrama se interpretan según contexto
    // -----------------------------------------------------------

    /**
     * Crear nueva acción
     */
    public String save(int codigo, String descripcion, int estado, String name) {
        String query = "INSERT INTO ACCION (codigo, descripcion, estado, name) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setInt(1, codigo);
            ps.setString(2, descripcion);
            ps.setInt(3, estado);
            ps.setString(4, name);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Acción creada exitosamente" : "Error: No se pudo crear la acción";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Actualizar acción existente
     */
    public String update(int id, int codigo, String descripcion, int estado, String name) {
        String query = "UPDATE ACCION SET codigo = ?, descripcion = ?, estado = ?, name = ? WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setInt(1, codigo);
            ps.setString(2, descripcion);
            ps.setInt(3, estado);
            ps.setString(4, name);
            ps.setInt(5, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Acción actualizada exitosamente" : "Error: No se pudo actualizar la acción";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Eliminar acción por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM ACCION WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Acción eliminada exitosamente" : "Error: No se pudo eliminar la acción";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Listar todas las acciones
     */
    public List<String[]> findAll() {
        String query = "SELECT id, codigo, descripcion, estado, name FROM ACCION ORDER BY name";
        List<String[]> acciones = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] accion = new String[5];
                accion[0] = String.valueOf(rs.getInt("id"));
                accion[1] = String.valueOf(rs.getInt("codigo"));
                accion[2] = rs.getString("descripcion");
                accion[3] = String.valueOf(rs.getInt("estado"));
                accion[4] = rs.getString("name");
                acciones.add(accion);
            }

            rs.close();
            ps.close();
            System.out.println("Total acciones: " + acciones.size());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return acciones;
    }

    /**
     * Buscar acción por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, codigo, descripcion, estado, name FROM ACCION WHERE id = ?";
        try {
            PreparedStatement ps = databaseConection.openConnection().prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] accion = new String[5];
                accion[0] = String.valueOf(rs.getInt("id"));
                accion[1] = String.valueOf(rs.getInt("codigo"));
                accion[2] = rs.getString("descripcion");
                accion[3] = String.valueOf(rs.getInt("estado"));
                accion[4] = rs.getString("name");

                rs.close();
                ps.close();
                return accion;
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
