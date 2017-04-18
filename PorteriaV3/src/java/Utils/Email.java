/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author MAURICIO
 */
public class Email {    
    
    //final String senderEmailID = "cager@seracis.com"; //Correo emisor 
    //final String senderPassword = "Veryown2015";  // PW emisor 
    //final String emailSMTPserver = "mail.seracis.com"; //Servidor SMTP 
    
    final String senderEmailID = "controlacceso.cager@gmail.com"; //Correo emisor 
    final String senderPassword = "Colombia2015*";  // PW emisor 
    final String emailSMTPserver = "smtp.gmail.com"; //Servidor SMTP 
    
    //final String senderEmailID = "camilo.hernandez.castillo@gmail.com"; //Correo emisor 
    //final String senderPassword = "KMILO_15";  // PW emisor 
    //final String emailSMTPserver = "smtp.gmail.com"; //Servidor SMTP 
    
    final String emailServerPort = "465"; // SSL
    
    String receiverEmails = ""; //Dato de receptor y mensaje por defecto 
    String ccEmails = "";
    String emailSubject = "Hola";
    String emailBody = "Hola mundo";

    public Email() {
    }

    public Email(String receiverEmails, String ccEmails, String emailSubject, String emailBody, String rutaArchivo) {
        this.receiverEmails = receiverEmails;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.ccEmails = ccEmails;
        // Ajusta propiedades para enviar mail 
        Properties props = new Properties();
        props.put("mail.user", senderEmailID);
        props.put("mail.password", senderPassword);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", emailSMTPserver);
        props.put("mail.smtp.port", emailServerPort);
        props.put("mail.smtp.socketFactory.port", emailServerPort);
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.connectiontimeout", "7000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtps.ssl.checkserveridentity", "false");
        props.put("mail.smtps.ssl.trust", "*");
        //Construye mail 
        try {
            Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(senderEmailID));
            
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(receiverEmails));
            if (ccEmails != null && !ccEmails.isEmpty()) {
                msg.setRecipients(Message.RecipientType.CC,
                        InternetAddress.parse(ccEmails));
            }
            
            msg.setSubject(emailSubject);
            
            BodyPart cuerpoMensaje = new MimeBodyPart();
            cuerpoMensaje.setText(emailBody);
            //msg.setText(emailBody);
            Multipart multiparte = new MimeMultipart();
            multiparte.addBodyPart(cuerpoMensaje);

            //String nombreArchivo = "C:/Users/RHA/Desktop/Kmilo/NetBeansProyects/Email/src/email/consulta.pdf";
            if(rutaArchivo!=null){
                cuerpoMensaje = new MimeBodyPart();
                DataSource fuente = new FileDataSource(rutaArchivo);
                cuerpoMensaje.setDataHandler(new DataHandler(fuente));
                cuerpoMensaje.setFileName(rutaArchivo);
                multiparte.addBodyPart(cuerpoMensaje);
            }
            msg.setContent(multiparte);
            
            Transport tr = session.getTransport("smtps");
            tr.connect(emailSMTPserver, 465, senderEmailID, senderPassword);
            msg.saveChanges();
            
            tr.sendMessage(msg, msg.getAllRecipients());
            tr.close();
            //Transport.send(msg);
            System.out.println("Email: Mensaje Enviado");
        } catch (Exception mex) {
            System.out.println("Email: mal"+mex);
        }
    }

    public class SMTPAuthenticator extends javax.mail.Authenticator {
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(senderEmailID, senderPassword);
        }
    }

    
}
