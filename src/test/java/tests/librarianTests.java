package tests;

import domain.Book;
import domain.CD;
import domain.Librarian;
import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.BookService;
import service.CDService;
import service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class librarianTests {

    private Librarian librarian;
    private BookService bookService;
    private CDService cdService;
    private UserService userService;

    @BeforeEach
    void setup() {
        bookService = mock(BookService.class);
        cdService = mock(CDService.class);
        userService = mock(UserService.class);
        librarian = new Librarian("lib1", "password123", bookService, cdService);
    }

    @Test
    void loginWithWrongCredentialsThrows() {
        assertThrows(Exception.class, () -> Librarian.loginThrow("user", "pass"));
    }

    @Test
    void applyFinesForOverdueBook() {
        User borrower = mock(User.class);
        Book book = mock(Book.class);
        when(book.getBorrowedBy()).thenReturn(borrower);
        when(book.getDueDate()).thenReturn(LocalDate.now().minusDays(3));
        when(book.getFineApplied()).thenReturn(0);
        when(bookService.getOverdueMedia()).thenReturn(List.of(book));
        when(bookService.calculateFine(book)).thenReturn(15);
        librarian.checkOverdueAndIssueFines(userService);
        verify(userService).applyFine(borrower, 15);
        verify(book).setFineApplied(1);
        verify(bookService).writeToFile(List.of(book));
    }

    @Test
    void applyFinesForOverdueCD() {
        User borrower = mock(User.class);
        CD cd = mock(CD.class);
        
        when(cd.getBorrowedBy()).thenReturn(borrower);
        when(cd.getDueDate()).thenReturn(LocalDate.now().minusDays(2));
        when(cd.getFineApplied()).thenReturn(0);
        when(cdService.getOverdueMedia()).thenReturn(List.of(cd));
        when(cdService.calculateFine(cd)).thenReturn(10);
        librarian.checkOverdueAndIssueFines(userService);
        verify(userService).applyFine(borrower, 10);
        verify(cd).setFineApplied(1);
        verify(cdService).writeToFile(List.of(cd));
    }

    @Test
    void finesNotAppliedTwiceInSameDay() {
        User borrower = mock(User.class);
        Book book = mock(Book.class);
        when(book.getBorrowedBy()).thenReturn(borrower);
        when(book.getDueDate()).thenReturn(LocalDate.now().minusDays(3));
        when(book.getFineApplied()).thenReturn(0);
        when(bookService.getOverdueMedia()).thenReturn(List.of(book));
        when(bookService.calculateFine(book)).thenReturn(15);
        librarian.checkOverdueAndIssueFines(userService);
        reset(userService, bookService, book);
        librarian.checkOverdueAndIssueFines(userService);
        verifyNoInteractions(userService);
        verifyNoInteractions(bookService);
    }
}
