package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Admin;

class adminTests {
    
    private Admin admin;

    @BeforeEach
    void initialize() {
        File file = new File("data/admins.txt");
        if (file.exists()) file.delete();
        admin = new Admin("admin", "1234");
    }

    @Test
    void loginFailName() throws IOException {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            Admin.loginThrow("wrongUser", "1234");
        });
        assertEquals("Invalid credentials!", ex.getMessage());
    }

    @Test
    void loginFailPassword() throws IOException {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            Admin.loginThrow("admin", "wrongPass");
        });
        assertEquals("Invalid credentials!", ex.getMessage());
    }

    @Test
    void loginSuccess() throws IOException {
        admin.login("1234");
        assertTrue(admin.isLoggedIn());
    }

    @Test
    void fileNotFound() {
        File file = new File("data/admins.txt");
        if (file.exists()) file.delete();
        Admin temp = new Admin("temp", "temp");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            temp.login("temp");
        });
        assertEquals("Admin file not found!", ex.getMessage());
    }

    @Test
    void logoutAfterLogin() throws IOException {
        admin.login("1234");
        admin.logout();
        assertFalse(admin.isLoggedIn());
    }

    @Test
    void logoutWithoutLogin() {
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            admin.logout();  
        });
        assertEquals("Admin is not logged in!", ex.getMessage());
    }

    @Test
    void multipleAdminsLoginSuccess() throws IOException {
        Admin secondAdmin = new Admin("yousef", "5678");
        secondAdmin.login("5678");
        assertTrue(secondAdmin.isLoggedIn());
        admin.login("1234");
        assertTrue(admin.isLoggedIn());
    }

    @Test
    void multipleAdminsLoginFail() throws IOException {
        Admin secondAdmin = new Admin("yousef", "5678");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            Admin.loginThrow("yousef", "wrongPass");
        });
        assertEquals("Invalid credentials!", ex.getMessage());
    }

    @Test
    void loginThrowLoopCoverage() throws IOException {
        Admin a1 = new Admin("a1", "p1");
        Admin a2 = new Admin("a2", "p2");
        Admin.loginThrow("a2", "p2");
    }

    @Test
    void constructorIOException() {
        File file = new File("data");
        file.setReadOnly();
        new Admin("fail", "fail");
        file.setWritable(true);
    }
}
