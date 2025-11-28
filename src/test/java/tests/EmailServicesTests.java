package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Book;
import domain.User;
import service.BookService;
import service.EmailNotifier;
import service.MockEmailService;

public class EmailServicesTests {

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
    
   
    private User createUserWithOverdueBook(String name, String email, String isbn, int overdueDays) throws Exception {
        User u = new User(name, email);
        Book b = service.addBook("Book " + isbn, "Author", isbn);
        service.borrowBook(u, isbn);

        
        List<Book> allBooks = service.search("");
        for (Book book : allBooks) {
            if (book.getIsbn().equals(isbn)) {
                book.setDueDate(LocalDate.now().minusDays(overdueDays));
            }
        }

        
        var m = BookService.class.getDeclaredMethod("writeBooksToFile", List.class);
        m.setAccessible(true);
        m.invoke(service, allBooks);

        return u;
    }
    
    @Test
    public void testSendReminderWithObserver() throws Exception {
        MockEmailService mockEmail = new MockEmailService();
        EmailNotifier emailNotifier = new EmailNotifier(mockEmail);
        service.addObserver(emailNotifier);

        User u = createUserWithOverdueBook("Alice", "alice@example.com", "300", 2);

        service.sendReminders(List.of(u));

        assertEquals(1, mockEmail.getSentMessages().size());
        assertTrue(mockEmail.getSentMessages().get(0).contains("You have 1 overdue book(s)"));
    }

}
