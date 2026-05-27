package com.tecnoweb.grupo24sa.business;

import com.tecnoweb.grupo24sa.data.DVenta;
import com.tecnoweb.grupo24sa.data.DCuota;
import com.tecnoweb.grupo24sa.data.DInsumo;
import com.tecnoweb.grupo24sa.data.DProduccion;
import com.tecnoweb.grupo24sa.data.DCompra;
import com.tecnoweb.grupo24sa.data.DProducto;

import java.util.List;

/**
 * CU8 - Reportes y Estadísticas del sistema
 */
public class BReporte {

    private final DVenta dVenta;
    private final DCuota dCuota;
    private final DInsumo dInsumo;
    private final DProduccion dProduccion;
    private final DCompra dCompra;
    private final DProducto dProducto;

    public BReporte() {
        this.dVenta = new DVenta();
        this.dCuota = new DCuota();
        this.dInsumo = new DInsumo();
        this.dProduccion = new DProduccion();
        this.dCompra = new DCompra();
        this.dProducto = new DProducto();
    }

    /**
     * Reporte de ventas: total de ventas, desglose por tipo y monto acumulado
     */
    public String reporteVentas() {
        List<String[]> ventas = dVenta.findAll();
        if (ventas.isEmpty()) {
            return "No hay ventas registradas en el sistema";
        }

        int totalContado = 0, totalCredito = 0;
        double montoContado = 0, montoCredito = 0;

        for (String[] v : ventas) {
            String tipo = v[6];
            double total = Double.parseDouble(v[7]);
            if ("CONTADO".equalsIgnoreCase(tipo)) {
                totalContado++;
                montoContado += total;
            } else if ("CREDITO".equalsIgnoreCase(tipo)) {
                totalCredito++;
                montoCredito += total;
            }
        }

        StringBuilder sb = new StringBuilder("=== REPORTE DE VENTAS ===\n");
        sb.append("Total ventas: ").append(ventas.size()).append("\n");
        sb.append(String.format("Ventas Contado: %d | Monto: %.2f\n", totalContado, montoContado));
        sb.append(String.format("Ventas Crédito: %d | Monto: %.2f\n", totalCredito, montoCredito));
        sb.append(String.format("Ingresos Totales: %.2f\n", montoContado + montoCredito));
        sb.append("\n--- Detalle ---\n");
        for (String[] v : ventas) {
            sb.append(String.format("ID:%s | Cliente:%s | Tipo:%s | Total:%.2f | Estado:%s | Fecha:%s\n",
                    v[0], v[1], v[6], Double.parseDouble(v[7]), v[2], v[3]));
        }
        return sb.toString();
    }

    /**
     * Reporte de cuotas pendientes (estado PENDIENTE o EN_MORA)
     */
    public String reporteCuotasPendientes() {
        List<String[]> ventas = dVenta.findAll();
        StringBuilder sb = new StringBuilder("=== CUOTAS PENDIENTES Y EN MORA ===\n");
        int totalPendientes = 0;
        double totalPendiente = 0;

        for (String[] v : ventas) {
            if (!"CREDITO".equalsIgnoreCase(v[6])) continue;
            List<String[]> cuotas = dCuota.findByVenta(Integer.parseInt(v[0]));
            for (String[] c : cuotas) {
                String estado = c[1];
                if ("PENDIENTE".equalsIgnoreCase(estado) || "EN_MORA".equalsIgnoreCase(estado)
                        || "PAGADO_CON_MORA".equalsIgnoreCase(estado)) {
                    totalPendientes++;
                    totalPendiente += Double.parseDouble(c[5]);
                    sb.append(String.format(
                            "VentaID:%s | Cuota N°%s | Estado:%s | Vence:%s | Monto:%.2f\n",
                            v[0], c[6], c[1], c[3], Double.parseDouble(c[5])));
                }
            }
        }

        if (totalPendientes == 0) {
            return "No hay cuotas pendientes ni en mora";
        }
        sb.insert(sb.indexOf("\n") + 1,
                "Total pendientes: " + totalPendientes + " | Monto pendiente: " +
                        String.format("%.2f", totalPendiente) + "\n");
        return sb.toString();
    }

    /**
     * Reporte de insumos con stock bajo el mínimo
     */
    public String reporteStockBajo() {
        List<String[]> insumos = dInsumo.findStockBajoMinimo();
        if (insumos.isEmpty()) {
            return "Todos los insumos tienen stock suficiente";
        }

        StringBuilder sb = new StringBuilder("=== INSUMOS CON STOCK BAJO MÍNIMO ===\n");
        sb.append(String.format("Total insumos críticos: %d\n\n", insumos.size()));
        for (String[] i : insumos) {
            sb.append(String.format("ID:%s | %s | Stock actual: %s %s | Mínimo: %s %s\n",
                    i[0], i[1], i[2], i[4], i[3], i[4]));
        }
        return sb.toString();
    }

    /**
     * Reporte de producción: cuánto se ha producido por receta
     */
    public String reporteProduccion() {
        List<String[]> producciones = dProduccion.findAll();
        if (producciones.isEmpty()) {
            return "No hay registros de producción";
        }

        StringBuilder sb = new StringBuilder("=== REPORTE DE PRODUCCIÓN ===\n");
        sb.append(String.format("Total registros: %d\n\n", producciones.size()));
        double totalUnidades = 0;
        for (String[] p : producciones) {
            double cant = Double.parseDouble(p[1]);
            totalUnidades += cant;
            sb.append(String.format("ID:%s | RecetaID:%s | Cantidad:%.2f | Fecha:%s\n",
                    p[0], p[3], cant, p[2]));
        }
        sb.append(String.format("\nTotal unidades producidas: %.2f", totalUnidades));
        return sb.toString();
    }

    /**
     * Reporte de compras: resumen general
     */
    public String reporteCompras() {
        List<String[]> compras = dCompra.findAll();
        if (compras.isEmpty()) {
            return "No hay compras registradas";
        }

        StringBuilder sb = new StringBuilder("=== REPORTE DE COMPRAS ===\n");
        double totalGastado = 0;
        for (String[] c : compras) {
            double total = Double.parseDouble(c[4]);
            totalGastado += total;
            sb.append(String.format("ID:%s | ProveedorID:%s | Estado:%s | Total:%.2f | Fecha:%s\n",
                    c[0], c[3], c[1], total, c[2]));
        }
        sb.append(String.format("\nTotal compras: %d | Gasto total: %.2f", compras.size(), totalGastado));
        return sb.toString();
    }

    /**
     * Reporte de ingresos totales (suma de todas las ventas)
     */
    public String reporteIngresos() {
        List<String[]> ventas = dVenta.findAll();
        double totalIngresos = 0;
        double ingresosContado = 0;
        double ingresosCredito = 0;

        for (String[] v : ventas) {
            double total = Double.parseDouble(v[7]);
            totalIngresos += total;
            if ("CONTADO".equalsIgnoreCase(v[6])) {
                ingresosContado += total;
            } else {
                ingresosCredito += total;
            }
        }

        // Calcular monto cobrado vs pendiente en cuotas
        double montoCobrado = 0;
        for (String[] v : ventas) {
            if (!"CREDITO".equalsIgnoreCase(v[6])) continue;
            List<String[]> cuotas = dCuota.findByVenta(Integer.parseInt(v[0]));
            for (String[] c : cuotas) {
                if ("PAGADO".equalsIgnoreCase(c[1]) || "PAGADO_CON_MORA".equalsIgnoreCase(c[1])) {
                    montoCobrado += Double.parseDouble(c[5]);
                }
            }
        }

        StringBuilder sb = new StringBuilder("=== REPORTE DE INGRESOS ===\n");
        sb.append(String.format("Total ventas registradas: %d\n", ventas.size()));
        sb.append(String.format("Ingresos Contado:  %.2f\n", ingresosContado));
        sb.append(String.format("Ingresos Crédito:  %.2f\n", ingresosCredito));
        sb.append(String.format("Cuotas cobradas:   %.2f\n", ingresosContado + montoCobrado));
        sb.append(String.format("TOTAL INGRESOS:    %.2f\n", totalIngresos));
        return sb.toString();
    }
}
