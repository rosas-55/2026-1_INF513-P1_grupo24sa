package com.tecnoweb.grupo24sa.connection;

import com.tecnoweb.grupo24sa.command.CommandInterpreter;
import com.tecnoweb.grupo24sa.communication.MailVerificationThread;
import com.tecnoweb.grupo24sa.communication.SendEmail;
import com.tecnoweb.grupo24sa.interfaces.IEmailEventListener;
import com.tecnoweb.grupo24sa.utils.Email;
import com.tecnoweb.grupo24sa.utils.ReporteResponse;

import java.util.List;

public class ConnectionCore {
    public SendEmail sendEmail = new SendEmail();

    public static void main(String[] args) {
        MailVerificationThread mail = new MailVerificationThread();
        ConnectionCore core = new ConnectionCore();
        mail.setEmailEventListener(new IEmailEventListener() {

            @Override
            public void onReceiveEmailEvent(List<Email> emails) {
                for (Email email : emails) {
                    System.out.println("Este es el email" + email);
                    String emailFrom = email.getFrom();
                    String emailSubject = email.getSubject();

                    // Usar interpretConGraficos para soportar archivos adjuntos
                    ReporteResponse response = CommandInterpreter.interpretConGraficos(emailSubject);
                    System.out.println(response.getTextoRespuesta());

                    // Enviar email con archivos adjuntos si los hay
                    if (response.tieneAdjuntos()) {
                        System.out.println("[DEBUG] Enviando correo con adjuntos: ");
                        for (java.io.File f : response.getArchivosAdjuntos()) {
                            System.out.println("  - " + f.getAbsolutePath() + " exists=" + f.exists());
                        }
                        core.sendEmail.sendEmail(emailFrom, response.getTextoRespuesta(),
                                response.getArchivosAdjuntos());
                        System.out.println("Correo enviado con " + response.getArchivosAdjuntos().size()
                                + " archivo(s) adjunto(s)");
                    } else {
                        System.out.println("[DEBUG] Enviando correo sin adjuntos");
                        core.sendEmail.sendEmail(emailFrom, response.getTextoRespuesta());
                    }
                    // System.out.println("Saltando Email por que ta todo lleno");
                }
            }
        });

        Thread thread = new Thread(mail);
        thread.setName("Mail Verification Thread");
        thread.start();
    }
}
