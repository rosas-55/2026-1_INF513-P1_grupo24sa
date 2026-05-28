package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DCompra;
import com.tecnoweb.grupo24sa.data.DProveedor;
import com.tecnoweb.grupo24sa.data.DInsumo;
import com.tecnoweb.grupo24sa.data.DDetalleCompra;
import com.tecnoweb.grupo24sa.data.DInventario;

import java.util.List;
import java.util.ArrayList;

/**
 * CU3 - Gestión de Compras
 */
public class BCompra {

    private final DCompra dCompra;
    private final DProveedor dProveedor;
    private final DInsumo dInsumo;

    public BCompra() {
        this.dCompra = new DCompra();
        this.dProveedor = new DProveedor();
        this.dInsumo = new DInsumo();
    }

    /**
     * Registra una nueva compra y sus detalles (el total se calcula automáticamente)
     * @param estado      estado de la compra
     * @param fecha       fecha en formato YYYY-MM-DD
     * @param proveedorId ID del proveedor (debe existir)
     * @param items       lista de ítems [insumo_id, cantidad, precio_unitario]
     */
    public String registrarCompra(String estado, String fecha, int proveedorId, List<String[]> items) {
        if (estado == null || estado.trim().isEmpty()) {
            return "Error: El estado de la compra es obligatorio";
        }
        if (fecha == null || fecha.trim().isEmpty()) {
            return "Error: La fecha de la compra es obligatoria";
        }
        if (proveedorId <= 0) {
            return "Error: El ID del proveedor debe ser mayor a 0";
        }
        if (dProveedor.findOneById(proveedorId) == null) {
            return "Error: El proveedor con ID " + proveedorId + " no existe";
        }

        double calculatedTotal = 0.0;

        // Validar todos los insumos antes de realizar cualquier inserción y calcular el total
        for (String[] item : items) {
            try {
                int insumoId = Integer.parseInt(item[0]);
                double cantidad = Double.parseDouble(item[1]);
                double precioUnitario = Double.parseDouble(item[2]);

                if (insumoId <= 0) {
                    return "Error: El ID del insumo debe ser mayor a 0";
                }
                if (dInsumo.findOneById(insumoId) == null) {
                    return "Error: El insumo con ID " + insumoId + " no existe";
                }
                if (cantidad <= 0) {
                    return "Error: La cantidad debe ser mayor a 0";
                }
                if (precioUnitario < 0) {
                    return "Error: El precio unitario no puede ser negativo";
                }
                calculatedTotal += cantidad * precioUnitario;
            } catch (NumberFormatException e) {
                return "Error: Formato numérico inválido en detalles de insumos";
            }
        }

        // Registrar la compra de cabecera con el total calculado
        int compraId = dCompra.save(estado.trim(), fecha.trim(), proveedorId, calculatedTotal);
        if (compraId == -1) {
            return "Error: No se pudo registrar la compra";
        }

        // Registrar cada detalle de la compra y actualizar stock/inventario
        DDetalleCompra dDetalle = new DDetalleCompra();
        DInventario dInventario = new DInventario();

        for (String[] item : items) {
            int insumoId = Integer.parseInt(item[0]);
            int cantidad = Integer.parseInt(item[1]);
            double precioUnitario = Double.parseDouble(item[2]);
            double subtotal = cantidad * precioUnitario;

            // 1. Guardar detalle_compra
            dDetalle.save(cantidad, compraId, insumoId, precioUnitario, subtotal);

            // 2. Registrar movimiento en inventario (INGRESO)
            dInventario.save(cantidad, fecha.trim(), insumoId, "Compra ID " + compraId, "INGRESO", precioUnitario, subtotal);

            // 3. Incrementar stock del insumo y recalcular costo promedio ponderado
            String[] insumo = dInsumo.findOneById(insumoId);
            if (insumo != null) {
                double stockActual = Double.parseDouble(insumo[5]);
                double costoUnitarioActual = Double.parseDouble(insumo[1]);
                double stockMinimo = Double.parseDouble(insumo[6]);

                double nuevoStock = stockActual + cantidad;
                double nuevoCostoUnitario = costoUnitarioActual;
                if (nuevoStock > 0) {
                    nuevoCostoUnitario = ((stockActual * costoUnitarioActual) + (cantidad * precioUnitario)) / nuevoStock;
                    nuevoCostoUnitario = Math.round(nuevoCostoUnitario * 100.0) / 100.0;
                }

                dInsumo.update(insumoId, nuevoCostoUnitario, insumo[2], insumo[3],
                        insumo[4], nuevoStock, stockMinimo, insumo[7]);
            }
        }

        return "Compra registrada exitosamente con ID: " + compraId + " y sus detalles asociados. Stock actualizado.";
    }

    /**
     * Actualiza el estado de una compra
     */
    public String actualizarEstadoCompra(int id, String estado) {
        if (dCompra.findOneById(id) == null) {
            return "Error: Compra no encontrada con ID: " + id;
        }
        if (estado == null || estado.trim().isEmpty()) {
            return "Error: El estado es obligatorio";
        }
        return dCompra.updateEstado(id, estado.trim());
    }

    /**
     * Elimina una compra por ID
     */
    public String eliminarCompra(int id) {
        if (dCompra.findOneById(id) == null) {
            return "Error: Compra no encontrada con ID: " + id;
        }
        return dCompra.delete(id);
    }

    /**
     * Lista todas las compras
     */
    public List<String[]> listarCompras() {
        return dCompra.findAll();
    }

    /**
     * Busca una compra por ID
     */
    public String[] buscarPorId(int id) {
        return dCompra.findOneById(id);
    }

    /**
     * Lista compras filtradas por proveedor
     */
    public List<String[]> listarPorProveedor(int proveedorId) {
        if (dProveedor.findOneById(proveedorId) == null) {
            return null;
        }
        return dCompra.findByProveedor(proveedorId);
    }
}
