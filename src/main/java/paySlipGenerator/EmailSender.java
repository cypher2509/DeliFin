package paySlipGenerator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.*;
import jakarta.mail.util.ByteArrayDataSource;

public class EmailSender {

    public void EmailSender(byte[] fileBytes) {
        String to = "lvpanchal@mun.ca";
        String from = "lokeshpanchal.2002@gmail.com ";
        String host = "smtp.gmail.com";
        String username = "lokeshpanchal.2002@gmail.com";
        String password = "actz ihiw vpbx fhco"; // App password generated from your Google Account

        // Setup mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Enable TLS

        // Get the Session object
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a default MimeMessage object
            Message message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(from));

            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            // Set Subject
            message.setSubject("Your PDF Document");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText("Hello,\n\nPlease find attached the requested PDF.\n\nRegards,\nYour Name");

            // Create a multipart message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(new ByteArrayInputStream(fileBytes), "application/pdf");
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName("invoice.pdf");
            multipart.addBodyPart(attachmentBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send the message
            Transport.send(message);

            System.out.println("Email Sent Successfully with PDF attachment...");

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

