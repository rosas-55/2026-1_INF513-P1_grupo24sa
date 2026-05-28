package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DProduccion;
import com.tecnoweb.grupo24sa.data.DReceta;
import com.tecnoweb.grupo24sa.data.DRecetaInsumo;
import com.tecnoweb.grupo24sa.data.DInsumo;
import com.tecnoweb.grupo24sa.data.DInventario;
import com.tecnoweb.grupo24sa.data.DProducto;

import java.util.List;

/**
 * CU4 - Gestión de Producción
 * Al registrar una producción, se descuenta el stock de cada insumo
 * requerido por la receta y se registra la salida en Inventario.
 */
public class BProduccion {

    private final DProduccion dProduccion;
    private final DReceta dReceta;
    private final DRecetaInsumo dRecetaInsumo;
    private final DInsumo dInsumo;
    private final DInventario dInventario;
    private final DProducto dProducto;

    public BProduccion() {
        this.dProduccion = new DProduccion();
        this.dReceta = new DReceta();
        this.dRecetaInsumo = new DRecetaInsumo();
        this.dInsumo = new DInsumo();
        this.dInventario = new DInventario();
        this.dProducto = new DProducto();
    }

    /**
     * Registra una producción, verifica stock suficiente y descuenta insumos
     * @param cantidadProducida unidades producidas (> 0)
     * @param fecha             fecha de producción (YYYY-MM-DD)
     * @param recetaId          ID de la receta (debe existir)
     */
    public String registrarProduccion(double cantidadProducida, String fecha, int recetaId) {
        if (cantidadProducida <= 0) {
            return "Error: La cantidad producida debe ser mayor a 0";
        }
        if (recetaId <= 0) {
            return "Error: El ID de la receta debe ser mayor a 0";
        }
        if (dReceta.findOneById(recetaId) == null) {
            return "Error: La receta con ID " + recetaId + " no existe";
        }
        if (fecha == null || fecha.trim().isEmpty()) {
            return "Error: La fecha de producción es obligatoria";
        }

        // Verificar stock suficiente para todos los insumos de la receta
        List<String[]> ingredientes = dRecetaInsumo.findByReceta(recetaId);
        if (ingredientes.isEmpty()) {
            return "Error: La receta " + recetaId + " no tiene insumos registrados";
        }

        StringBuilder faltantes = new StringBuilder();
        for (String[] ingrediente : ingredientes) {
            double cantidadRequerida = Double.parseDouble(ingrediente[0]) * cantidadProducida;
            int insumoId = Integer.parseInt(ingrediente[1]);
            String[] insumo = dInsumo.findOneById(insumoId);
            if (insumo == null) continue;

            double stockActual = Double.parseDouble(insumo[5]);
            if (cantidadRequerida > stockActual) {
                faltantes.append(String.format("\n  - %s: necesita %.2f, tiene %.2f",
                        insumo[4], cantidadRequerida, stockActual));
            }
        }

        if (faltantes.length() > 0) {
            return "Error: Stock insuficiente para la producción:" + faltantes.toString();
        }

        // Registrar la producción
        String resultado = dProduccion.save(cantidadProducida, fecha.trim(), recetaId);
        if (!resultado.startsWith("Producción registrada")) {
            return resultado;
        }

        // Incrementar el stock del producto terminado en la tabla Producto
        String[] receta = dReceta.findOneById(recetaId);
        if (receta != null) {
            int productoId = Integer.parseInt(receta[2]);
            String[] prod = dProducto.findOneById(productoId);
            if (prod != null) {
                int stockActualProd = (prod.length > 4 && prod[4] != null) ? Integer.parseInt(prod[4]) : 0;
                int nuevoStockProd = stockActualProd + (int) cantidadProducida;
                dProducto.updateStock(productoId, nuevoStockProd);
            }
        }

        // Descontar insumos del inventario (SALIDA)
        for (String[] ingrediente : ingredientes) {
            double cantidadRequerida = Double.parseDouble(ingrediente[0]) * cantidadProducida;
            int insumoId = Integer.parseInt(ingrediente[1]);
            String[] insumo = dInsumo.findOneById(insumoId);
            if (insumo == null) continue;

            double costoUnitario = Double.parseDouble(insumo[1]);
            double subtotal = cantidadRequerida * costoUnitario;
            subtotal = Math.round(subtotal * 100.0) / 100.0;

            // Registrar salida en inventario
            dInventario.save(cantidadRequerida, fecha.trim(), insumoId,
                    "Consumo por producción receta ID " + recetaId, "SALIDA", costoUnitario, subtotal);

            // Actualizar stock del insumo (el costo promedio no varía en salidas)
            double stockActual = Double.parseDouble(insumo[5]);
            double nuevoStock = stockActual - cantidadRequerida;
            double stockMinimo = Double.parseDouble(insumo[6]);
            dInsumo.update(insumoId, costoUnitario, insumo[2], insumo[3],
                    insumo[4], nuevoStock, stockMinimo, insumo[7]);
        }

        return resultado + " | Insumos descontados y stock del producto incrementado correctamente";
    }

    /**
     * Actualiza un registro de producción
     */
    public String actualizarProduccion(int id, double cantidadProducida, String fecha, int recetaId) {
        if (dProduccion.findOneById(id) == null) {
            return "Error: Producción no encontrada con ID: " + id;
        }
        if (cantidadProducida <= 0) {
            return "Error: La cantidad producida debe ser mayor a 0";
        }
        if (dReceta.findOneById(recetaId) == null) {
            return "Error: La receta con ID " + recetaId + " no existe";
        }
        return dProduccion.update(id, cantidadProducida, fecha == null ? "" : fecha.trim(), recetaId);
    }

    /**
     * Elimina un registro de producción
     */
    public String eliminarProduccion(int id) {
        if (dProduccion.findOneById(id) == null) {
            return "Error: Producción no encontrada con ID: " + id;
        }
        return dProduccion.delete(id);
    }

    /**
     * Lista todos los registros de producción
     */
    public List<String[]> listarProducciones() {
        return dProduccion.findAll();
    }

    /**
     * Busca una producción por ID
     */
    public String[] buscarPorId(int id) {
        return dProduccion.findOneById(id);
    }

    /**
     * Lista producciones de una receta específica
     */
    public List<String[]> listarPorReceta(int recetaId) {
        return dProduccion.findByReceta(recetaId);
    }
}
