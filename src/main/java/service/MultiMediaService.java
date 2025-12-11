package service;

import domain.Book;
import domain.Media;
import domain.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for managing multiple types of media in the library.
 * Implements {@link MediaService} and provides common functionality such as:
 * <ul>
 *     <li>Fine calculation</li>
 *     <li>Observer notifications</li>
 *     <li>User borrowing checks</li>
 *     <li>Returning all media for a user</li>
 *     <li>Retrieving overdue media</li>
 * </ul>
 *
 * <p>Subclasses must implement methods for reading and writing media to persistent storage.</p>
 *
 * @param <T> type of media managed by this service (e.g., {@link Book})
 * @since 1.0
 * @see MediaService
 */
public abstract class MultiMediaService<T extends Media>
        implements MediaService<T> {

    /** Strategy for calculating fines */
    protected FineStrategy fineStrategy;

    /** Observers to notify about overdue items */
    protected List<Observer> observers = new ArrayList<>();

    /** Service for managing users */
    protected UserService userService;

    /**
     * Sets the {@link UserService} instance for this media service.
     *
     * @param userService the user service
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Sets the fine calculation strategy.
     *
     * @param strategy the fine strategy
     */
    public void setFineStrategy(FineStrategy strategy) {
        this.fineStrategy = strategy;
    }

    /**
     * Adds an observer to receive notifications.
     *
     * @param observer the observer to add
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers for a given user with a message.
     *
     * @param user    the user to notify
     * @param message the message to send
     */
    protected void notifyObservers(User user, String message) {
        for (Observer o : observers) o.notify(user, message);
    }

    /**
     * Calculates the fine for a media item based on overdue days and the configured strategy.
     *
     * @param media the media item
     * @return the calculated fine, or 0 if not overdue
     */
    @Override
    public int calculateFine(T media) {
        if (media.getDueDate() == null || media.isAvailable()) return 0;
        long overdueDays = java.time.temporal.ChronoUnit.DAYS
                .between(media.getDueDate(), LocalDate.now());
        if (overdueDays > 0 && fineStrategy != null) {
            return fineStrategy.calculateFine((int) overdueDays);
        }
        return 0;
    }

    /**
     * Checks if a user can borrow media based on overdue items or unpaid fines.
     *
     * @param user     the user
     * @param allMedia list of all media
     * @return true if the user can borrow, false otherwise
     */
    public boolean canUserBorrow(User user, List<Media> allMedia) {
        if (!user.canBorrow()) return false;

        for (Media m : allMedia) {
            if (!m.isAvailable()
                    && user.equals(m.getBorrowedBy())
                    && m.isOverdue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a user has active loans for this media type.
     *
     * @param user the user
     * @return true if user has any borrowed media not returned
     */
    public boolean hasActiveLoans(User user) {
        if (user == null) return false;
        return readFromFile().stream()
                .anyMatch(m -> !m.isAvailable()
                        && user.equals(m.getBorrowedBy()));
    }

    /**
     * Returns all media borrowed by a user and marks them as returned.
     *
     * @param user the user returning all media
     */
    @Override
    public void returnAllMediaForUser(User user) {
        List<T> mediaList = readFromFile();

        for (T m : mediaList) {
            if (user.equals(m.getBorrowedBy())) {
                m.setAvailable(true);
                m.setBorrowedBy(null);
                m.setDueDate(null);
                m.setFineApplied(0);
            }
        }
        writeToFile(mediaList);
    }

    /**
     * Returns a list of all media that are currently overdue.
     *
     * @return list of overdue media
     */
    @Override
    public List<T> getOverdueMedia() {
        return readFromFile().stream()
                .filter(m -> !m.isAvailable())
                .filter(m -> m.getDueDate() != null)
                .filter(m -> m.getBorrowedBy() != null)
                .filter(m -> LocalDate.now().isAfter(m.getDueDate()))
                .toList();
    }

    /**
     * Sends reminders to users about overdue media.
     *
     * @param users      the users to notify
     * @param mediaLabel the type of media (e.g., "Book", "CD") to include in the message
     */
    public void sendReminders(List<User> users, String mediaLabel) {
        List<T> overdue = getOverdueMedia();

        for (User user : users) {
            long count = overdue.stream()
                    .filter(m -> user.equals(m.getBorrowedBy()))
                    .count();

            if (count > 0) {
                notifyObservers(user,
                        "You have " + count + " overdue " + mediaLabel + "(s).");
            }
        }
    }

    /**
     * Reads all media from persistent storage.
     * Must be implemented by subclasses.
     *
     * @return list of media
     */
    protected abstract List<T> readFromFile();

    /**
     * Writes a list of media to persistent storage.
     * Must be implemented by subclasses.
     *
     * @param list list of media to write
     */
    public abstract void writeToFile(List<T> list);
}
