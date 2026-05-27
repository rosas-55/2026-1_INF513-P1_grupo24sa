package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DProveedor;

import java.util.List;
import java.util.regex.Pattern;

/**
 * CU - Gestión de Proveedores
 */
public class BProveedor {

    private final DProveedor dProveedor;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{6,15}$");

    public BProveedor() {
        this.dProveedor = new DProveedor();
    }

    /**
     * Registra un nuevo proveedor
     * @param direccion dirección del proveedor
     * @param nombre    nombre o razón social
     * @param telefono  teléfono de contacto (6-15 dígitos)
     */
    public String registrarProveedor(String direccion, String nombre, String telefono) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: El nombre del proveedor es obligatorio";
        }
        if (nombre.trim().length() < 2) {
            return "Error: El nombre debe tener al menos 2 caracteres";
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            return "Error: La dirección es obligatoria";
        }
        if (telefono != null && !telefono.trim().isEmpty()
                && !PHONE_PATTERN.matcher(telefono.trim()).matches()) {
            return "Error: Teléfono inválido (solo dígitos, 6-15 caracteres)";
        }
        return dProveedor.save(direccion.trim(), nombre.trim(),
                telefono == null ? "" : telefono.trim());
    }

    /**
     * Actualiza un proveedor existente
     */
    public String actualizarProveedor(int id, String direccion, String nombre, String telefono) {
        if (dProveedor.findOneById(id) == null) {
            return "Error: Proveedor no encontrado con ID: " + id;
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: El nombre del proveedor es obligatorio";
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            return "Error: La dirección es obligatoria";
        }
        if (telefono != null && !telefono.trim().isEmpty()
                && !PHONE_PATTERN.matcher(telefono.trim()).matches()) {
            return "Error: Teléfono inválido (solo dígitos, 6-15 caracteres)";
        }
        return dProveedor.update(id, direccion.trim(), nombre.trim(),
                telefono == null ? "" : telefono.trim());
    }

    /**
     * Elimina un proveedor por ID
     */
    public String eliminarProveedor(int id) {
        if (dProveedor.findOneById(id) == null) {
            return "Error: Proveedor no encontrado con ID: " + id;
        }
        return dProveedor.delete(id);
    }

    /**
     * Lista todos los proveedores
     */
    public List<String[]> listarProveedores() {
        return dProveedor.findAll();
    }

    /**
     * Busca un proveedor por ID
     */
    public String[] buscarPorId(int id) {
        return dProveedor.findOneById(id);
    }
}
