package com.tecnoweb.grupo24sa.communication;

import com.tecnoweb.grupo24sa.interfaces.IEmailEventListener;
import com.tecnoweb.grupo24sa.utils.Command;
import com.tecnoweb.grupo24sa.utils.Email;
import com.tecnoweb.grupo24sa.utils.Extractor;

import javax.security.sasl.AuthenticationException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MailVerificationThread implements Runnable {

    private final static int PORT_POP = 110;
    private final static String HOST = com.tecnoweb.grupo24sa.utils.EnvConfig.get(
            "MAIL_HOST", "mail.tecnoweb.org.bo");
    private final static String USER = "grupo24sa";
    private final static String PASSWORD = "grup024grup024*";

    private Socket socket;
    private BufferedReader input;
    private DataOutputStream output;

    private IEmailEventListener emailEventListener;

    public IEmailEventListener getEmailEventListener() {
        return emailEventListener;
    }

    public void setEmailEventListener(IEmailEventListener emailEventListener) {
        this.emailEventListener = emailEventListener;
    }

    public MailVerificationThread() {
        socket = null;
        input = null;
        output = null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<Email> emails = null;
                socket = new Socket(HOST, PORT_POP);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new DataOutputStream(socket.getOutputStream());
                System.out.println("**************** Conexion establecida *************");

                authUser(USER, PASSWORD);

                int count = getEmailCount();
                if (count > 0) {
                    emails = getEmails(count);
                    System.out.println("Nuevos correos: " + emails.size());
                    deleteEmails(count);
                }
                output.writeBytes(Command.quit());
                input.readLine();

                // Procesar correos
                if (count > 0 && emailEventListener != null) {
                    emailEventListener.onReceiveEmailEvent(emails);
                }

            } catch (IOException ex) {
                System.err.println("Error de red (POP3): " + ex.getMessage());
            } catch (Exception ex) {
                System.err.println("Error general procesando correo: " + ex.getMessage());
            } finally {
                // Siempre cerramos la conexión de forma segura
                try {
                    if (input != null)
                        input.close();
                    if (output != null)
                        output.close();
                    if (socket != null && !socket.isClosed())
                        socket.close();
                    System.out.println("************** Conexion cerrada ************");
                } catch (IOException e) {
                    System.err.println("Error cerrando sockets: " + e.getMessage());
                }

                // El sleep DEBE estar en el finally para garantizar la espera
                // incluso si hubo error, evitando así colapsar/atacar el servidor.
                try {
                    Thread.sleep(10000); // 10 segundos
                } catch (InterruptedException ex) {
                    System.err.println("Hilo interrumpido: " + ex.getMessage());
                }
            }
        }
    }

    private void authUser(String email, String password) throws IOException {
        if (socket != null && input != null && output != null) {
            input.readLine();
            output.writeBytes(Command.user(email));
            input.readLine();
            output.writeBytes(Command.pass(password));
            String message = input.readLine();
            if (message.contains("-ERR")) {
                throw new AuthenticationException();
            }
        }
    }

    private void deleteEmails(int emails) throws IOException {
        for (int i = 1; i <= emails; i++) {
            output.writeBytes(Command.dele(i));
        }
    }

    private int getEmailCount() throws IOException {
        output.writeBytes(Command.stat());
        String line = input.readLine();
        String[] data = line.split(" ");
        return Integer.parseInt(data[1]);
    }

    private List<Email> getEmails(int count) throws IOException {
        List<Email> emails = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            output.writeBytes(Command.retr(i));
            String text = readMultiline();
            emails.add(Extractor.getEmail(text));
        }
        return emails;
    }

    private String readMultiline() throws IOException {
        String lines = "";
        while (true) {
            String line = input.readLine();
            if (line == null) {
                throw new IOException("Server no responde (ocurrio un error al abrir el correo)");
            }
            if (line.equals(".")) {
                break;
            }
            lines = lines + "\n" + line;
        }
        return lines;
    }
}
