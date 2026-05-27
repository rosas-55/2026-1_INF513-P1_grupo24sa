package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DRole;
import com.tecnoweb.grupo24sa.data.DRoleUsers;
import com.tecnoweb.grupo24sa.data.DUsuario;

import java.util.List;
import java.util.regex.Pattern;

public class BUsuario {
    private DUsuario dUsuario;
    private DRole dRole;
    private DRoleUsers dRoleUsers;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{7,15}$"
    );

    public BUsuario() {
        this.dUsuario = new DUsuario();
        this.dRole = new DRole();
        this.dRoleUsers = new DRoleUsers();
    }

    /**
     * Registra un nuevo usuario con validaciones
     */
    public String registrarUsuario(String nombre, String cedula, String celular, String direccion, String email,
                                   String password, String rol) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: El nombre es obligatorio";
        }
        
        if (nombre.length() < 3) {
            return "Error: El nombre debe tener al menos 3 caracteres";
        }

        if (direccion == null || direccion.trim().isEmpty()) {
            return "Error: La dirección es obligatoria";
        }
        
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return "Error: Email inválido";
        }
        
        if (celular != null && !celular.isEmpty() && !PHONE_PATTERN.matcher(celular).matches()) {
            return "Error: Celular inválido (debe contener solo números, 7-15 dígitos)";
        }
        
        if (cedula == null || cedula.trim().isEmpty()) {
            return "Error: La cédula es obligatoria";
        }
        
        if (password == null || password.length() < 6) {
            return "Error: La contraseña debe tener al menos 6 caracteres";
        }
        
        if (!validarRol(rol)) {
            return "Error: Rol inválido";
        }

        String[] role = dRole.findByNombre(rol);
        if (role == null) {
            return "Error: El rol no existe. Debe crearlo primero con role registrar(descripcion,nombre)";
        }

        String[] usuarioExistente = dUsuario.findByEmail(email);
        if (usuarioExistente != null) {
            return "Error: El email ya está registrado";
        }

        usuarioExistente = dUsuario.findByCedula(cedula);
        if (usuarioExistente != null) {
            return "Error: La cédula ya está registrada";
        }

        String resultado = dUsuario.save(nombre, cedula, celular, direccion, email, password);
        if (!resultado.startsWith("Usuario creado exitosamente")) {
            return resultado;
        }

        String[] usuarioCreado = dUsuario.findByEmail(email);
        if (usuarioCreado == null) {
            return "Error: No se pudo recuperar el usuario creado";
        }

        String asignacionRol = dRoleUsers.save(Integer.parseInt(role[0]), Integer.parseInt(usuarioCreado[0]));
        if (!asignacionRol.startsWith("Rol asignado")) {
            dUsuario.delete(Integer.parseInt(usuarioCreado[0]));
            return asignacionRol;
        }

        return resultado;
    }

    /**
     * Actualiza información de un usuario
     */
    public String actualizarUsuario(int id, String nombre, String cedula, String celular, String direccion,
                                    String email, String password, String rol) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: El nombre es obligatorio";
        }

        if (direccion == null || direccion.trim().isEmpty()) {
            return "Error: La dirección es obligatoria";
        }
        
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return "Error: Email inválido";
        }
        
        if (celular != null && !celular.isEmpty() && !PHONE_PATTERN.matcher(celular).matches()) {
            return "Error: Celular inválido";
        }
        
        if (!validarRol(rol)) {
            return "Error: Rol inválido";
        }

        String[] role = dRole.findByNombre(rol);
        if (role == null) {
            return "Error: El rol no existe";
        }
        
        String[] usuario = dUsuario.findOneById(id);
        if (usuario == null) {
            return "Error: Usuario no encontrado";
        }
        
        String[] usuarioEmail = dUsuario.findByEmail(email);
        if (usuarioEmail != null && !usuarioEmail[0].equals(String.valueOf(id))) {
            return "Error: El email ya está en uso por otro usuario";
        }

        String[] usuarioCedula = dUsuario.findByCedula(cedula);
        if (usuarioCedula != null && !usuarioCedula[0].equals(String.valueOf(id))) {
            return "Error: La cédula ya está en uso por otro usuario";
        }

        List<String[]> rolesPrevios = dRoleUsers.findByUser(id);
        
        String resultado = dUsuario.update(id, nombre, cedula, celular, direccion, email, password);
        if (!resultado.startsWith("Usuario actualizado exitosamente")) {
            return resultado;
        }

        for (String[] item : rolesPrevios) {
            dRoleUsers.delete(Integer.parseInt(item[0]), id);
        }

        String asignacionRol = dRoleUsers.save(Integer.parseInt(role[0]), id);
        if (!asignacionRol.startsWith("Rol asignado")) {
            for (String[] item : rolesPrevios) {
                dRoleUsers.save(Integer.parseInt(item[0]), id);
            }
            return asignacionRol;
        }

        return resultado;
    }

    /**
     * Desactiva un usuario (soft delete)
     */
    public String desactivarUsuario(int id) {
        String[] usuario = dUsuario.findOneById(id);
        if (usuario == null) {
            return "Error: Usuario no encontrado";
        }
        
        return dUsuario.delete(id);
    }

    /**
     * Reactiva un usuario
     */
    public String reactivarUsuario(int id) {
        return dUsuario.reactivate(id);
    }

    /**
     * Autentica un usuario
     */
    public String[] autenticarUsuario(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return new String[]{"Error: Email requerido"};
        }
        
        if (password == null || password.trim().isEmpty()) {
            return new String[]{"Error: Contraseña requerida"};
        }
        
        String[] usuario = dUsuario.authenticateUser(email, password);
        
        if (usuario == null) {
            return new String[]{"Error: Email o contraseña incorrectos"};
        }
        
        return usuario; // Retorna datos del usuario autenticado
    }

    /**
     * Obtiene todos los usuarios
     */
    public List<String[]> listarUsuarios() {
        return dUsuario.findAllUsers();
    }

    /**
     * Obtiene usuarios por rol
     */
    public List<String[]> listarPorRol(String rol) {
        if (!validarRol(rol)) {
            return null;
        }
        return dUsuario.findByRole(rol);
    }

    /**
     * Busca un usuario por ID
     */
    public String[] buscarPorId(int id) {
        return dUsuario.findOneById(id);
    }

    /**
     * Busca un usuario por email
     */
    public String[] buscarPorEmail(String email) {
        return dUsuario.findByEmail(email);
    }

    /**
     * Busca un usuario por cédula
     */
    public String[] buscarPorCedula(String cedula) {
        return dUsuario.findByCedula(cedula);
    }

    /**
     * Cambia la contraseña de un usuario
     */
    public String cambiarPassword(int id, String passwordActual, String passwordNuevo) {
        String[] usuario = dUsuario.findOneById(id);
        
        if (usuario == null) {
            return "Error: Usuario no encontrado";
        }

        if (!usuario[6].equals(passwordActual)) {
            return "Error: Contraseña actual incorrecta";
        }
        
        if (passwordNuevo == null || passwordNuevo.length() < 6) {
            return "Error: La nueva contraseña debe tener al menos 6 caracteres";
        }
        
        return dUsuario.update(id, usuario[1], usuario[2], usuario[3], usuario[4], usuario[5], passwordNuevo);
    }

    /**
     * Valida que el rol sea correcto
     */
    private boolean validarRol(String rol) {
        return rol != null && !rol.trim().isEmpty();
    }

    /**
     * Obtiene estadísticas de usuarios
     */
    public String[] obtenerEstadisticas() {
        List<String[]> usuarios = dUsuario.findAllUsers();
        
        int totalPropietarios = 0;
        int totalVendedores = 0;
        int totalClientes = 0;
        int totalSinRol = 0;
        
        for (String[] usuario : usuarios) {
            String rol = usuario[7];

            if (rol == null || rol.trim().isEmpty()) {
                totalSinRol++;
                continue;
            }

            switch (rol.toUpperCase()) {
                case "PROPIETARIO":
                    totalPropietarios++;
                    break;
                case "VENDEDOR":
                    totalVendedores++;
                    break;
                case "CLIENTE":
                    totalClientes++;
                    break;
                default:
                    totalSinRol++;
                    break;
            }
        }
        
        return new String[]{
            String.valueOf(usuarios.size()),
            String.valueOf(totalPropietarios),
            String.valueOf(totalVendedores),
            String.valueOf(totalClientes),
            String.valueOf(totalSinRol)
        };
    }
}
