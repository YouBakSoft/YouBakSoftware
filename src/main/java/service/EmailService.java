package service;

/**
 * Represents a service that can send emails to users.
 *
 * <p>Implementations of this interface define how emails are sent (e.g., via SMTP).</p>
 *
 * <p>Example usage:
 * <pre><code>
 * EmailService emailService = new SmtpEmailService();
 * emailService.sendEmail("alice@example.com", "Your book is overdue!");
 * </code></pre>
 *
 * @since 1.0
 */
public interface EmailService {

    /**
     * Sends an email to the specified recipient with the given message.
     *
     * @param to the recipient's email address
     * @param message the message content
     * @since 1.0
     */
    void sendEmail(String to, String message);
}
