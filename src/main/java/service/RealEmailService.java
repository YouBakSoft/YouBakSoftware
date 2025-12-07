package service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Real implementation of {@link EmailService} that sends emails using SMTP.
 * <p>
 * Uses Gmail's SMTP server to send messages. Sender credentials are loaded from
 * environment variables via {@link Dotenv}:
 * <ul>
 *     <li>SENDER_EMAIL</li>
 *     <li>SENDER_PASSWORD</li>
 * </ul>
 * </p>
 *
 * <p>This service is typically used in combination with {@link EmailNotifier}
 * to notify library users about overdue media or other important messages.</p>
 *
 * @see EmailNotifier
 * @see EmailService
 */
public class RealEmailService implements EmailService {

    /** Loads environment variables */
    Dotenv dotenv = Dotenv.load();

    /** Email address used to send messages */
    String senderEmail = dotenv.get("SENDER_EMAIL");

    /** Password for the sender email account */
    String senderPassword = dotenv.get("SENDER_PASSWORD");

    /**
     * Sends an email to the specified recipient.
     *
     * @param to      the recipient's email address
     * @param message the email body content
     */
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
