package com.tecnoweb.grupo24sa.connection;

import com.tecnoweb.grupo24sa.command.CommandInterpreter;
import com.tecnoweb.grupo24sa.communication.ImapIdleThread;
import com.tecnoweb.grupo24sa.communication.MailVerificationThread;
import com.tecnoweb.grupo24sa.communication.SendEmail;
import com.tecnoweb.grupo24sa.interfaces.IEmailEventListener;
import com.tecnoweb.grupo24sa.utils.Email;
import com.tecnoweb.grupo24sa.utils.ReporteResponse;

import java.util.List;

public class ConnectionCore {
    public SendEmail sendEmail = new SendEmail();

    // ========================================================
    // TOGGLE PARA CAMBIAR ENTRE IMAP IDLE Y POP3 MANUAL
    // true = Usa IMAP IDLE (Push Email instantáneo y eficiente)
    // false = Usa POP3 tradicional (Sondeo manual cada 15 seg)
    // ========================================================
    public static final boolean USE_IMAP_IDLE = false;

    public static void main(String[] args) {
        ConnectionCore core = new ConnectionCore();

        // El Listener es exactamente el mismo para ambos protocolos
        IEmailEventListener listener = new IEmailEventListener() {
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
                }
            }
        };

        if (USE_IMAP_IDLE) {
            System.out.println(">>> Iniciando sistema en modo IMAP IDLE (Push Email) <<<");
            ImapIdleThread imapMail = new ImapIdleThread();
            imapMail.setEmailEventListener(listener);
            Thread thread = new Thread(imapMail);
            thread.setName("Imap-Idle-Thread");
            thread.start();
        } else {
            System.out.println(">>> Iniciando sistema en modo POP3 Tradicional (Sondeo manual) <<<");
            MailVerificationThread pop3Mail = new MailVerificationThread();
            pop3Mail.setEmailEventListener(listener);
            Thread thread = new Thread(pop3Mail);
            thread.setName("Pop3-Polling-Thread");
            thread.start();
        }
    }
}
