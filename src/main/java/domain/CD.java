package domain;

import java.time.LocalDate;

public class CD extends Media {

    private String id;       // unique identifier for CD
    private String artist;   // optional, like author for books

    public CD(String title, String artist, String id) {
        super(title);
        if (id == null) throw new IllegalArgumentException("CD ID must not be null");
        this.artist = artist;
        this.id = id;
    }

    @Override
    public void borrow(User user) {
        if (!available) {
            throw new IllegalStateException("CD is already borrowed");
        }
        this.borrowedBy = user;
        this.available = false;
        this.dueDate = LocalDate.now().plusDays(7); // CDs = 7 days
    }

    // Getters
    public String getId() { return id; }
    public String getArtist() { return artist; }
}
