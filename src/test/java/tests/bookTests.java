package tests;

import domain.Book;
import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class bookTests {

    private Book book;
    private User user;

    @BeforeEach
    void setup() {
        book = new Book("Effective Java", "Joshua Bloch", "978-0134685991");
        user = mock(User.class);
    }


    @Test
    void cannotCreateBookWithNullAuthor() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new Book("Title", null, "12345"));
        assertEquals("Author and ISBN must not be null", ex.getMessage());
    }

    @Test
    void cannotCreateBookWithNullIsbn() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new Book("Title", "Author", null));
        assertEquals("Author and ISBN must not be null", ex.getMessage());
    }


    @Test
    void borrowBookSuccessfully() {
        book.borrow(user);
        assertFalse(book.isAvailable());
        assertEquals(user, book.getBorrowedBy());
        assertEquals(LocalDate.now().plusDays(28), book.getDueDate());
    }

    @Test
    void cannotBorrowAlreadyBorrowedBook() {
        book.borrow(user);
        User anotherUser = mock(User.class);

        Exception ex = assertThrows(IllegalStateException.class,
                () -> book.borrow(anotherUser));
        assertEquals("Book is already borrowed", ex.getMessage());
    }


    @Test
    void getAuthorReturnsCorrectValue() {
        assertEquals("Joshua Bloch", book.getAuthor());
    }

    @Test
    void getIsbnReturnsCorrectValue() {
        assertEquals("978-0134685991", book.getIsbn());
    }
}
