package com.tecnoweb.grupo24sa.utils;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class ChartGenerator {

    private static File generateNoDataChart(String title, String filePrefix) {
        try {
            CategoryChart chart = new CategoryChartBuilder()
                    .width(600)
                    .height(400)
                    .title(title)
                    .xAxisTitle("")
                    .yAxisTitle("")
                    .build();
            chart.getStyler().setLegendVisible(false);
            List<String> cats = Collections.singletonList("No data");
            List<Number> vals = Collections.singletonList(0);
            chart.addSeries("", cats, vals);
            String fileName = filePrefix + "_nodata_" + System.currentTimeMillis() + ".png";
            // Guardar como imagen
            BitmapEncoder.saveBitmap(chart, fileName, BitmapEncoder.BitmapFormat.PNG);
            return new File(fileName);
        } catch (IOException e) {
            System.err.println("Error al generar placeholder chart: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera un gráfico de barras para ventas por período
     */
    public static File generateVentasChart(Map<String, BigDecimal> datos, String titulo) {
        try {
            if (datos == null || datos.isEmpty()) {
                return generateNoDataChart(titulo, "chart_ventas");
            }

            CategoryChart chart = new CategoryChartBuilder()
                    .width(800)
                    .height(600)
                    .title(titulo)
                    .xAxisTitle("Período")
                    .yAxisTitle("Monto (Bs.)")
                    .build();

            // Personalizar estilo
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);

            // Agregar datos
            List<String> categorias = new ArrayList<>(datos.keySet());
            List<Number> valores = new ArrayList<>();
            for (String categoria : categorias) {
                valores.add(datos.get(categoria).doubleValue());
            }

            chart.addSeries("Ventas", categorias, valores);

            // Guardar como imagen temporal
            String fileName = "chart_ventas_" + System.currentTimeMillis() + ".png";
            BitmapEncoder.saveBitmap(chart, fileName, BitmapEncoder.BitmapFormat.PNG);
            return new File(fileName);
        } catch (IOException e) {
            System.err.println("Error al generar gráfico de ventas: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera un gráfico de pastel para distribución de pagos por método
     */
    public static File generatePagosPorMetodoChart(Map<String, BigDecimal> datos, String titulo) {
        try {
            if (datos == null || datos.isEmpty()) {
                return generateNoDataChart(titulo, "chart_pagos");
            }
            PieChart chart = new PieChartBuilder()
                    .width(800)
                    .height(600)
                    .title(titulo)
                    .build();

            // Agregar datos
            for (Map.Entry<String, BigDecimal> entry : datos.entrySet()) {
                chart.addSeries(entry.getKey(), entry.getValue().doubleValue());
            }

            // Guardar como imagen temporal
            String fileName = "chart_pagos_" + System.currentTimeMillis() + ".png";
            BitmapEncoder.saveBitmap(chart, fileName, BitmapEncoder.BitmapFormat.PNG);
            return new File(fileName);
        } catch (IOException e) {
            System.err.println("Error al generar gráfico de pagos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera un gráfico de barras para destinos más vendidos
     */
    public static File generateDestinosMasVendidosChart(Map<String, Integer> datos, String titulo) {
        try {
            if (datos == null || datos.isEmpty()) {
                return generateNoDataChart(titulo, "chart_destinos");
            }
            CategoryChart chart = new CategoryChartBuilder()
                    .width(800)
                    .height(600)
                    .title(titulo)
                    .xAxisTitle("Destino")
                    .yAxisTitle("Cantidad de Ventas")
                    .build();

            // Personalizar estilo
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);

            // Agregar datos
            List<String> categorias = new ArrayList<>(datos.keySet());
            List<Number> valores = new ArrayList<>();
            for (String categoria : categorias) {
                valores.add(datos.get(categoria));
            }

            chart.addSeries("Ventas", categorias, valores);

            // Guardar como imagen temporal
            String fileName = "chart_destinos_" + System.currentTimeMillis() + ".png";
            BitmapEncoder.saveBitmap(chart, fileName, BitmapEncoder.BitmapFormat.PNG);
            return new File(fileName);
        } catch (IOException e) {
            System.err.println("Error al generar gráfico de destinos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera un gráfico de línea para ingresos mensuales
     */
    public static File generateIngresosMensualesChart(Map<String, BigDecimal> datos, String titulo) {
        try {
            if (datos == null || datos.isEmpty()) {
                return generateNoDataChart(titulo, "chart_ingresos");
            }
            XYChart chart = new XYChartBuilder()
                    .width(800)
                    .height(600)
                    .title(titulo)
                    .xAxisTitle("Día")
                    .yAxisTitle("Ingresos (Bs.)")
                    .build();

            // Personalizar estilo
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);

            // Agregar datos
            List<Integer> dias = new ArrayList<>();
            List<Double> valores = new ArrayList<>();

            for (Map.Entry<String, BigDecimal> entry : datos.entrySet()) {
                try {
                    dias.add(Integer.parseInt(entry.getKey()));
                    valores.add(entry.getValue().doubleValue());
                } catch (NumberFormatException e) {
                    // Ignorar entradas que no sean números
                }
            }

            chart.addSeries("Ingresos", dias, valores);

            // Guardar como imagen temporal
            String fileName = "chart_ingresos_" + System.currentTimeMillis() + ".png";
            BitmapEncoder.saveBitmap(chart, fileName, BitmapEncoder.BitmapFormat.PNG);
            return new File(fileName);
        } catch (IOException e) {
            System.err.println("Error al generar gráfico de ingresos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera un gráfico de barras para reservas por estado
     */
    public static File generateReservasPorEstadoChart(Map<String, Integer> datos, String titulo) {
        try {
            if (datos == null || datos.isEmpty()) {
                return generateNoDataChart(titulo, "chart_reservas");
            }
            CategoryChart chart = new CategoryChartBuilder()
                    .width(800)
                    .height(600)
                    .title(titulo)
                    .xAxisTitle("Estado")
                    .yAxisTitle("Cantidad")
                    .build();

            // Personalizar estilo
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);

            // Agregar datos
            List<String> categorias = new ArrayList<>(datos.keySet());
            List<Number> valores = new ArrayList<>();
            for (String categoria : categorias) {
                valores.add(datos.get(categoria));
            }

            chart.addSeries("Reservas", categorias, valores);

            // Guardar como imagen temporal
            String fileName = "chart_reservas_" + System.currentTimeMillis() + ".png";
            BitmapEncoder.saveBitmap(chart, fileName, BitmapEncoder.BitmapFormat.PNG);
            return new File(fileName);
        } catch (IOException e) {
            System.err.println("Error al generar gráfico de reservas: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera un gráfico de barras genérico
     */
    public static File generateBarChart(Map<String, Number> datos, String titulo, String xLabel, String yLabel) {
        try {
            if (datos == null || datos.isEmpty()) {
                return generateNoDataChart(titulo, "chart_bar");
            }
            CategoryChart chart = new CategoryChartBuilder()
                    .width(800)
                    .height(600)
                    .title(titulo)
                    .xAxisTitle(xLabel)
                    .yAxisTitle(yLabel)
                    .build();

            // Personalizar estilo
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);

            // Agregar datos
            List<String> categorias = new ArrayList<>(datos.keySet());
            List<Number> valores = new ArrayList<>(datos.values());

            chart.addSeries("Datos", categorias, valores);

            // Guardar como imagen temporal
            String fileName = "chart_bar_" + System.currentTimeMillis() + ".png";
            BitmapEncoder.saveBitmap(chart, fileName, BitmapEncoder.BitmapFormat.PNG);
            return new File(fileName);
        } catch (IOException e) {
            System.err.println("Error al generar gráfico de barras: " + e.getMessage());
            return null;
        }
    }
}
