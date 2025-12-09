package service;

import domain.User;

/**
 * Observer interface for receiving notifications about library events.
 * Typically used to notify users about overdue media or other important messages.
 *
 * <p>Classes implementing this interface define how users are notified,
 * e.g., via email, SMS, or console messages.</p>
 *
 * @see EmailNotifier
 */
public interface Observer {

    /**
     * Notify a user with a message.
     *
     * @param user    the user to notify
     * @param message the message content
     */
    void notify(User user, String message);
}
