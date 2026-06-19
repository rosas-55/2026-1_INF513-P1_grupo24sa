package com.tecnoweb.grupo24sa.data;

import com.tecnoweb.grupo24sa.ConfigDB.ConfigDB;
import com.tecnoweb.grupo24sa.ConfigDB.DatabaseConection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DUsuario {

    private final DatabaseConection databaseConection;
    ConfigDB configDB = new ConfigDB();

    public DUsuario() {
        this.databaseConection = new DatabaseConection(configDB.getUser(), configDB.getPassword(),
                configDB.getHost(), configDB.getPort(), configDB.getDbName());
    }

    public void disconnect() {
        if (databaseConection != null) {
            databaseConection.closeConnection();
        }
    }

    /**
     * Crear nuevo usuario
     */
    public String save(String nombre, String cedula, String celular, String direccion, String email,
            String password) {
        String query = "INSERT INTO \"User\" (cedula, celular, direccion, email, name, password) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            System.out.println("DUsuario.save SQL: " + query);
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, cedula);
            ps.setString(2, celular);
            ps.setString(3, direccion);
            ps.setString(4, email);
            ps.setString(5, nombre);
            ps.setString(6, password);
            System.out.println("DUsuario.save params: cedula=" + cedula + ", celular=" + celular + ", direccion=" + direccion + ", email=" + email + ", nombre=" + nombre);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Usuario creado exitosamente" : "Error: No se pudo crear el usuario";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Actualizar usuario existente
     */
    public String update(int id, String nombre, String cedula, String celular, String direccion, String email,
            String password) {
        String query = "UPDATE \"User\" SET cedula = ?, celular = ?, direccion = ?, email = ?, name = ?, password = ? WHERE id = ?";

        try {
            System.out.println("DUsuario.update SQL: " + query);
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, cedula);
            ps.setString(2, celular);
            ps.setString(3, direccion);
            ps.setString(4, email);
            ps.setString(5, nombre);
            ps.setString(6, password);
            ps.setInt(7, id);
            System.out.println("DUsuario.update params: id=" + id + ", cedula=" + cedula + ", celular=" + celular + ", direccion=" + direccion + ", email=" + email + ", nombre=" + nombre);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0 ? "Usuario actualizado exitosamente" : "Error: No se pudo actualizar el usuario";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Eliminar usuario y sus roles asociados
     */
    public String delete(int id) {
        try {
                PreparedStatement deleteRoles = databaseConection.openConnection()
                    .prepareStatement("DELETE FROM \"role_users\" WHERE user_id = ?");
            deleteRoles.setInt(1, id);
            deleteRoles.executeUpdate();
            deleteRoles.close();

                PreparedStatement deleteUser = databaseConection.openConnection()
                    .prepareStatement("DELETE FROM \"User\" WHERE id = ?");
            deleteUser.setInt(1, id);

            int result = deleteUser.executeUpdate();
            deleteUser.close();

            return result > 0 ? "Usuario eliminado exitosamente" : "Error: No se pudo eliminar el usuario";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Reactivaci脙鲁n no soportada con el esquema actual
     */
    public String reactivate(int id) {
        return "Error: El esquema actual no soporta reactivaci脙鲁n porque la tabla User no tiene columna activo";
    }

    /**
     * Listar todos los usuarios
     */
    public List<String[]> findAllUsers() {
        String query = "SELECT u.id, u.name, u.cedula, u.celular, u.direccion, u.email, u.password, " +
            "COALESCE(r.nombre, '') AS rol " +
                "FROM \"User\" u " +
                "LEFT JOIN role_users ru ON ru.user_id = u.id " +
                "LEFT JOIN role r ON r.id = ru.role_id " +
            "ORDER BY u.name";
        List<String[]> usuarios = new ArrayList<>();

        try {
            System.out.println("DUsuario.findAllUsers SQL: " + query);
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                usuarios.add(mapUser(rs));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error en DUsuario: " + e.getMessage());`n        throw new RuntimeException("Error de conexi髇 a la base de datos: " + e.getMessage());`n        }

        return usuarios;
    }

    /**
     * Buscar usuario por ID
     */
    public String[] findOneById(int id) {
        String query = "SELECT u.id, u.name, u.cedula, u.celular, u.direccion, u.email, u.password, " +
            "COALESCE(r.nombre, '') AS rol " +
            "FROM \"User\" u " +
            "LEFT JOIN role_users ru ON ru.user_id = u.id " +
            "LEFT JOIN role r ON r.id = ru.role_id " +
            "WHERE u.id = ?";

        try {
            System.out.println("DUsuario.findOneById SQL: " + query + " params: id=" + id);
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) {
                throw new java.sql.SQLException("No se pudo conectar a la base de datos. Verifica que el servidor est茅 en ejecuci贸n.");
            }
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] usuario = mapUser(rs);
                rs.close();
                ps.close();
                return usuario;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DUsuario.findOneById: " + e.getMessage());
            // Propagar como RuntimeException para que el negocio sepa que es error de conexi贸n
            throw new RuntimeException("Error de conexi贸n a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Listar usuarios por rol espec脙颅fico
     */
    public List<String[]> findByRole(String rol) {
        String query = "SELECT u.id, u.name, u.cedula, u.celular, u.direccion, u.email, u.password, " +
            "COALESCE(r.nombre, '') AS rol " +
            "FROM \"User\" u " +
            "JOIN role_users ru ON ru.user_id = u.id " +
            "JOIN role r ON r.id = ru.role_id " +
            "WHERE UPPER(r.nombre) = UPPER(?) " +
            "ORDER BY u.name";
        List<String[]> usuarios = new ArrayList<>();

        try {
            System.out.println("DUsuario.findByRole SQL: " + query + " params: rol=" + rol);
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, rol);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                usuarios.add(mapUser(rs));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error en DUsuario: " + e.getMessage());`n        throw new RuntimeException("Error de conexi髇 a la base de datos: " + e.getMessage());`n        }

        return usuarios;
    }

    /**
     * Buscar usuario por c脙漏dula
     */
    public String[] findByCedula(String cedula) {
        String query = "SELECT u.id, u.name, u.cedula, u.celular, u.direccion, u.email, u.password, " +
            "COALESCE(r.nombre, '') AS rol " +
            "FROM \"User\" u " +
            "LEFT JOIN role_users ru ON ru.user_id = u.id " +
            "LEFT JOIN role r ON r.id = ru.role_id " +
            "WHERE u.cedula = ?";

        try {
            System.out.println("DUsuario.findByCedula SQL: " + query + " params: cedula=" + cedula);
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] usuario = mapUser(rs);
                rs.close();
                ps.close();
                return usuario;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DUsuario.findByCedula: " + e.getMessage());
            throw new RuntimeException("Error de conexi贸n a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Autentica usuario por email y password
     */
    public String[] authenticateUser(String email, String password) {
        String query = "SELECT u.id, u.name, u.cedula, u.celular, u.direccion, u.email, u.password, " +
            "COALESCE(r.nombre, '') AS rol " +
            "FROM \"User\" u " +
            "LEFT JOIN role_users ru ON ru.user_id = u.id " +
            "LEFT JOIN role r ON r.id = ru.role_id " +
            "WHERE u.email = ? AND u.password = ?";

        try {
            System.out.println("DUsuario.authenticateUser SQL: " + query + " params: email=" + email + ", password=***");
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) throw new java.sql.SQLException("No se pudo obtener la conexion a la base de datos");
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] usuario = mapUser(rs);
                rs.close();
                ps.close();
                return usuario;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DUsuario.authenticateUser: " + e.getMessage());
            throw new RuntimeException("Error de conexi贸n a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Buscar usuario por email
     */
    public String[] findByEmail(String email) {
        String query = "SELECT u.id, u.name, u.cedula, u.celular, u.direccion, u.email, u.password, " +
            "COALESCE(r.nombre, '') AS rol " +
            "FROM \"User\" u " +
            "LEFT JOIN role_users ru ON ru.user_id = u.id " +
            "LEFT JOIN role r ON r.id = ru.role_id " +
            "WHERE u.email = ?";

        try {
            System.out.println("DUsuario.findByEmail SQL: " + query + " params: email=" + email);
            java.sql.Connection conn = databaseConection.openConnection();
            if (conn == null) {
                throw new java.sql.SQLException("No se pudo conectar a la base de datos. Verifica que el servidor est茅 en ejecuci贸n.");
            }
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String[] usuario = mapUser(rs);
                rs.close();
                ps.close();
                return usuario;
            }

            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            System.err.println("Error en DUsuario.findByEmail: " + e.getMessage());
            throw new RuntimeException("Error de conexi贸n a la base de datos: " + e.getMessage());
        }
    }

    private String[] mapUser(ResultSet rs) throws SQLException {
        String[] usuario = new String[8];
        usuario[0] = String.valueOf(rs.getInt("id"));
        usuario[1] = rs.getString("name");
        usuario[2] = rs.getString("cedula");
        usuario[3] = rs.getString("celular");
        usuario[4] = rs.getString("direccion");
        usuario[5] = rs.getString("email");
        usuario[6] = rs.getString("password");
        usuario[7] = rs.getString("rol");
        return usuario;
    }
}
