package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import domain.User;

class userTests {

   /* @Test
    void createUserSuccess() {
        User u = new User("Alice", "U01");
        assertEquals("Alice", u.getName());
        assertEquals("U01", u.getId());
        assertEquals(0, u.getFineBalance());
        assertTrue(u.canBorrow());
    }

    @Test
    void addAndPayFine() {
        User u = new User("Bob", "B01");
        u.addFine(50);
        assertEquals(50, u.getFineBalance());
        assertFalse(u.canBorrow());

        u.payFine(20);
        assertEquals(30, u.getFineBalance());
        assertFalse(u.canBorrow());

        u.payFine(50);
        assertEquals(0, u.getFineBalance());
        assertTrue(u.canBorrow());
    }

    @Test
    void nullNameIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> new User(null, "123"));
        assertThrows(IllegalArgumentException.class, () -> new User("Name", null));
    }

    @Test
    void negativeFineThrows() {
        User u = new User("X", "ID");
        assertThrows(IllegalArgumentException.class, () -> u.addFine(-1));
        assertThrows(IllegalArgumentException.class, () -> u.payFine(-1));
    }*/
}
