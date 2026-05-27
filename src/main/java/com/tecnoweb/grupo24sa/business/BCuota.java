package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DCuota;
import com.tecnoweb.grupo24sa.data.DVenta;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * CU7 - Gestión de Pagos y Moras (Cuotas de ventas a crédito)
 */
public class BCuota {

    private final DCuota dCuota;
    private final DVenta dVenta;

    public BCuota() {
        this.dCuota = new DCuota();
        this.dVenta = new DVenta();
    }

    /**
     * Registra una nueva cuota para una venta a crédito
     * @param estado           estado de la cuota (PENDIENTE, PAGADO, EN_MORA)
     * @param fechaPago        fecha de pago efectivo (puede ser null si no pagado aún)
     * @param fechaVencimiento fecha límite de pago
     * @param interesMora      porcentaje de mora (>= 0)
     * @param montoPagado      monto pagado (>= 0)
     * @param nroCuota         número de cuota dentro del plan (>= 1)
     * @param planPago         descripción del plan de pago
     * @param ventaId          ID de la venta (debe existir)
     */
    public String registrarCuota(String estado, String fechaPago, String fechaVencimiento,
                                  int interesMora, double montoPagado, int nroCuota,
                                  String planPago, int ventaId) {
        if (dVenta.findOneById(ventaId) == null) {
            return "Error: La venta con ID " + ventaId + " no existe";
        }
        if (nroCuota < 1) {
            return "Error: El número de cuota debe ser mayor a 0";
        }
        if (montoPagado < 0) {
            return "Error: El monto pagado no puede ser negativo";
        }
        if (interesMora < 0) {
            return "Error: El interés de mora no puede ser negativo";
        }
        if (fechaVencimiento == null || fechaVencimiento.trim().isEmpty()) {
            return "Error: La fecha de vencimiento es obligatoria";
        }
        if (estado == null || estado.trim().isEmpty()) {
            return "Error: El estado de la cuota es obligatorio";
        }
        return dCuota.save(estado.trim(), fechaPago, fechaVencimiento.trim(),
                interesMora, montoPagado, nroCuota,
                planPago == null ? "" : planPago.trim(), ventaId);
    }

    /**
     * Registra el pago de una cuota y calcula mora si aplica
     * @param id          ID de la cuota a pagar
     * @param fechaPago   fecha en que se realiza el pago (YYYY-MM-DD)
     * @param montoPagado monto abonado
     */
    public String pagarCuota(int id, String fechaPago, double montoPagado) {
        String[] cuota = dCuota.findOneById(id);
        if (cuota == null) {
            return "Error: Cuota no encontrada con ID: " + id;
        }
        if (montoPagado <= 0) {
            return "Error: El monto pagado debe ser mayor a 0";
        }
        if (fechaPago == null || fechaPago.trim().isEmpty()) {
            return "Error: La fecha de pago es obligatoria";
        }

        // Verificar si hay mora
        String estado = "PAGADO";
        try {
            LocalDate fechaVenc = LocalDate.parse(cuota[3]);
            LocalDate fechaPagoDate = LocalDate.parse(fechaPago.trim());
            if (fechaPagoDate.isAfter(fechaVenc)) {
                estado = "PAGADO_CON_MORA";
            }
        } catch (DateTimeParseException e) {
            // Si la fecha no parsea, se registra como PAGADO
        }

        return dCuota.update(id, estado, fechaPago.trim(), montoPagado);
    }

    /**
     * Elimina una cuota por ID
     */
    public String eliminarCuota(int id) {
        if (dCuota.findOneById(id) == null) {
            return "Error: Cuota no encontrada con ID: " + id;
        }
        return dCuota.delete(id);
    }

    /**
     * Lista todas las cuotas de una venta
     */
    public List<String[]> listarPorVenta(int ventaId) {
        return dCuota.findByVenta(ventaId);
    }

    /**
     * Busca una cuota por ID
     */
    public String[] buscarPorId(int id) {
        return dCuota.findOneById(id);
    }

    /**
     * Calcula la mora de una cuota dado el porcentaje de interés
     * @param id          ID de la cuota
     * @param tasaMora    tasa de mora a aplicar (porcentaje)
     * @return descripción del cálculo de mora
     */
    public String calcularMora(int id, double tasaMora) {
        String[] cuota = dCuota.findOneById(id);
        if (cuota == null) {
            return "Error: Cuota no encontrada con ID: " + id;
        }
        try {
            double montoPendiente = Double.parseDouble(cuota[5]);
            double mora = montoPendiente * (tasaMora / 100.0);
            double totalConMora = montoPendiente + mora;
            return String.format(
                "Cuota N°%s | Monto original: %.2f | Mora (%.1f%%): %.2f | Total con mora: %.2f",
                cuota[6], montoPendiente, tasaMora, mora, totalConMora
            );
        } catch (NumberFormatException e) {
            return "Error: No se pudo calcular la mora: " + e.getMessage();
        }
    }
}
