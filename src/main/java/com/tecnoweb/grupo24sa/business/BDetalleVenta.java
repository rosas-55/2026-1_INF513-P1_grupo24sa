package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DDetalleVenta;
import com.tecnoweb.grupo24sa.data.DVenta;
import com.tecnoweb.grupo24sa.data.DProducto;

import java.util.List;

/**
 * Gestión de Detalle de Ventas
 */
public class BDetalleVenta {

    private final DDetalleVenta dDetalleVenta;
    private final DVenta dVenta;
    private final DProducto dProducto;

    public BDetalleVenta() {
        this.dDetalleVenta = new DDetalleVenta();
        this.dVenta = new DVenta();
        this.dProducto = new DProducto();
    }

    /**
     * Registra un ítem en el detalle de una venta
     * @param cantidad       unidades vendidas (> 0)
     * @param precioUnitario precio por unidad (>= 0)
     * @param productoId     ID del producto (debe existir)
     * @param subTotal       monto parcial (>= 0)
     * @param ventaId        ID de la venta (debe existir)
     */
    public String registrarDetalle(int cantidad, double precioUnitario, int productoId,
                                    double subTotal, int ventaId) {
        if (cantidad <= 0) {
            return "Error: La cantidad debe ser mayor a 0";
        }
        if (dVenta.findOneById(ventaId) == null) {
            return "Error: La venta con ID " + ventaId + " no existe";
        }
        if (dProducto.findOneById(productoId) == null) {
            return "Error: El producto con ID " + productoId + " no existe";
        }
        if (precioUnitario < 0) {
            return "Error: El precio unitario no puede ser negativo";
        }
        if (subTotal < 0) {
            return "Error: El subtotal no puede ser negativo";
        }
        return dDetalleVenta.save(cantidad, precioUnitario, productoId, subTotal, ventaId);
    }

    /**
     * Actualiza un detalle existente
     */
    public String actualizarDetalle(int id, int cantidad, double precioUnitario,
                                     int productoId, double subTotal) {
        if (dDetalleVenta.findOneById(id) == null) {
            return "Error: Detalle no encontrado con ID: " + id;
        }
        if (cantidad <= 0) {
            return "Error: La cantidad debe ser mayor a 0";
        }
        if (precioUnitario < 0) {
            return "Error: El precio unitario no puede ser negativo";
        }
        return dDetalleVenta.update(id, cantidad, precioUnitario, productoId, subTotal);
    }

    /**
     * Elimina un detalle por ID
     */
    public String eliminarDetalle(int id) {
        if (dDetalleVenta.findOneById(id) == null) {
            return "Error: Detalle no encontrado con ID: " + id;
        }
        return dDetalleVenta.delete(id);
    }

    /**
     * Lista todos los ítems de una venta
     */
    public List<String[]> listarPorVenta(int ventaId) {
        return dDetalleVenta.findByVenta(ventaId);
    }

    /**
     * Busca un detalle por ID
     */
    public String[] buscarPorId(int id) {
        return dDetalleVenta.findOneById(id);
    }
}
