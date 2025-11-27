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
	void initilize() {
	 admin= new Admin("admin", "1234");
	}

    @Test
    void loginFailDouble() throws IOException {
       
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Admin.loginThrow("wrongUser","wrongPass");
        });

        assertEquals("Invalid credentials!", exception.getMessage());
    }

    @Test
    void loginFailPassword() throws IOException {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Admin.loginThrow("admin","wrongPass");
        });

        assertEquals("Invalid credentials!", exception.getMessage());
    }

    @Test
    void loginSuccess() throws IOException {
        admin.login("1234");
        assertTrue(admin.isLoggedIn());
    }

    @Test
    void fileNotFound() throws IOException {
        Admin bla = new Admin("bla", "bla");
        
        File f = new File("data/admins.txt");
        if (f.exists()) f.delete();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            bla.login("bla");
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
    void logoutWithoutLogin() throws IOException{
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            admin.logout();  
        });
        assertEquals("Admin is not logged in!", ex.getMessage());
    }
}
