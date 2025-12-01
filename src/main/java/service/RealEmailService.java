package service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;

public class RealEmailService implements EmailService {

	Dotenv dotenv = Dotenv.load();
	String senderEmail = dotenv.get("SENDER_EMAIL");
	String senderPassword = dotenv.get("SENDER_PASSWORD");

    @Override
    public void sendEmail(String to, String message) {
        if (to == null || to.isBlank()) {
            System.out.println("Skipping email: recipient is null or empty.");
            return;  // prevent any NullPointerException
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(senderEmail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject("Library Overdue Reminder");
            msg.setText(message);

            Transport.send(msg);
            System.out.println("Email sent successfully to " + to);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
