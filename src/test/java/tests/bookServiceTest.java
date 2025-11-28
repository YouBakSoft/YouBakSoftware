package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Book;
import domain.User;
import service.BookService;

class bookServiceTest {

    private BookService service;

    @BeforeEach
    void clearFileAndInit() {
        try (FileWriter fw = new FileWriter("data/books.txt")) {
            fw.write("");    
        } catch (Exception e) {
            e.printStackTrace();
        }
        service = new BookService();
    }

    @Test
    void addBookSuccess() {
        Book b = service.addBook("Clean Code", "Robert Martin", "111");
        assertNotNull(b);
        assertEquals("Clean Code", b.getTitle());
    }

    @Test
    void addBookDuplicateIsbn() {
        service.addBook("B1", "A", "123");
        assertThrows(IllegalArgumentException.class, () -> service.addBook("B2", "B", "123"));
    }

    @Test
    void searchBook() {
        service.addBook("Clean Code", "Robert Martin", "111");
        List<Book> results = service.search("Clean");
        assertEquals(1, results.size());
    }

    @Test
    void borrowBookSuccess() {
        User u = new User("Baker", "100");
        service.addBook("Clean Code", "Robert Martin", "111");
        Book b = service.borrowBook(u, "111");
        assertFalse(b.isAvailable());
        assertEquals(LocalDate.now().plusDays(28), b.getDueDate());
    }

    @Test
    void borrowBookAlreadyBorrowed() {
        User u = new User("Baker", "100");
        service.addBook("Clean Code", "Robert Martin", "111");
        service.borrowBook(u, "111");
        assertThrows(IllegalArgumentException.class, () -> service.borrowBook(u, "111"));
    }

    @Test
    void borrowBookNotFound() {
        User u = new User("Baker", "100");
        assertThrows(IllegalArgumentException.class, () -> service.borrowBook(u, "999"));
    }

    @Test
    void overdueBookDetection() throws Exception {
        User u = new User("Baker", "100");
        Book b = service.addBook("Old Book", "A", "101");
        service.borrowBook(u, "101");

        List<Book> all = service.search("");
        for (Book book : all) {
            if (book.getIsbn().equals("101")) book.setDueDate(LocalDate.now().minusDays(1));
        }

        var m = BookService.class.getDeclaredMethod("writeBooksToFile", List.class);
        m.setAccessible(true);
        m.invoke(service, all);

        List<Book> overdue = service.getOverdueBooks();
        assertEquals(1, overdue.size());
        assertEquals("101", overdue.get(0).getIsbn());
    }

    @Test
    void borrowBookWithFineFails() {
        User u = new User("Baker", "100");
        service.addBook("Clean Code", "Robert Martin", "111");
        u.addFine(50);
        assertFalse(u.canBorrow());
        assertThrows(IllegalStateException.class, () -> service.borrowBook(u, "111"));
    }

    @Test
    void payFineAndBorrow() {
        User u = new User("Alice", "U01");
        service.addBook("Clean Code", "Robert Martin", "111");
        u.addFine(50);
        u.payFine(50);
        assertEquals(0, u.getFineBalance());
        assertTrue(u.canBorrow());
        Book b = service.borrowBook(u, "111");
        assertFalse(b.isAvailable());
    }
}
