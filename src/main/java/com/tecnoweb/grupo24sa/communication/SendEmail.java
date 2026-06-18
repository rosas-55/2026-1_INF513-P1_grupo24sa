package com.tecnoweb.grupo24sa.communication;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.List;
import java.util.Properties;

public class SendEmail {

    private final static String PROTOCOL = "smtp";
    private final String mail = "grupo24sa@tecnoweb.org.bo";
    private final String username = "grupo24sa";
    private final String password = "grup024grup024*";
    private final String smtpHost = System.getenv().getOrDefault(
            "MAIL_HOST", "mail.tecnoweb.org.bo");
    private final String smtpPort = "25";

    public void sendEmail(String to, String response) {
        sendEmail(to, response, null);
    }

    public void sendEmail(String to, String response, List<File> attachments) {
        Properties props = new Properties();
        props.put("mail.transport.protocol", PROTOCOL);
        // props.put("mail.smtp.auth", "true");
        // props.put("mail.smtp.starttls.enable", "true");
        // props.put("mail.smtp.host", "smtp.gmail.com");
        // props.put("mail.smtp.port", "587");

        props.setProperty("mail.smtp.auth", "false");
        // props.put("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.tls.enable", "true");
        props.setProperty("mail.smtp.host", smtpHost);
        props.setProperty("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Your Subject Here");

            // Si hay archivos adjuntos, usar multipart
            if (attachments != null && !attachments.isEmpty()) {
                System.out.println("[DEBUG] Preparando multipart con " + attachments.size() + " adjuntos");
                Multipart multipart = new MimeMultipart();

                // Parte del texto
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(response);
                multipart.addBodyPart(textPart);

                // Agregar cada archivo adjunto
                for (File file : attachments) {
                    System.out
                            .println("[DEBUG] Adjuntando file: " + file.getAbsolutePath() + " exists=" + file.exists());
                    if (file.exists()) {
                        MimeBodyPart attachmentPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(file);
                        attachmentPart.setDataHandler(new DataHandler(source));
                        attachmentPart.setFileName(file.getName());
                        multipart.addBodyPart(attachmentPart);
                    } else {
                        System.err.println("[WARN] Archivo adjunto no encontrado: " + file.getAbsolutePath());
                    }
                }

                message.setContent(multipart);
            } else {
                message.setText(response);
            }

            Transport.send(message);

            System.out.println("Correo enviado correctamente");

            // Limpiar archivos temporales después de enviar
            if (attachments != null) {
                for (File file : attachments) {
                    if (file.exists() && file.getName().startsWith("chart_")) {
                        file.delete();
                    }
                }
            }

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SendEmail emailSender = new SendEmail();
        emailSender.sendEmail("recipient-email@example.com", "This is the response text");
    }
}