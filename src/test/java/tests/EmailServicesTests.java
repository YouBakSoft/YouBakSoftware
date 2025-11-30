package tests;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.Book;
import domain.User;
import service.BookService;
import service.EmailNotifier;
import service.EmailService;
import service.UserService;

import static org.mockito.Mockito.*;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class EmailServicesTests {

    private BookService service;

    @Mock
    private UserService mockUserService;

    @BeforeEach
    void clearFileAndInit() throws Exception {
        try (FileWriter fw = new FileWriter("data/books.txt")) {
            fw.write("");
        }

        service = new BookService();
        service.setUserService(mockUserService);
    }

  /*  @Test
    void testSendReminder() throws Exception {
        User u = new User("Alice", "alice@example.com");

        // Make the mocked UserService return this user
        when(mockUserService.getAllUsers()).thenReturn(List.of(u));

        // Add your email observer
        EmailService mockEmailService = mock(EmailService.class);
        service.addObserver(new EmailNotifier(mockEmailService));

        // Create an overdue book for the user
        Book b = service.addBook("Book 300", "Author", "300");
        service.borrowBook(u, "300");

        List<Book> books = service.search("");
        books.get(0).setDueDate(LocalDate.now().minusDays(2));

        // Save via reflection
        var m = BookService.class.getDeclaredMethod("writeBooksToFile", List.class);
        m.setAccessible(true);
        m.invoke(service, books);

        // Call sendReminders
        service.sendReminders(List.of(u));

        // Verify email was called
        verify(mockEmailService, times(1)).sendEmail(eq(u.getId()), anyString());
    }*/
}
