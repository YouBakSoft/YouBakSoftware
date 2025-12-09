package tests;

import domain.Book;
import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.BookService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;

class bookServiceTest {

    private BookService bookService;
    private User user;

    @BeforeEach
    void setup() {
        bookService = new BookService();
        user = new User("Alice", "U1", "alice@example.com");
        bookService.writeToFile(List.of()); // start fresh
    }

    @Test
    void borrowBookSuccessfully() {
        Book book = new Book("Java 101", "John Doe", "ISBN123");
        bookService.addMedia(book);
        Book borrowed = bookService.borrowMedia(user, "ISBN123");
        assertFalse(borrowed.isAvailable());
        assertEquals(user, borrowed.getBorrowedBy());
        assertNotNull(borrowed.getDueDate());
    }

    @Test
    void cannotBorrowAlreadyBorrowedBook() {
        Book book = new Book("Java 101", "John Doe", "ISBN123");
        bookService.addMedia(book);
        bookService.borrowMedia(user, "ISBN123");
        User user2 = new User("Bob", "U2", "bob@example.com");
        Exception ex = assertThrows(IllegalStateException.class,
                () -> bookService.borrowMedia(user2, "ISBN123"));
        assertEquals("Book already borrowed", ex.getMessage());
    }

    @Test
    void cannotBorrowIfUserHasFines() {
        Book book = new Book("Java 101", "John Doe", "ISBN123");
        bookService.addMedia(book);
        user.addFine(10.0);
        Exception ex = assertThrows(IllegalStateException.class,
                () -> bookService.borrowMedia(user, "ISBN123"));
        assertEquals("Cannot borrow books: overdue media or unpaid fines", ex.getMessage());
    }

    @Test
    void searchBookByTitleAuthorIsbn() {
        Book book1 = new Book("Java Basics", "John Doe", "ISBN123");
        Book book2 = new Book("Python Guide", "Jane Doe", "ISBN456");
        bookService.addMedia(book1);
        bookService.addMedia(book2);
        assertEquals(1, bookService.search("Java").size());
        assertEquals(1, bookService.search("Jane").size());
        assertEquals(1, bookService.search("ISBN123").size());
        assertEquals(0, bookService.search("C++").size());
    }

    @Test
    void cannotAddBookWithSameISBN() {
        Book book1 = new Book("Java Basics", "John Doe", "ISBN123");
        Book book2 = new Book("Another Book", "Jane Doe", "ISBN123");
        bookService.addMedia(book1);
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> bookService.addMedia(book2));
        assertEquals("Book with same ISBN already exists", ex.getMessage());
    }

    // ---- Additional tests for 100% coverage ----

    @Test
    void addMediaNullFieldsThrows() {
        Book book = mock(Book.class);
        when(book.getTitle()).thenReturn(null);
        when(book.getAuthor()).thenReturn("Author");
        when(book.getIsbn()).thenReturn("ISBN1");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> bookService.addMedia(book));
        assertEquals("Title, author, and ISBN cannot be null", ex.getMessage());
    }

    @Test
    void borrowMediaNullUserThrows() {
        Book book = new Book("Title", "Author", "ISBN1");
        bookService.addMedia(book);
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> bookService.borrowMedia(null, "ISBN1"));
        assertEquals("User cannot be null", ex.getMessage());
    }

    @Test
    void readFromFileInvalidDateHandled() throws IOException {
        // simulate invalid date in file
        File f = new File("data/books.txt");
        f.getParentFile().mkdirs();
        try (var bw = new java.io.BufferedWriter(new java.io.FileWriter(f))) {
            bw.write("Title;Author;ISBN;true;invalid-date;null;0");
            bw.newLine();
        }
        // just call readFromFile to trigger warning branch
        List<Book> books = bookService.getAllMedia();
        assertEquals(1, books.size());
        assertNull(books.get(0).getDueDate());
    }

    @Test
    void writeToFileHandlesUserNull() {
        Book book = new Book("Title", "Author", "ISBN1");
        book.setBorrowedBy(null); // ensure branch where user is null
        assertDoesNotThrow(() -> bookService.writeToFile(List.of(book)));
    }
}
