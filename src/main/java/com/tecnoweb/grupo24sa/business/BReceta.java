package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DReceta;
import com.tecnoweb.grupo24sa.data.DProducto;
import com.tecnoweb.grupo24sa.data.DInsumo;
import com.tecnoweb.grupo24sa.data.DRecetaInsumo;

import java.util.List;

/**
 * CU4 - Gestión de Recetas de Producción
 */
public class BReceta {

    private final DReceta dReceta;
    private final DProducto dProducto;
    private final DInsumo dInsumo;
    private final DRecetaInsumo dRecetaInsumo;

    public BReceta() {
        this.dReceta = new DReceta();
        this.dProducto = new DProducto();
        this.dInsumo = new DInsumo();
        this.dRecetaInsumo = new DRecetaInsumo();
    }

    /**
     * Registra una nueva receta junto con sus ingredientes
     * @param descripcion       descripción de la receta
     * @param productoId        ID del producto final (debe existir)
     * @param tiempoPreparacion tiempo de preparación en minutos
     * @param ingredients       lista de ingredientes [insumo_id, cantidad]
     */
    public String registrarReceta(String descripcion, int productoId, int tiempoPreparacion, List<String[]> ingredients) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return "Error: La descripción de la receta es obligatoria";
        }
        if (productoId <= 0) {
            return "Error: El ID del producto debe ser mayor a 0";
        }
        if (dProducto.findOneById(productoId) == null) {
            return "Error: El producto con ID " + productoId + " no existe";
        }
        if (tiempoPreparacion < 0) {
            return "Error: El tiempo de preparación no puede ser negativo";
        }

        // Validar todos los ingredientes antes de insertar nada
        for (String[] ing : ingredients) {
            try {
                int insumoId = Integer.parseInt(ing[0]);
                double cantidad = Double.parseDouble(ing[1]);

                if (insumoId <= 0) {
                    return "Error: El ID del insumo debe ser mayor a 0";
                }
                if (dInsumo.findOneById(insumoId) == null) {
                    return "Error: El insumo con ID " + insumoId + " no existe";
                }
                if (cantidad <= 0) {
                    return "Error: La cantidad del insumo " + insumoId + " debe ser mayor a 0";
                }
            } catch (NumberFormatException e) {
                return "Error: Formato numérico inválido en ingredientes";
            }
        }

        // Guardar la receta
        int recetaId = dReceta.save(descripcion.trim(), productoId, tiempoPreparacion);
        if (recetaId == -1) {
            return "Error: No se pudo registrar la receta";
        }

        // Guardar los ingredientes de la receta
        for (String[] ing : ingredients) {
            int insumoId = Integer.parseInt(ing[0]);
            double cantidad = Double.parseDouble(ing[1]);
            dRecetaInsumo.save(cantidad, insumoId, recetaId);
        }

        return "Receta registrada exitosamente con ID: " + recetaId + " y sus ingredientes asociados.";
    }

    /**
     * Actualiza una receta existente
     */
    public String actualizarReceta(int id, String descripcion, int productoId, int tiempoPreparacion) {
        if (dReceta.findOneById(id) == null) {
            return "Error: Receta no encontrada con ID: " + id;
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return "Error: La descripción de la receta es obligatoria";
        }
        if (productoId <= 0 || dProducto.findOneById(productoId) == null) {
            return "Error: El producto con ID " + productoId + " no existe";
        }
        return dReceta.update(id, descripcion.trim(), productoId, tiempoPreparacion);
    }

    /**
     * Elimina una receta por ID
     */
    public String eliminarReceta(int id) {
        if (dReceta.findOneById(id) == null) {
            return "Error: Receta no encontrada con ID: " + id;
        }
        return dReceta.delete(id);
    }

    /**
     * Lista todas las recetas
     */
    public List<String[]> listarRecetas() {
        return dReceta.findAll();
    }

    /**
     * Busca una receta por ID
     */
    public String[] buscarPorId(int id) {
        return dReceta.findOneById(id);
    }

    /**
     * Lista recetas de un producto específico
     */
    public List<String[]> listarPorProducto(int productoId) {
        return dReceta.findByProducto(productoId);
    }
}
