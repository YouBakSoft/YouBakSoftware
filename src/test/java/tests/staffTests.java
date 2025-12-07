package tests;

import domain.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class staffTests {

    private Staff staff;

    @BeforeEach
    void setup() {
        staff = new Staff("johnDoe", "password123");
    }

    @Test
    void getUsernameReturnsCorrectValue() {
        assertEquals("johnDoe", staff.getUserName());
    }

    @Test
    void setUsernameUpdatesValue() {
        staff.setUserName("newName");
        assertEquals("newName", staff.getUserName());
    }

    @Test
    void getPasswordReturnsCorrectValue() {
        assertEquals("password123", staff.getPassword());
    }

    @Test
    void setPasswordUpdatesValue() {
        staff.setPassword("newPass");
        assertEquals("newPass", staff.getPassword());
    }

    @Test
    void isLoggedInInitiallyFalse() {
        assertFalse(staff.isLoggedIn());
    }

    @Test
    void setLoggedInUpdatesValue() {
        staff.setLoggedIn(true);
        assertTrue(staff.isLoggedIn());
    }
}
