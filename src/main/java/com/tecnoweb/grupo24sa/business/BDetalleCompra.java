package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DDetalleCompra;
import com.tecnoweb.grupo24sa.data.DCompra;
import com.tecnoweb.grupo24sa.data.DInsumo;

import java.util.List;

/**
 * Gestión de Detalle de Compras
 */
public class BDetalleCompra {

    private final DDetalleCompra dDetalleCompra;
    private final DCompra dCompra;
    private final DInsumo dInsumo;

    public BDetalleCompra() {
        this.dDetalleCompra = new DDetalleCompra();
        this.dCompra = new DCompra();
        this.dInsumo = new DInsumo();
    }

    /**
     * Registra un ítem en el detalle de una compra
     * @param cantidad       cantidad comprada (> 0)
     * @param compraId       ID de la compra (debe existir)
     * @param insumoId       ID del insumo (debe existir)
     * @param precioUnitario precio por unidad (>= 0)
     * @param subtotal       monto parcial (>= 0)
     */
    public String registrarDetalle(int cantidad, int compraId, int insumoId,
                                    double precioUnitario, double subtotal) {
        if (cantidad <= 0) {
            return "Error: La cantidad debe ser mayor a 0";
        }
        if (dCompra.findOneById(compraId) == null) {
            return "Error: La compra con ID " + compraId + " no existe";
        }
        if (dInsumo.findOneById(insumoId) == null) {
            return "Error: El insumo con ID " + insumoId + " no existe";
        }
        if (precioUnitario < 0) {
            return "Error: El precio unitario no puede ser negativo";
        }
        if (subtotal < 0) {
            return "Error: El subtotal no puede ser negativo";
        }
        return dDetalleCompra.save(cantidad, compraId, insumoId, precioUnitario, subtotal);
    }

    /**
     * Actualiza un detalle existente
     */
    public String actualizarDetalle(int id, int cantidad, double precioUnitario, double subtotal) {
        if (dDetalleCompra.findOneById(id) == null) {
            return "Error: Detalle no encontrado con ID: " + id;
        }
        if (cantidad <= 0) {
            return "Error: La cantidad debe ser mayor a 0";
        }
        if (precioUnitario < 0) {
            return "Error: El precio unitario no puede ser negativo";
        }
        return dDetalleCompra.update(id, cantidad, precioUnitario, subtotal);
    }

    /**
     * Elimina un detalle por ID
     */
    public String eliminarDetalle(int id) {
        if (dDetalleCompra.findOneById(id) == null) {
            return "Error: Detalle no encontrado con ID: " + id;
        }
        return dDetalleCompra.delete(id);
    }

    /**
     * Lista todos los ítems de una compra
     */
    public List<String[]> listarPorCompra(int compraId) {
        return dDetalleCompra.findByCompra(compraId);
    }

    /**
     * Busca un detalle por ID
     */
    public String[] buscarPorId(int id) {
        return dDetalleCompra.findOneById(id);
    }
}
