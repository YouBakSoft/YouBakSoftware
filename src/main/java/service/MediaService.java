package service;

import domain.Media;
import domain.User;
import java.util.List;

/**
 * Generic interface for managing media items in the library.
 * Provides methods for adding, borrowing, searching, and managing fines for media.
 *
 * <p>Example usage:
 * <pre><code>
 * MediaService&lt;Book&gt; bookService = new BookService();
 * Book book = new Book("Java Programming", "Author Name", "12345");
 * bookService.addMedia(book);
 * bookService.borrowMedia(user, "12345");
 * </code></pre>
 *
 * @param <T> the type of media managed by the service (e.g., {@link domain.Book}, {@link domain.CD})
 * @since 1.0
 * @see domain.Media
 * @see domain.User
 */
public interface MediaService<T extends Media> {

    /**
     * Adds a new media item to the system.
     *
     * @param media the media item to add
     * @return the added media
     */
    T addMedia(T media);

    /**
     * Borrows a media item for a user, identified by a unique identifier
     * (e.g., ISBN for books, ID for CDs).
     *
     * @param user the user borrowing the media
     * @param identifier unique identifier of the media
     * @return the borrowed media
     */
    T borrowMedia(User user, String identifier);

    /**
     * Returns a list of media items that are currently overdue.
     *
     * @return list of overdue media
     */
    List<T> getOverdueMedia();

    /**
     * Calculates the fine for a given media item based on overdue days.
     *
     * @param media the media item
     * @return the calculated fine
     */
    int calculateFine(T media);

    /**
     * Returns all media borrowed by a specific user.
     * Marks them as returned in the system.
     *
     * @param user the user returning all media
     */
    void returnAllMediaForUser(User user);

    /**
     * Searches media items by title, author/artist, or other identifiers.
     *
     * @param query the search query
     * @return list of media that match the query
     */
    List<T> search(String query);
}
