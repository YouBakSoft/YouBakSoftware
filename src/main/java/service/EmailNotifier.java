package service;

import domain.User;

/**
 * Sends notifications to users via email.
 * Implements the {@link Observer} interface.
 *
 * <p>Example usage:
 * <pre><code>
 * EmailService emailService = new EmailService();
 * EmailNotifier notifier = new EmailNotifier(emailService);
 * User user = new User("Alice", "U001", "alice@example.com");
 * notifier.notify(user, "Your book is overdue!");
 * </code></pre>
 *
 * @since 1.0
 * @see Observer
 * @see EmailService
 * @see User
 */
public class EmailNotifier implements Observer {

    /** Service used to send emails */
    private final EmailService emailService;

    /**
     * Creates an EmailNotifier with the given {@link EmailService}.
     *
     * @param emailService service used to send emails
     * @since 1.0
     */
    public EmailNotifier(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Notify a user by sending them an email.
     * Prints a warning if the user has no email address.
     *
     * @param user user to notify
     * @param message message to send
     * @since 1.0
     */
    @Override
    public void notify(User user, String message) {
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            System.out.println("Cannot send email: user " + user.getName() + " has no email.");
            return;
        }
        emailService.sendEmail(email, message);
    }
}
