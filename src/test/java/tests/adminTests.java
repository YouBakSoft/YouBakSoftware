package tests;

import domain.Admin;
import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.BookService;
import service.UserService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class adminTests {

    private Admin admin;
    private UserService userService;
    private BookService bookService;

    @BeforeEach
    void setup() throws IOException {
        File file = new File("data/admins.txt");
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("admin1,password123\n");
        }
        admin = new Admin("admin1", "password123");
        userService = mock(UserService.class);
        bookService = mock(BookService.class);
    }


    @Test
    void loginWithCorrectCredentials() throws IOException {
        assertDoesNotThrow(() -> Admin.loginThrow("admin1", "password123"));
    }

    @Test
    void loginWithWrongCredentials() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> Admin.loginThrow("admin1", "wrongpass"));
        assertEquals("Invalid credentials!", ex.getMessage());
    }

    @Test
    void adminCanLoginAndLogout() throws IOException {
        admin.login("password123");
        assertTrue(admin.isLoggedIn());

        admin.logout();
        assertFalse(admin.isLoggedIn());
    }

    @Test
    void logoutWithoutLoginFails() {
        Exception ex = assertThrows(IllegalStateException.class, admin::logout);
        assertEquals("Admin is not logged in!", ex.getMessage());
    }


    @Test
    void unregisterUserSuccess() {
        User user = mock(User.class);
        when(user.getFineBalance()).thenReturn(0.0);
        when(bookService.hasActiveLoans(user)).thenReturn(false);
        when(userService.unregisterUser(user)).thenReturn(true);

        admin.setLoggedIn(true);
        assertTrue(admin.unregisterUser(user, userService, bookService));
    }

    @Test
    void cannotUnregisterUserWithFines() {
        User user = mock(User.class);
        when(user.getFineBalance()).thenReturn(10.0);

        admin.setLoggedIn(true);
        Exception ex = assertThrows(IllegalStateException.class,
                () -> admin.unregisterUser(user, userService, bookService));
        assertEquals("User has unpaid fines", ex.getMessage());
    }

    @Test
    void cannotUnregisterUserWithActiveLoans() {
        User user = mock(User.class);
        when(user.getFineBalance()).thenReturn(0.0);
        when(bookService.hasActiveLoans(user)).thenReturn(true);

        admin.setLoggedIn(true);
        Exception ex = assertThrows(IllegalStateException.class,
                () -> admin.unregisterUser(user, userService, bookService));
        assertEquals("User still has borrowed books", ex.getMessage());
    }

    @Test
    void cannotUnregisterIfAdminNotLoggedIn() {
        User user = mock(User.class);
        when(user.getFineBalance()).thenReturn(0.0);
        when(bookService.hasActiveLoans(user)).thenReturn(false);

        Exception ex = assertThrows(IllegalStateException.class,
                () -> admin.unregisterUser(user, userService, bookService));
        assertEquals("Admin must be logged in", ex.getMessage());
    }
}
