package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BCuota;

import java.util.List;

/**
 * Handler para comandos de la entidad 'cuota'
 */
public class HandleCuota {

    public static String execute(String command, String params) {
        BCuota bCuota = new BCuota();
        try {
            switch (command) {
                case "registrar":    return registrar(bCuota, params);
                case "pagar":        return pagar(bCuota, params);
                case "eliminar":     return eliminar(bCuota, params);
                case "listarPorVenta": return listarPorVenta(bCuota, params);
                case "buscar":       return buscar(bCuota, params);
                default:             return "Comando no implementado: " + command;
            }
        } catch (NumberFormatException e) {
            return "Error: Parámetro numérico inválido - " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * registrar(estado, fecha_pago, fecha_vencimiento, interes_mora, monto_pagado,
     *           nro_cuota, plan_pago, venta_id)
     * Escribir 'null' en fecha_pago si la cuota aún no ha sido pagada.
     */
    private static String registrar(BCuota b, String params) {
        String[] p = params.split(",");
        if (p.length < 8) return "Error: Uso: registrar(estado,fecha_pago,fecha_vencimiento,"
                + "interes_mora,monto_pagado,nro_cuota,plan_pago,venta_id)";
        String fechaPago = p[1].trim().equalsIgnoreCase("null") ? null : p[1].trim();
        return b.registrarCuota(p[0].trim(), fechaPago, p[2].trim(),
                Integer.parseInt(p[3].trim()), Double.parseDouble(p[4].trim()),
                Integer.parseInt(p[5].trim()), p[6].trim(), Integer.parseInt(p[7].trim()));
    }

    /** pagar(id, fecha_pago, monto_pagado) */
    private static String pagar(BCuota b, String params) {
        String[] p = params.split(",");
        if (p.length < 3) return "Error: Uso: pagar(id,fecha_pago,monto_pagado)";
        return b.pagarCuota(Integer.parseInt(p[0].trim()), p[1].trim(),
                Double.parseDouble(p[2].trim()));
    }

    /** eliminar(id) */
    private static String eliminar(BCuota b, String params) {
        return b.eliminarCuota(Integer.parseInt(params.trim()));
    }

    /** listarPorVenta(venta_id) */
    private static String listarPorVenta(BCuota b, String params) {
        int ventaId = Integer.parseInt(params.trim());
        List<String[]> lista = b.listarPorVenta(ventaId);
        if (lista.isEmpty()) return "No hay cuotas para la venta " + ventaId;
        StringBuilder sb = new StringBuilder("=== CUOTAS DE VENTA " + ventaId + " ===\n");
        for (String[] c : lista) {
            sb.append("N°").append(c[6])
              .append(" | ID:").append(c[0])
              .append(" | Estado:").append(c[1])
              .append(" | Vence:").append(c[3])
              .append(" | Pagado:").append(c[5])
              .append(" | PlanPago:").append(c[7])
              .append("\n");
        }
        return sb.toString();
    }

    /** buscar(id) */
    private static String buscar(BCuota b, String params) {
        String[] c = b.buscarPorId(Integer.parseInt(params.trim()));
        if (c == null) return "Cuota no encontrada";
        return "ID: " + c[0] + "\nN° Cuota: " + c[6]
                + "\nEstado: " + c[1] + "\nFecha pago: " + c[2]
                + "\nFecha vencimiento: " + c[3] + "\nMonto pagado: " + c[5]
                + "\nInterés mora: " + c[4] + "%" + "\nPlan pago: " + c[7]
                + "\nVentaID: " + c[8];
    }
}
