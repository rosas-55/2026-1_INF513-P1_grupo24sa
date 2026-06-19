package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DModulo {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DModulo() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    // -----------------------------------------------------------
    // CU - GESTIÃ“N DE MÃ“DULOS
    // Atributos: codigo (int), descripcion (int), estado (int),
    //            name (int), nivel (int)
    // Nota: Los tipos "int" del diagrama se interpretan como int en BD
    // -----------------------------------------------------------

    /**
     * Crear nuevo mÃ³dulo
     */
    public String save(int codigo, String descripcion, int estado, String name, int nivel) {
        String query = "INSERT INTO MODULO (codigo, descripcion, estado, name, nivel) VALUES (?, ?, ?, ?, ?)";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, codigo);
            ps.setString(2, descripcion);
            ps.setInt(3, estado);
            ps.setString(4, name);
            ps.setInt(5, nivel);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "MÃ³dulo creado exitosamente" : "Error: No se pudo crear el mÃ³dulo";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Actualizar mÃ³dulo existente
     */
    public String update(int id, int codigo, String descripcion, int estado, String name, int nivel) {
        String query = "UPDATE MODULO SET codigo = ?, descripcion = ?, estado = ?, name = ?, nivel = ? WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, codigo);
            ps.setString(2, descripcion);
            ps.setInt(3, estado);
            ps.setString(4, name);
            ps.setInt(5, nivel);
            ps.setInt(6, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "MÃ³dulo actualizado exitosamente" : "Error: No se pudo actualizar el mÃ³dulo";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Eliminar mÃ³dulo por ID
     */
    public String delete(int id) {
        String query = "DELETE FROM MODULO WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "MÃ³dulo eliminado exitosamente" : "Error: No se pudo eliminar el mÃ³dulo";
        } catch (SQLException e) {
            return "Error de BD: " + e.getMessage();
        }
    }

    /**
     * Listar todos los mÃ³dulos
     */
    public List<String[]> findAll() {
        String query = "SELECT id, codigo, descripcion, estado, name, nivel FROM MODULO ORDER BY nivel, name";
        List<String[]> modulos = new ArrayList<>();
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String[] modulo = new String[6];
                modulo[0] = String.valueOf(rs.getInt("id"));
                modulo[1] = String.valueOf(rs.getInt("codigo"));
                modulo[2] = rs.getString("descripcion");
                modulo[3] = String.valueOf(rs.getInt("estado"));
                modulo[4] = rs.getString("name");
                modulo[5] = String.valueOf(rs.getInt("nivel"));
                modulos.add(modulo);
            }

            rs.close();
            ps.close();
            System.out.println("Total mÃ³dulos: " + modulos.size());
        } catch (SQLException e) {
            System.err.println("Error en DModulo: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
        return modulos;
    }

    /**
     * Buscar mÃ³dulo por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT id, codigo, descripcion, estado, name, nivel FROM MODULO WHERE id = ?";
        try {
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] modulo = new String[6];
                modulo[0] = String.valueOf(rs.getInt("id"));
                modulo[1] = String.valueOf(rs.getInt("codigo"));
                modulo[2] = rs.getString("descripcion");
                modulo[3] = String.valueOf(rs.getInt("estado"));
                modulo[4] = rs.getString("name");
                modulo[5] = String.valueOf(rs.getInt("nivel"));

                rs.close();
                ps.close();
                return modulo;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DModulo: " + e.getMessage());
            throw new RuntimeException("Error de conexion a la base de datos: " + e.getMessage());
        }
    }
}

