package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DInsumo;

import java.util.List;

/**
 * CU6 - Gestión de Insumos (parte del módulo de Inventario)
 */
public class BInsumo {

    private final DInsumo dInsumo;

    public BInsumo() {
        this.dInsumo = new DInsumo();
    }

    /**
     * Registra un nuevo insumo
     * @param costoUnitario costo por unidad (>= 0)
     * @param descripcion   descripción del insumo
     * @param estado        estado (activo/inactivo)
     * @param nombre        nombre del insumo
     * @param stockActual   cantidad actual en stock (>= 0)
     * @param stockMinimo   stock mínimo requerido (>= 0)
     * @param unidadMedida  unidad de medida (kg, lt, unidad, etc.)
     */
    public String registrarInsumo(double costoUnitario, String descripcion, String estado,
                                   String nombre, double stockActual, double stockMinimo,
                                   String unidadMedida) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: El nombre del insumo es obligatorio";
        }
        if (costoUnitario < 0) {
            return "Error: El costo unitario no puede ser negativo";
        }
        if (stockActual < 0) {
            return "Error: El stock actual no puede ser negativo";
        }
        if (stockMinimo < 0) {
            return "Error: El stock mínimo no puede ser negativo";
        }
        if (unidadMedida == null || unidadMedida.trim().isEmpty()) {
            return "Error: La unidad de medida es obligatoria";
        }
        if (estado == null || estado.trim().isEmpty()) {
            return "Error: El estado es obligatorio";
        }
        return dInsumo.save(costoUnitario, descripcion == null ? "" : descripcion.trim(),
                estado.trim(), nombre.trim(), stockActual, stockMinimo, unidadMedida.trim());
    }

    /**
     * Actualiza un insumo existente
     */
    public String actualizarInsumo(int id, double costoUnitario, String descripcion, String estado,
                                    String nombre, double stockActual, double stockMinimo,
                                    String unidadMedida) {
        if (dInsumo.findOneById(id) == null) {
            return "Error: Insumo no encontrado con ID: " + id;
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            return "Error: El nombre del insumo es obligatorio";
        }
        if (costoUnitario < 0) {
            return "Error: El costo unitario no puede ser negativo";
        }
        if (stockActual < 0) {
            return "Error: El stock actual no puede ser negativo";
        }
        if (stockMinimo < 0) {
            return "Error: El stock mínimo no puede ser negativo";
        }
        if (unidadMedida == null || unidadMedida.trim().isEmpty()) {
            return "Error: La unidad de medida es obligatoria";
        }
        return dInsumo.update(id, costoUnitario, descripcion == null ? "" : descripcion.trim(),
                estado == null ? "" : estado.trim(), nombre.trim(),
                stockActual, stockMinimo, unidadMedida.trim());
    }

    /**
     * Elimina un insumo por ID
     */
    public String eliminarInsumo(int id) {
        if (dInsumo.findOneById(id) == null) {
            return "Error: Insumo no encontrado con ID: " + id;
        }
        return dInsumo.delete(id);
    }

    /**
     * Lista todos los insumos
     */
    public List<String[]> listarInsumos() {
        return dInsumo.findAll();
    }

    /**
     * Busca un insumo por ID
     */
    public String[] buscarPorId(int id) {
        return dInsumo.findOneById(id);
    }

    /**
     * Lista insumos con stock por debajo del mínimo
     */
    public List<String[]> listarStockBajo() {
        return dInsumo.findStockBajoMinimo();
    }
}
