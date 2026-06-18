package com.tecnoweb.grupo24sa.communication;

import com.tecnoweb.grupo24sa.interfaces.IEmailEventListener;
import com.tecnoweb.grupo24sa.utils.Email;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import com.sun.mail.imap.IMAPFolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ImapIdleThread implements Runnable {

    private final static String HOST = com.tecnoweb.grupo24sa.utils.EnvConfig.get(
            "MAIL_HOST", "mail.tecnoweb.org.bo");
    private final static String USER = "grupo24sa";
    private final static String PASSWORD = "grup024grup024*";
    
    // Intentaremos usar IMAPS (IMAP Seguro) por si el 143 estándar está bloqueado
    private final static String PROTOCOL = "imaps"; 
    private final static String PORT = "993";

    private IEmailEventListener emailEventListener;

    public void setEmailEventListener(IEmailEventListener emailEventListener) {
        this.emailEventListener = emailEventListener;
    }

    @Override
    public void run() {
        while (true) {
            Store store = null;
            IMAPFolder inbox = null;
            try {
                Properties props = new Properties();
                props.put("mail.store.protocol", PROTOCOL);
                props.put("mail.imaps.host", HOST);
                props.put("mail.imaps.port", PORT);
                props.put("mail.imaps.ssl.enable", "true"); 
                // Evitar validación estricta de certificados por si es un server local universitario
                props.put("mail.imaps.ssl.trust", "*");

                Session session = Session.getInstance(props);
                store = session.getStore(PROTOCOL);
                
                System.out.println("**************** Conectando por IMAP IDLE *************");
                store.connect(USER, PASSWORD);

                inbox = (IMAPFolder) store.getFolder("INBOX");
                inbox.open(Folder.READ_WRITE);

                // Agregar el Listener que despertará mágicamente cuando llegue un correo
                inbox.addMessageCountListener(new MessageCountAdapter() {
                    @Override
                    public void messagesAdded(MessageCountEvent ev) {
                        Message[] messages = ev.getMessages();
                        List<Email> newEmails = new ArrayList<>();
                        
                        System.out.println("Nuevos correos (IMAP Push): " + messages.length);
                        
                        for (Message msg : messages) {
                            try {
                                String from = "Unknown";
                                if (msg.getFrom() != null && msg.getFrom().length > 0) {
                                    from = msg.getFrom()[0].toString();
                                    // Extraer solo el correo si viene en formato "Nombre <correo@dominio>"
                                    if(from.contains("<") && from.contains(">")) {
                                        from = from.substring(from.indexOf("<") + 1, from.indexOf(">"));
                                    }
                                }
                                
                                String subject = msg.getSubject() != null ? msg.getSubject() : "No Subject";
                                
                                // Creamos el objeto Email compatible con tu sistema actual
                                Email email = new Email(from, subject);
                                newEmails.add(email);
                                
                                // Marcar para eliminar inmediatamente y no volver a procesarlo
                                msg.setFlag(Flags.Flag.DELETED, true);
                            } catch (MessagingException e) {
                                System.err.println("Error procesando mensaje entrante: " + e.getMessage());
                            }
                        }
                        
                        // Notificar a ConnectionCore
                        if (!newEmails.isEmpty() && emailEventListener != null) {
                            emailEventListener.onReceiveEmailEvent(newEmails);
                        }
                    }
                });

                System.out.println("************** IMAP IDLE Escuchando activamente... ************");
                
                // Entrar en bucle infinito IDLE (esperando notificaciones PUSH del servidor)
                // idle() bloquea la ejecución hasta que ocurre un evento de red o el servidor corta la conexión.
                while (true) {
                    inbox.idle(); 
                }

            } catch (Exception ex) {
                System.err.println("Error en IMAP IDLE Thread: " + ex.getMessage());
            } finally {
                // Si llegamos aquí es porque se cortó la red o el servidor nos desconectó
                try {
                    if (inbox != null && inbox.isOpen()) {
                        inbox.close(true); // true = expunge (borrar los marcados como DELETED)
                    }
                    if (store != null) {
                        store.close();
                    }
                    System.out.println("************** IMAP Desconectado ************");
                } catch (MessagingException e) {
                    System.err.println("Error cerrando IMAP: " + e.getMessage());
                }

                // Esperamos unos segundos antes de intentar reconectar (seguridad)
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    System.err.println("Hilo IMAP interrumpido: " + ex.getMessage());
                }
            }
        }
    }
}
