package com.tecnoweb.grupo24sa.tools;

// import com.tecnoweb.grupo24sa.business.BReporte;
import com.tecnoweb.grupo24sa.communication.SendEmail;
import com.tecnoweb.grupo24sa.utils.ReporteResponse;

import java.util.List;

/**
 * Small test utility to generate the dashboard report and optionally send it.
 * Usage:
 * - Run in IDE or with `mvn -q exec:java
 * -Dexec.mainClass="com.tecnoweb.grupo24sa.tools.ReportTestSender"
 * -Dexec.args="[email]`
 * If an email address argument is provided, the tool will attempt to send the
 * report to that address.
 */
public class ReportTestSender {
    public static void main(String[] args) {
        // BReporte bReporte = new BReporte();
        // ReporteResponse resp = bReporte.generarDashboardConGraficos();

        // System.out.println("=== TEXTO DEL DASHBOARD ===");
        // System.out.println(resp.getTextoRespuesta());
        ReporteResponse resp = new ReporteResponse("hola", new java.util.ArrayList<java.io.File>());
        if (resp.tieneAdjuntos()) {
            System.out.println("Adjuntos generados: " + resp.getArchivosAdjuntos().size());
            for (java.io.File f : resp.getArchivosAdjuntos()) {
                System.out.println(" - " + f.getAbsolutePath() + " (exists=" + f.exists() + ")");
            }
        } else {
            System.out.println("No se generaron adjuntos para el dashboard.");
        }

        if (args.length > 0) {
            String to = args[0];
            System.out.println("Intentando enviar a: " + to);
            SendEmail sender = new SendEmail();
            sender.sendEmail(to, resp.getTextoRespuesta(), resp.getArchivosAdjuntos());
            System.out.println(
                    "Envio finalizado (ver logs de SendEmail para detalles). If attachments were sent they are removed after sending.");
        } else {
            System.out.println("No se especificó email destino; solo comprobación local realizada.");
        }
    }
}
