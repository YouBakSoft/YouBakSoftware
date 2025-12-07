package domain;

import java.time.LocalDate;

/**
 * Represents a CD in the library.
 * A CD is a type of {@link Media} that users can borrow.
 */
public class CD extends Media {

    /** The CD's unique ID */
    private String id;

    /** The artist of the CD */
    private String artist;

    /**
     * Create a new CD with title, artist, and ID.
     *
     * @param title the CD title
     * @param artist the artist's name
     * @param id the CD's unique identifier
     * @throws IllegalArgumentException if the ID is null
     */
    public CD(String title, String artist, String id) {
        super(title);
        if (id == null) throw new IllegalArgumentException("CD ID must not be null");
        this.artist = artist;
        this.id = id;
    }

    /**
     * Borrow the CD for a user.
     * Sets the due date to 7 days from today.
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
     * Get the CD's unique ID.
     *
     * @return CD ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the CD's artist.
     *
     * @return artist name
     */
    public String getArtist() {
        return artist;
    }
}
