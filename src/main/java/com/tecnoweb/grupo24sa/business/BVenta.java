package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DVenta;
import com.tecnoweb.grupo24sa.data.DUsuario;
import com.tecnoweb.grupo24sa.data.DProducto;
import com.tecnoweb.grupo24sa.data.DDetalleVenta;
import com.tecnoweb.grupo24sa.data.DCuota;
import com.tecnoweb.grupo24sa.data.DInsumo;
import com.tecnoweb.grupo24sa.data.DInventario;
import com.tecnoweb.grupo24sa.data.DReceta;
import com.tecnoweb.grupo24sa.data.DRecetaInsumo;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * CU5 - Gestión de Ventas (Contado y Crédito con múltiples cuotas)
 */
public class BVenta {

    private final DVenta dVenta;
    private final DUsuario dUsuario;
    private final DProducto dProducto;
    private final DDetalleVenta dDetalleVenta;
    private final DCuota dCuota;
    private final DInsumo dInsumo;
    private final DInventario dInventario;
    private final DReceta dReceta;
    private final DRecetaInsumo dRecetaInsumo;

    public BVenta() {
        this.dVenta = new DVenta();
        this.dUsuario = new DUsuario();
        this.dProducto = new DProducto();
        this.dDetalleVenta = new DDetalleVenta();
        this.dCuota = new DCuota();
        this.dInsumo = new DInsumo();
        this.dInventario = new DInventario();
        this.dReceta = new DReceta();
        this.dRecetaInsumo = new DRecetaInsumo();
    }

    /**
     * Registra una nueva venta junto con sus detalles y cuotas (si aplica)
     * @param clienteId   ID del cliente (usuario con rol CLIENTE)
     * @param estado      estado de la venta
     * @param fecha       fecha en formato YYYY-MM-DD
     * @param interesMora porcentaje de interés por mora (>= 0)
     * @param nroCuotas   número de cuotas (1 para contado, >= 2 para crédito)
     * @param tipo        tipo de venta: CONTADO o CREDITO
     * @param vendedorId  ID del vendedor (usuario con rol VENDEDOR)
     * @param items       lista de ítems [producto_id, cantidad]
     */
    public String registrarVenta(int clienteId, String estado, String fecha, double interesMora,
                                  int nroCuotas, String tipo, int vendedorId, List<String[]> items) {
        if (clienteId <= 0) {
            return "Error: El ID del cliente debe ser mayor a 0";
        }
        if (dUsuario.findOneById(clienteId) == null) {
            return "Error: El cliente con ID " + clienteId + " no existe";
        }
        if (vendedorId <= 0) {
            return "Error: El ID del vendedor debe ser mayor a 0";
        }
        if (dUsuario.findOneById(vendedorId) == null) {
            return "Error: El vendedor con ID " + vendedorId + " no existe";
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            return "Error: El tipo de venta es obligatorio (CONTADO o CREDITO)";
        }
        String tipoUpper = tipo.trim().toUpperCase();
        if (!tipoUpper.equals("CONTADO") && !tipoUpper.equals("CREDITO")) {
            return "Error: El tipo de venta debe ser CONTADO o CREDITO";
        }
        if (tipoUpper.equals("CONTADO") && nroCuotas != 1) {
            return "Error: Para venta CONTADO el número de cuotas debe ser 1";
        }
        if (tipoUpper.equals("CREDITO") && nroCuotas < 2) {
            return "Error: Para venta CREDITO el número de cuotas debe ser >= 2";
        }
        if (interesMora < 0) {
            interesMora = 0.0;
        }
        if (estado == null || estado.trim().isEmpty()) {
            estado = "PENDIENTE";
        }
        if (fecha == null || fecha.trim().isEmpty()) {
            fecha = LocalDate.now().toString();
        }

        double calculatedTotal = 0.0;

        // Validar todos los productos y cantidades, y calcular el total de la venta
        for (String[] item : items) {
            try {
                int prodId = Integer.parseInt(item[0]);
                int cantidad = Integer.parseInt(item[1]);

                if (prodId <= 0) {
                    return "Error: El ID del producto debe ser mayor a 0";
                }
                String[] prod = dProducto.findOneById(prodId);
                if (prod == null) {
                    return "Error: El producto con ID " + prodId + " no existe";
                }
                if (cantidad <= 0) {
                    return "Error: La cantidad del producto " + prodId + " debe ser mayor a 0";
                }
                double precioVenta = Double.parseDouble(prod[3]);
                calculatedTotal += cantidad * precioVenta;
            } catch (NumberFormatException e) {
                return "Error: Formato numérico inválido en detalles de la venta";
            }
        }

        if (calculatedTotal <= 0) {
            return "Error: El total de la venta debe ser mayor a 0";
        }

        // Registrar la venta de cabecera con el total calculado
        int ventaId = dVenta.save(clienteId, estado.trim(), fecha.trim(), interesMora,
                nroCuotas, tipoUpper, calculatedTotal, vendedorId);
        if (ventaId == -1) {
            return "Error: No se pudo registrar la venta";
        }

        // Registrar cada detalle de venta e implementar descuento automático de stock
        for (String[] item : items) {
            int prodId = Integer.parseInt(item[0]);
            int cantidad = Integer.parseInt(item[1]);
            String[] prod = dProducto.findOneById(prodId);
            double precioVenta = Double.parseDouble(prod[3]);
            double subtotal = cantidad * precioVenta;

            dDetalleVenta.save(cantidad, precioVenta, prodId, subtotal, ventaId);

            if (prod != null) {
                // 1. Descontar del stock del producto terminado
                int stockActualProd = (prod.length > 4 && prod[4] != null) ? Integer.parseInt(prod[4]) : 0;
                int nuevoStockProd = stockActualProd - cantidad;
                dProducto.updateStock(prodId, nuevoStockProd);

                // 2. Descontar insumos asociados (Inventario Permanente)
                List<String[]> recetas = dReceta.findByProducto(prodId);
                if (!recetas.isEmpty()) {
                    // Caso A: Con receta (Plato preparado) -> Omitir descuento en venta
                    // (Los insumos ya fueron descontados previamente al registrar la Producción)
                } else {
                    // Caso B: Sin receta (Producto directo, ej: Gaseosa) -> Buscar por llave foránea insumo_id
                    int insumoIdFk = (prod.length > 5 && prod[5] != null) ? Integer.parseInt(prod[5]) : 0;
                    if (insumoIdFk > 0) {
                        String[] insumo = dInsumo.findOneById(insumoIdFk);
                        if (insumo != null) {
                        int insumoId = Integer.parseInt(insumo[0]);
                        double stockActualInsumo = Double.parseDouble(insumo[5]);
                        double costoUnitario = Double.parseDouble(insumo[1]);
                        double nuevoStockInsumo = stockActualInsumo - cantidad;
                        double stockMinimo = Double.parseDouble(insumo[6]);

                        dInsumo.update(insumoId, costoUnitario, insumo[2], insumo[3],
                                insumo[4], nuevoStockInsumo, stockMinimo, insumo[7]);

                        double subtotalInsumo = cantidad * costoUnitario;
                        subtotalInsumo = Math.round(subtotalInsumo * 100.0) / 100.0;
                        dInventario.save(cantidad, fecha.trim(), insumoId,
                                "Venta ID " + ventaId + " - Producto directo sin receta", "SALIDA", costoUnitario, subtotalInsumo);
                        }
                    }
                }
            }
        }

        // Generar cuotas automáticamente si es venta a crédito
        if (tipoUpper.equals("CREDITO")) {
            double montoCuota = calculatedTotal / nroCuotas;
            montoCuota = Math.round(montoCuota * 100.0) / 100.0;

            LocalDate baseDate;
            try {
                baseDate = LocalDate.parse(fecha.trim());
            } catch (Exception e) {
                baseDate = LocalDate.now();
            }

            for (int i = 1; i <= nroCuotas; i++) {
                LocalDate vencimiento = baseDate.plusMonths(i);
                double actualMonto = montoCuota;
                if (i == nroCuotas) {
                    // Ajuste de redondeo en la última cuota
                    actualMonto = calculatedTotal - (montoCuota * (nroCuotas - 1));
                    actualMonto = Math.round(actualMonto * 100.0) / 100.0;
                }

                dCuota.save("PENDIENTE", null, vencimiento.toString(), (int) interesMora,
                        actualMonto, i, "Cuota " + i + " de " + nroCuotas, ventaId);
            }
        }

        return "Venta registrada exitosamente con ID: " + ventaId + " y sus detalles. Total calculado: " + calculatedTotal + ". Cuotas generadas si aplica.";
    }

    /**
     * Actualiza el estado de una venta
     */
    public String actualizarEstadoVenta(int id, String estado) {
        if (dVenta.findOneById(id) == null) {
            return "Error: Venta no encontrada con ID: " + id;
        }
        if (estado == null || estado.trim().isEmpty()) {
            return "Error: El estado es obligatorio";
        }
        return dVenta.updateEstado(id, estado.trim());
    }

    /**
     * Elimina una venta por ID
     */
    public String eliminarVenta(int id) {
        if (dVenta.findOneById(id) == null) {
            return "Error: Venta no encontrada con ID: " + id;
        }
        return dVenta.delete(id);
    }

    /**
     * Lista todas las ventas
     */
    public List<String[]> listarVentas() {
        return dVenta.findAll();
    }

    /**
     * Busca una venta por ID
     */
    public String[] buscarPorId(int id) {
        return dVenta.findOneById(id);
    }

    /**
     * Lista ventas de un cliente específico
     */
    public List<String[]> listarPorCliente(int clienteId) {
        if (dUsuario.findOneById(clienteId) == null) {
            return null;
        }
        return dVenta.findByCliente(clienteId);
    }
}
