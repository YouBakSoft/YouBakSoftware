package domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import service.FineStrategy;

/**
 * Represents a general media item in the library system.
 * This class is abstract and provides common properties and behaviors
 * for all media types such as books, CDs, etc.
 */
public abstract class Media {

    /** The title of the media */
    protected String title;

    /** Indicates whether the media is currently available for borrowing */
    protected boolean available;

    /** The due date for returning the media */
    protected LocalDate dueDate;

    /** The user who borrowed the media, or null if not borrowed */
    protected User borrowedBy;

    /** Flag indicating if a fine has been applied (0 = no fine, 1 = fine applied) */
    protected int fineApplied;

    /** Strategy for calculating fines */
    protected FineStrategy fineStrategy;

    /**
     * Constructs a new Media item with the given title.
     * Initially, the media is available, not borrowed, and has no due date.
     *
     * @param title the title of the media
     * @throws IllegalArgumentException if the title is null
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
     * Borrows this media item for a specific user.
     * Must be implemented by subclasses to define borrowing rules (e.g., due date, duration).
     *
     * @param user the user borrowing the media
     */
    public abstract void borrow(User user);

    /**
     * Checks whether the media is overdue.
     *
     * @return true if the current date is after the due date, false otherwise
     */
    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Sets the strategy used for calculating fines for this media.
     *
     * @param strategy the fine calculation strategy
     */
    public void setFineStrategy(FineStrategy strategy) {
        this.fineStrategy = strategy;
    }

    /**
     * Calculates the fine for this media based on the overdue days and fine strategy.
     *
     * @return the fine amount in integer units (e.g., NIS)
     */
    public int calculateFine() {
        if (dueDate == null || available || fineStrategy == null) return 0;

        long overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        if (overdueDays <= 0) return 0;

        return fineStrategy.calculateFine((int) overdueDays);
    }

    // ----- Getters and Setters -----

    /**
     * Returns the title of the media.
     *
     * @return the media title
     */
    public String getTitle() {
        return title; 
    }

    /**
     * Checks if the media is available for borrowing.
     *
     * @return true if available, false otherwise
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets the availability of the media.
     *
     * @param available true if available, false otherwise
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Returns the due date for the media.
     *
     * @return the due date, or null if not borrowed
     */
    public LocalDate getDueDate() {
        return dueDate; 
    }

    /**
     * Sets the due date for the media.
     *
     * @param dueDate the due date
     */
    public void setDueDate(LocalDate dueDate) { 
        this.dueDate = dueDate; 
    }

    /**
     * Returns the user who borrowed this media.
     *
     * @return the borrower, or null if not borrowed
     */
    public User getBorrowedBy() {
        return borrowedBy;
    }

    /**
     * Sets the user who borrowed this media.
     *
     * @param borrowedBy the borrower
     */
    public void setBorrowedBy(User borrowedBy) {
        this.borrowedBy = borrowedBy; 
    }

    /**
     * Returns the fine applied status.
     *
     * @return 0 if no fine applied, 1 if fine applied
     */
    public int getFineApplied() {
        return fineApplied; 
    }

    /**
     * Sets the fine applied status.
     *
     * @param fineApplied 0 for no fine, 1 for fine applied
     */
    public void setFineApplied(int fineApplied) {
        this.fineApplied = fineApplied;
    }

}
