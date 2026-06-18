package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DCuota;
import com.tecnoweb.grupo24sa.data.DVenta;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
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
     * Obtiene una lista cruda de las cuotas pendientes o en mora de un cliente
     */
    public List<String[]> obtenerCuotasPendientes(int clienteId) {
        List<String[]> pendientes = new java.util.ArrayList<>();
        List<String[]> lista = dCuota.findByCliente(clienteId);
        for (String[] c : lista) {
            String estado = c[1];
            if (estado.equalsIgnoreCase("PENDIENTE") || estado.equalsIgnoreCase("EN_MORA")) {
                pendientes.add(c);
            }
        }
        return pendientes;
    }

    /**
     * Lista todas las cuotas de un cliente y muestra la morosidad si corresponde
     */
    public String listarPorCliente(int clienteId) {
        List<String[]> lista = dCuota.findByCliente(clienteId);
        if (lista.isEmpty()) {
            return "No hay cuotas registradas para el cliente " + clienteId;
        }

        StringBuilder sb = new StringBuilder("=== CUOTAS DEL CLIENTE " + clienteId + " ===\n");
        LocalDate hoy = LocalDate.now();

        for (String[] c : lista) {
            int id = Integer.parseInt(c[0]);
            String estado = c[1];
            String fechaVencimientoStr = c[3];
            int interesMora = Integer.parseInt(c[4]);
            double montoPagado = Double.parseDouble(c[5]); // Este es el monto de la cuota a pagar

            sb.append("N°").append(c[6])
              .append(" | ID:").append(id)
              .append(" | Vence: ").append(fechaVencimientoStr)
              .append(" | Hoy: ").append(hoy.toString());

            if (estado.equalsIgnoreCase("PENDIENTE") || estado.equalsIgnoreCase("EN_MORA")) {
                try {
                    LocalDate fechaVenc = LocalDate.parse(fechaVencimientoStr);
                    if (hoy.isAfter(fechaVenc)) {
                        long diasAtrasados = ChronoUnit.DAYS.between(fechaVenc, hoy);
                        // interesMora = porcentaje anual (ej: 30 = 30% anual)
                        // Fórmula: monto * (tasa_anual/100/365) * días_atraso
                        double penalizacion = montoPagado * (interesMora / 100.0 / 365.0) * diasAtrasados;
                        double totalAPagar = montoPagado + penalizacion;

                        sb.append("\n  -> ¡ATRASADO! CuotaMensual = ").append(String.format("%.2f", montoPagado))
                          .append(" , Interes Moratorio (Penalizacion) = ").append(String.format("%.2f", penalizacion))
                          .append(" , Total a pagar = ").append(String.format("%.2f", totalAPagar));
                    } else {
                         sb.append(" | Estado: ").append(estado).append(" | Monto a pagar: ").append(String.format("%.2f", montoPagado));
                    }
                } catch (DateTimeParseException e) {
                     sb.append(" | Estado: ").append(estado).append(" | Monto a pagar: ").append(String.format("%.2f", montoPagado));
                }
            } else {
                sb.append(" | Estado: ").append(estado).append(" | Monto: ").append(String.format("%.2f", montoPagado));
            }
            sb.append("\n");
        }
        return sb.toString();
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
