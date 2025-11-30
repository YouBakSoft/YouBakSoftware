package domain;

import java.time.LocalDate;

public class Book extends Media {

    private String author;
    private String isbn;

    public Book(String title, String author, String isbn) {
        super(title);
        if (author == null || isbn == null) {
            throw new IllegalArgumentException("Author and ISBN must not be null");
        }
        this.author = author;
        this.isbn = isbn;
    }

    @Override
    public void borrow(User user) {
        if (!available) {
            throw new IllegalStateException("Book is already borrowed");
        }
        this.borrowedBy = user;
        this.available = false;
        this.dueDate = LocalDate.now().plusDays(14); // books = 14 days
    }



    // Getters
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
}
