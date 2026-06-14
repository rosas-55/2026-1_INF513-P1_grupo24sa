package com.tecnoweb.grupo24sa.utils;

/**
 * Contexto del usuario remitente del correo electrónico.
 * Se construye en CommandInterpreter al recibir cada email y se pasa
 * a los handlers que necesitan conocer quién está ejecutando el comando.
 *
 * Estructura del arreglo de DUsuario:
 *   [0]=id  [1]=nombre  [2]=cedula  [3]=celular  [4]=direccion  [5]=email  [6]=password  [7]=rol
 */
public class ContextoEmail {

    private final int    usuarioId;
    private final String nombre;
    private final String rol;
    private final String email;

    public ContextoEmail(int usuarioId, String nombre, String rol, String email) {
        this.usuarioId = usuarioId;
        this.nombre    = nombre != null ? nombre : "";
        this.rol       = rol    != null ? rol.toUpperCase().trim() : "";
        this.email     = email  != null ? email : "";
    }

    /** Crea un ContextoEmail desde el arreglo devuelto por DUsuario.findByEmail() */
    public static ContextoEmail desde(String[] usuario) {
        if (usuario == null || usuario.length < 8) return null;
        return new ContextoEmail(
            Integer.parseInt(usuario[0]),   // id
            usuario[1],                      // nombre
            usuario[7],                      // rol
            usuario[5]                       // email
        );
    }

    public int    getUsuarioId() { return usuarioId; }
    public String getNombre()    { return nombre; }
    public String getRol()       { return rol; }
    public String getEmail()     { return email; }

    public boolean esCliente()     { return "CLIENTE".equals(rol); }
    public boolean esVendedor()    { return "VENDEDOR".equals(rol); }
    public boolean esPropietario() { return "PROPIETARIO".equals(rol); }

    @Override
    public String toString() {
        return "ContextoEmail{id=" + usuarioId + ", nombre='" + nombre + "', rol='" + rol + "', email='" + email + "'}";
    }
}
