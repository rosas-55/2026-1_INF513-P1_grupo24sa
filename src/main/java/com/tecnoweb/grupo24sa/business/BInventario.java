package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DInventario;
import com.tecnoweb.grupo24sa.data.DInsumo;

import java.util.Arrays;
import java.util.List;

/**
 * CU6 - Gestión de Inventario (Ingresos, Salidas, Técnicas de inventario y costo)
 */
public class BInventario {

    private static final List<String> TIPOS_MOVIMIENTO = Arrays.asList("INGRESO", "SALIDA");
    private static final List<String> METODOS_VALIDOS  = Arrays.asList("FIFO", "LIFO", "PROMEDIO");

    private final DInventario dInventario;
    private final DInsumo dInsumo;

    public BInventario() {
        this.dInventario = new DInventario();
        this.dInsumo = new DInsumo();
    }

    /**
     * Registra un movimiento de inventario y actualiza el stock del insumo
     * @param cantidad         unidades del movimiento (> 0)
     * @param fecha            fecha del movimiento (YYYY-MM-DD)
     * @param insumoId         ID del insumo (debe existir)
     * @param metodoInventario técnica: FIFO, LIFO, PROMEDIO (o texto libre)
     * @param observacion      observación del movimiento
     * @param tipoMovimiento   INGRESO o SALIDA
     */
    public String registrarMovimiento(double cantidad, String fecha, int insumoId,
                                       double costoUnitario, String observacion,
                                       String tipoMovimiento) {
        if (cantidad <= 0) {
            return "Error: La cantidad debe ser mayor a 0";
        }
        if (insumoId <= 0) {
            return "Error: El ID del insumo debe ser mayor a 0";
        }
        String[] insumo = dInsumo.findOneById(insumoId);
        if (insumo == null) {
            return "Error: El insumo con ID " + insumoId + " no existe";
        }
        if (tipoMovimiento == null || tipoMovimiento.trim().isEmpty()) {
            return "Error: El tipo de movimiento es obligatorio (INGRESO o SALIDA)";
        }
        String tipoUpper = tipoMovimiento.trim().toUpperCase();
        if (!TIPOS_MOVIMIENTO.contains(tipoUpper)) {
            return "Error: Tipo de movimiento inválido. Use INGRESO o SALIDA";
        }
        if (fecha == null || fecha.trim().isEmpty()) {
            return "Error: La fecha del movimiento es obligatoria";
        }

        double stockActual = Double.parseDouble(insumo[5]);

        // Validar stock suficiente para SALIDA
        if (tipoUpper.equals("SALIDA")) {
            if (cantidad > stockActual) {
                return String.format("Error: Stock insuficiente. Stock actual: %.2f, Cantidad solicitada: %.2f",
                        stockActual, cantidad);
            }
        }

        // Definir costo unitario del movimiento
        double costoMovimiento = costoUnitario;
        if (tipoUpper.equals("SALIDA") || costoMovimiento <= 0) {
            costoMovimiento = Double.parseDouble(insumo[1]);
        }

        double valorTotal = cantidad * costoMovimiento;
        valorTotal = Math.round(valorTotal * 100.0) / 100.0;

        // Registrar el movimiento
        String resultado = dInventario.save(cantidad, fecha.trim(), insumoId,
                observacion == null ? "" : observacion.trim(), tipoUpper, costoMovimiento, valorTotal);

        if (!resultado.startsWith("Movimiento")) {
            return resultado;
        }

        // Actualizar stock y costo promedio ponderado del insumo
        double nuevoStock = tipoUpper.equals("INGRESO")
                ? stockActual + cantidad
                : stockActual - cantidad;

        double nuevoCostoUnitario = Double.parseDouble(insumo[1]);
        if (tipoUpper.equals("INGRESO")) {
            if (nuevoStock > 0) {
                nuevoCostoUnitario = ((stockActual * nuevoCostoUnitario) + (cantidad * costoMovimiento)) / nuevoStock;
                nuevoCostoUnitario = Math.round(nuevoCostoUnitario * 100.0) / 100.0;
            }
        }

        double stockMinimo   = Double.parseDouble(insumo[6]);
        dInsumo.update(insumoId, nuevoCostoUnitario, insumo[2], insumo[3],
                insumo[4], nuevoStock, stockMinimo, insumo[7]);

        String avisoStock = "";
        if (nuevoStock <= stockMinimo) {
            avisoStock = "\n⚠ AVISO: El stock actual (" + String.format("%.2f", nuevoStock)
                    + ") está por debajo del mínimo (" + String.format("%.2f", stockMinimo) + ")";
        }

        return resultado + " | Stock actualizado: " + String.format("%.2f", nuevoStock) + avisoStock;
    }

    /**
     * Actualiza observación y cantidad de un movimiento
     */
    public String actualizarMovimiento(int id, double cantidad, String observacion) {
        if (dInventario.findOneById(id) == null) {
            return "Error: Registro de inventario no encontrado con ID: " + id;
        }
        if (cantidad <= 0) {
            return "Error: La cantidad debe ser mayor a 0";
        }
        return dInventario.update(id, cantidad, observacion == null ? "" : observacion.trim());
    }

    /**
     * Elimina un movimiento de inventario
     */
    public String eliminarMovimiento(int id) {
        if (dInventario.findOneById(id) == null) {
            return "Error: Registro no encontrado con ID: " + id;
        }
        return dInventario.delete(id);
    }

    /**
     * Lista todos los movimientos de inventario
     */
    public List<String[]> listarMovimientos() {
        return dInventario.findAll();
    }

    /**
     * Busca un movimiento por ID
     */
    public String[] buscarPorId(int id) {
        return dInventario.findOneById(id);
    }

    /**
     * Lista movimientos de un insumo específico
     */
    public List<String[]> listarPorInsumo(int insumoId) {
        return dInventario.findByInsumo(insumoId);
    }
}
