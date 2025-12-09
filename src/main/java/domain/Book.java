package domain;

import java.time.LocalDate;

/**
 * Represents a Book in the library.
 * A Book is a type of {@link Media} that users can borrow.
 *
 * <p>Example usage:
 * <pre><code>
 * Book book = new Book("Effective Java", "Joshua Bloch", "978-0134685991");
 * User user = userService.getUser("user1");
 * book.borrow(user);
 * String author = book.getAuthor();
 * String isbn = book.getIsbn();
 * </code></pre>
 *
 * @since 1.0
 * @see Media
 * @see User
 */
public class Book extends Media {

    /** The book's author */
    private String author;

    /** The book's ISBN */
    private String isbn;

    /**
     * Create a new Book with title, author, and ISBN.
     *
     * @param title the book title
     * @param author the author's name
     * @param isbn the ISBN of the book
     * @throws IllegalArgumentException if author or ISBN is null
     * @since 1.0
     */
    public Book(String title, String author, String isbn) {
        super(title);
        if (author == null || isbn == null) {
            throw new IllegalArgumentException("Author and ISBN must not be null");
        }
        this.author = author;
        this.isbn = isbn;
    }

    /**
     * Borrow the book for a user.
     * Sets the due date to 28 days from today.
     *
     * @param user the user borrowing the book
     * @throws IllegalStateException if the book is already borrowed
     * @since 1.0
     */
    @Override
    public void borrow(User user) {
        if (!available) {
            throw new IllegalStateException("Book is already borrowed");
        }
        this.borrowedBy = user;
        this.available = false;
        this.dueDate = LocalDate.now().plusDays(28);
    }

    /**
     * Get the book's author.
     *
     * @return author name
     * @since 1.0
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get the book's ISBN.
     *
     * @return ISBN
     * @since 1.0
     */
    public String getIsbn() {
        return isbn;
    }
}
