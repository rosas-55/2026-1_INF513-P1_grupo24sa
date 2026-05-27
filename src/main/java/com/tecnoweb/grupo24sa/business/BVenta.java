package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DVenta;
import com.tecnoweb.grupo24sa.data.DUsuario;
import com.tecnoweb.grupo24sa.data.DProducto;
import com.tecnoweb.grupo24sa.data.DDetalleVenta;
import com.tecnoweb.grupo24sa.data.DCuota;

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

    public BVenta() {
        this.dVenta = new DVenta();
        this.dUsuario = new DUsuario();
        this.dProducto = new DProducto();
        this.dDetalleVenta = new DDetalleVenta();
        this.dCuota = new DCuota();
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
            return "Error: El interés de mora no puede ser negativo";
        }
        if (estado == null || estado.trim().isEmpty()) {
            return "Error: El estado de la venta es obligatorio";
        }
        if (fecha == null || fecha.trim().isEmpty()) {
            return "Error: La fecha de la venta es obligatoria";
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

        // Registrar cada detalle de venta
        for (String[] item : items) {
            int prodId = Integer.parseInt(item[0]);
            int cantidad = Integer.parseInt(item[1]);
            String[] prod = dProducto.findOneById(prodId);
            double precioVenta = Double.parseDouble(prod[3]);
            double subtotal = cantidad * precioVenta;

            dDetalleVenta.save(cantidad, precioVenta, prodId, subtotal, ventaId);
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
