package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import domain.Book;

class bookTests {

    @Test
    void createBookSuccess() {
        Book b = new Book("Clean Code", "Robert Martin", "111");
        assertEquals("Clean Code", b.getTitle());
        assertEquals("Robert Martin", b.getAuthor());
        assertEquals("111", b.getIsbn());
        assertTrue(b.isAvailable());
        assertNull(b.getDueDate());
    }

    @Test
    void nullTitleThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Book(null, "Author", "123"));
    }

    @Test
    void nullAuthorThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Book("Title", null, "123"));
    }

    @Test
    void nullIsbnThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Book("Title", "Author", null));
    }

    @Test
    void setAvailabilityAndDueDate() {
        Book b = new Book("Book", "A", "123");
        b.setAvailable(false);
        b.setDueDate(LocalDate.now());
        assertFalse(b.isAvailable());
        assertNotNull(b.getDueDate());
    }
}
