package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DRole;

import java.util.List;

public class BRole {

    private final DRole dRole;

    public BRole() {
        this.dRole = new DRole();
    }

    public String registrarRol(String descripcion, String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: El nombre del rol es obligatorio";
        }

        if (dRole.findByNombre(nombre) != null) {
            return "Error: El rol ya existe";
        }

        return dRole.save(descripcion, nombre);
    }

    public String actualizarRol(int id, String descripcion, String nombre) {
        if (dRole.findOneById(id) == null) {
            return "Error: Rol no encontrado";
        }

        return dRole.update(id, descripcion, nombre);
    }

    public String eliminarRol(int id) {
        if (dRole.findOneById(id) == null) {
            return "Error: Rol no encontrado";
        }

        return dRole.delete(id);
    }

    public List<String[]> listarRoles() {
        return dRole.findAll();
    }

    public String[] buscarPorId(int id) {
        return dRole.findOneById(id);
    }
}