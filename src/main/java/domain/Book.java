package domain;

import java.time.LocalDate;

/**
 * Represents a Book in the library system.
 * A Book is a type of {@link Media} that can be borrowed by users.
 */
public class Book extends Media {

    /** The author of the book */
    private String author;

    /** The International Standard Book Number (ISBN) */
    private String isbn;

    /**
     * Constructs a new Book with the specified title, author, and ISBN.
     *
     * @param title the title of the book
     * @param author the author of the book
     * @param isbn the ISBN of the book
     * @throws IllegalArgumentException if author or ISBN is null
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
     * Borrows the book for a specified user.
     * The book must be available; otherwise, an exception is thrown.
     * Sets the due date to 28 days from the current date.
     *
     * @param user the user borrowing the book
     * @throws IllegalStateException if the book is already borrowed
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
     * Returns the author of the book.
     *
     * @return the author's name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the ISBN of the book.
     *
     * @return the book's ISBN
     */
    public String getIsbn() {
        return isbn;
    }
}
