package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import domain.Admin;
import domain.Session;

class adminTests {

	@Test
	void loginFailDouble() throws IOException {
		
	    new File("data").mkdirs();
	    try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/admins.txt"))) {
	        bw.write("admin,1234");
	    }

	    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
	        Admin.loginThrow("wrongUser", "wrongPass");
	    });

	    assertEquals("Invalid credentials!", exception.getMessage());
	}
    //fails first when admin does not exist then pass
	
	
	@Test
	void loginFailName() throws IOException {
		
	    new File("data").mkdirs();
	    try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/admins.txt"))) {
	        bw.write("admin,1234");
	    }

	    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
	        Admin.loginThrow("wrongUser", "1234");
	    });

	    assertEquals("Invalid credentials!", exception.getMessage());
	}
	
	
	@Test
	void loginFailPassword() throws IOException {
		
	    new File("data").mkdirs();
	    try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/admins.txt"))) {
	        bw.write("admin,1234");
	    }

	    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
	        Admin.loginThrow("admin", "wrongPass");
	    });

	    assertEquals("Invalid credentials!", exception.getMessage());
	}
    
    
	@Test
	void loginSuccess() throws IOException {
	    new File("data").mkdirs();
	    try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/admins.txt"))) {
	        bw.write("yousef,1234");
	    }
	    Admin.loginThrow("yousef", "1234");
	    assertTrue(true);
	}
    //pass when the user exists
	
	
	
	@Test
	void fileNotFound() {
	    File f = new File("data/admins.txt");
	    f.delete();

	    Exception ex = assertThrows(IllegalArgumentException.class, () -> {
	        Admin.loginThrow("bla", "bla");
	    });

	    assertEquals("Admin file not found!", ex.getMessage());
	}

    
    
	@Test
	void createAdmin() {
		
	    Admin admin = new Admin("admin", "1234");
	    assertEquals("admin", admin.getUserName());
	    assertEquals("1234", admin.getPassword());
	}

    
    
    @Test
    void logout() {
    	
        Session.login("admin");
        Session.logout();
        assertFalse(Session.isLoggedIn());
    }

}
