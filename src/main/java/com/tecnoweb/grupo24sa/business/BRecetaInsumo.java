package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DRecetaInsumo;
import com.tecnoweb.grupo24sa.data.DReceta;
import com.tecnoweb.grupo24sa.data.DInsumo;

import java.util.List;

/**
 * Gestión de la relación Receta-Insumo (ingredientes de una receta)
 */
public class BRecetaInsumo {

    private final DRecetaInsumo dRecetaInsumo;
    private final DReceta dReceta;
    private final DInsumo dInsumo;

    public BRecetaInsumo() {
        this.dRecetaInsumo = new DRecetaInsumo();
        this.dReceta = new DReceta();
        this.dInsumo = new DInsumo();
    }

    /**
     * Agrega un insumo a una receta
     * @param cantidad  cantidad del insumo requerida en la receta (> 0)
     * @param insumoId  ID del insumo (debe existir)
     * @param recetaId  ID de la receta (debe existir)
     */
    public String registrarIngrediente(double cantidad, int insumoId, int recetaId) {
        if (cantidad <= 0) {
            return "Error: La cantidad del insumo debe ser mayor a 0";
        }
        if (recetaId <= 0 || dReceta.findOneById(recetaId) == null) {
            return "Error: La receta con ID " + recetaId + " no existe";
        }
        if (insumoId <= 0 || dInsumo.findOneById(insumoId) == null) {
            return "Error: El insumo con ID " + insumoId + " no existe";
        }
        return dRecetaInsumo.save(cantidad, insumoId, recetaId);
    }

    /**
     * Actualiza la cantidad de un insumo en una receta
     */
    public String actualizarIngrediente(int recetaId, int insumoId, double cantidad) {
        if (cantidad <= 0) {
            return "Error: La cantidad del insumo debe ser mayor a 0";
        }
        if (dReceta.findOneById(recetaId) == null) {
            return "Error: La receta con ID " + recetaId + " no existe";
        }
        if (dInsumo.findOneById(insumoId) == null) {
            return "Error: El insumo con ID " + insumoId + " no existe";
        }
        return dRecetaInsumo.update(recetaId, insumoId, cantidad);
    }

    /**
     * Elimina un insumo de una receta
     */
    public String eliminarIngrediente(int recetaId, int insumoId) {
        return dRecetaInsumo.delete(recetaId, insumoId);
    }

    /**
     * Lista todos los insumos (ingredientes) de una receta
     */
    public List<String[]> listarPorReceta(int recetaId) {
        return dRecetaInsumo.findByReceta(recetaId);
    }
}
