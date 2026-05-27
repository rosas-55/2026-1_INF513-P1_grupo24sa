package com.tecnoweb.grupo24sa.command;

import com.tecnoweb.grupo24sa.business.BReporte;

/**
 * Handler para comandos de la entidad 'reporte'
 * CU8 - Reportes y Estadísticas
 */
public class HandleReporte {

    public static String execute(String command, String params) {
        BReporte bReporte = new BReporte();
        try {
            switch (command) {
                case "ventas":           return bReporte.reporteVentas();
                case "cuotasPendientes": return bReporte.reporteCuotasPendientes();
                case "stockBajo":        return bReporte.reporteStockBajo();
                case "produccion":       return bReporte.reporteProduccion();
                case "compras":          return bReporte.reporteCompras();
                case "ingresos":         return bReporte.reporteIngresos();
                default:                 return "Reporte no reconocido: " + command
                        + "\nReportes disponibles: ventas, cuotasPendientes, stockBajo, produccion, compras, ingresos";
            }
        } catch (Exception e) {
            return "Error generando reporte: " + e.getMessage();
        }
    }
}
