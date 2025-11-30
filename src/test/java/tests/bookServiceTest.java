package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import domain.Book;
import domain.User;
import service.BookService;
import service.EmailNotifier;
import service.EmailService;
import service.UserService;
import service.BookFineStrategy;

class bookServiceTest {

    private BookService service;
    private UserService mockUserService;
/*
    @BeforeEach
    void setup() {
        mockUserService = mock(UserService.class);
        service = new BookService() {
            private List<Book> testBooks = new ArrayList<>();
            @Override
			public List<Book> readBooksFromFile() {
                return testBooks;
            }
            @Override
			public void writeBooksToFile(List<Book> books) {
                testBooks = books;
            }
        };
        when(mockUserService.getAllUsers()).thenReturn(List.of(new User("Baker","100")));
        service.setUserService(mockUserService);
    }



    @Test
    void addBookSuccess() {
        Book b = service.addBook("Clean Code", "Robert Martin", "111");
        assertNotNull(b);
        assertEquals("Clean Code", b.getTitle());
    }
    @Test
    void overdueBookDetectionWithMockedTime() {
        User u = new User("Baker", "100");
        when(mockUserService.getAllUsers()).thenReturn(List.of(u));
        LocalDate borrowDate = LocalDate.of(2025, 11, 30);
        try (MockedStatic<LocalDate> mockedDate = mockStatic(LocalDate.class)) {
            mockedDate.when(LocalDate::now).thenReturn(borrowDate);

            Book b = service.addBook("Old Book", "A", "101");
            service.borrowBook(u, "101");
        }
        LocalDate checkDate = LocalDate.of(2025, 12, 30);
        try (MockedStatic<LocalDate> mockedDate = mockStatic(LocalDate.class)) {
            mockedDate.when(LocalDate::now).thenReturn(checkDate);

            List<Book> overdue = service.getOverdueBooks();

            assertEquals(1, overdue.size(), "There should be exactly 1 overdue book");
            assertEquals("101", overdue.get(0).getIsbn(), "The overdue book should have ISBN 101");
        }
    }



    @Test
    public void testCalculateFineWithStrategy() throws Exception {

        User u = new User("TestUser", "U01");
        Book b = service.addBook("Old Book", "Author", "200");
        service.borrowBook(u, "200");

        List<Book> allBooks = service.search("");
        allBooks.get(0).setDueDate(LocalDate.now().minusDays(3));

        var m = BookService.class.getDeclaredMethod("writeBooksToFile", List.class);
        m.setAccessible(true);
        m.invoke(service, allBooks);

        service.setFineStrategy(new BookFineStrategy());

        when(mockUserService.getAllUsers()).thenReturn(List.of(u));

        List<Book> overdueBooks = service.getOverdueBooks();
        for (Book book : overdueBooks) {
            int fine = service.calculateFineForBook(book);
            System.out.println(book.getTitle() + " - Fine: " + fine + " NIS");  
        }

        assertTrue(overdueBooks.size() > 0);
    }*/
}
