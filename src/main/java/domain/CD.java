package domain;

import java.time.LocalDate;

/**
 * Represents a CD in the library system.
 * A CD is a type of {@link Media} that can be borrowed by users.
 */
public class CD extends Media {

    /** The unique identifier of the CD */
    private String id;

    /** The artist of the CD */
    private String artist;

    /**
     * Constructs a new CD with the specified title, artist, and ID.
     *
     * @param title the title of the CD
     * @param artist the artist of the CD
     * @param id the unique identifier of the CD
     * @throws IllegalArgumentException if the CD ID is null
     */
    public CD(String title, String artist, String id) {
        super(title);
        if (id == null) throw new IllegalArgumentException("CD ID must not be null");
        this.artist = artist;
        this.id = id;
    }

    /**
     * Borrows the CD for a specified user.
     * The CD must be available; otherwise, an exception is thrown.
     * Sets the due date to 7 days from the current date.
     *
     * @param user the user borrowing the CD
     * @throws IllegalStateException if the CD is already borrowed
     */
    @Override
    public void borrow(User user) {
        if (!available) {
            throw new IllegalStateException("CD is already borrowed");
        }
        this.borrowedBy = user;
        this.available = false;
        this.dueDate = LocalDate.now().plusDays(7);
    }

    /**
     * Returns the unique identifier of the CD.
     *
     * @return the CD's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the artist of the CD.
     *
     * @return the CD's artist
     */
    public String getArtist() {
        return artist;
    }
}
