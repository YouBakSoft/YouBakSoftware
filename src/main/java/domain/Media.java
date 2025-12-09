package domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import service.FineStrategy;

/**
 * Represents a general media item in the library.
 * This is an abstract class for all types of media like books and CDs.
 *
 * <p>Example usage:
 * <pre><code>
 * Book book = new Book("Effective Java", "Joshua Bloch", "978-0134685991");
 * CD cd = new CD("Abbey Road", "The Beatles", "CD12345");
 * User user = userService.getUser("user1");
 * book.borrow(user);
 * cd.borrow(user);
 * int bookFine = book.calculateFine();
 * int cdFine = cd.calculateFine();
 * </code></pre>
 *
 * @since 1.0
 * @see Book
 * @see CD
 * @see User
 * @see FineStrategy
 */
public abstract class Media {

    /** Media title */
    protected String title;

    /** True if media is available for borrowing */
    protected boolean available;

    /** Due date for returning the media */
    protected LocalDate dueDate;

    /** The user who borrowed the media, null if not borrowed */
    protected User borrowedBy;

    /** Fine status: 0 = no fine, 1 = fine applied */
    protected int fineApplied;

    /** Strategy for calculating fines */
    protected FineStrategy fineStrategy;

    /**
     * Create a new Media item with a title.
     * Initially available, not borrowed, no due date.
     *
     * @param title the media title
     * @throws IllegalArgumentException if title is null
     * @since 1.0
     */
    public Media(String title) {
        if (title == null) {
            throw new IllegalArgumentException("Title must not be null");
        }
        this.title = title;
        this.available = true;
        this.borrowedBy = null;
        this.dueDate = null;
        this.fineApplied = 0;
    }

    /**
     * Borrow the media for a user.
     * Subclasses define rules (e.g., due date, duration).
     *
     * @param user the borrower
     * @since 1.0
     */
    public abstract void borrow(User user);

    /**
     * Check if the media is overdue.
     *
     * @return true if past due date, false otherwise
     * @since 1.0
     */
    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Set the fine calculation strategy.
     *
     * @param strategy the fine strategy
     * @since 1.0
     */
    public void setFineStrategy(FineStrategy strategy) {
        this.fineStrategy = strategy;
    }

    /**
     * Calculate the fine based on overdue days and strategy.
     *
     * @return fine amount, 0 if not overdue or no strategy
     * @since 1.0
     */
    public int calculateFine() {
        if (dueDate == null || available || fineStrategy == null) return 0;

        long overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        if (overdueDays <= 0) return 0;

        return fineStrategy.calculateFine((int) overdueDays);
    }

    // ----- Getters and Setters -----

    /**
     * Get the media title.
     *
     * @return title of the media
     * @since 1.0
     */
    public String getTitle() {
        return title; 
    }

    /**
     * Check if media is available.
     *
     * @return true if available
     * @since 1.0
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Set availability of the media.
     *
     * @param available true if available, false if borrowed
     * @since 1.0
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Get the due date for returning the media.
     *
     * @return due date, or null if not borrowed
     * @since 1.0
     */
    public LocalDate getDueDate() {
        return dueDate; 
    }

    /**
     * Set the due date for returning the media.
     *
     * @param dueDate the due date
     * @since 1.0
     */
    public void setDueDate(LocalDate dueDate) { 
        this.dueDate = dueDate; 
    }

    /**
     * Get the user who borrowed the media.
     *
     * @return borrower, or null if not borrowed
     * @since 1.0
     */
    public User getBorrowedBy() {
        return borrowedBy;
    }

    /**
     * Set the user who borrowed the media.
     *
     * @param borrowedBy the borrower
     * @since 1.0
     */
    public void setBorrowedBy(User borrowedBy) {
        this.borrowedBy = borrowedBy; 
    }

    /**
     * Get the fine applied status.
     *
     * @return 0 if no fine, 1 if fine applied
     * @since 1.0
     */
    public int getFineApplied() {
        return fineApplied; 
    }

    /**
     * Set the fine applied status.
     *
     * @param fineApplied 0 if no fine, 1 if fine applied
     * @since 1.0
     */
    public void setFineApplied(int fineApplied) {
        this.fineApplied = fineApplied;
    }
}
