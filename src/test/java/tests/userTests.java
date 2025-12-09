package tests;

import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class userTests {

    private User user;

    @BeforeEach
    void setup() {
        user = new User("Alice", "U123", "alice@example.com");
    }

    @Test
    void addFineIncreasesBalance() {
        user.addFine(10.0);
        assertEquals(10.0, user.getFineBalance());
    }

    @Test
    void addFineThrowsIfNegativeOrZero() {
        assertThrows(IllegalArgumentException.class, () -> user.addFine(0));
        assertThrows(IllegalArgumentException.class, () -> user.addFine(-5));
    }

    @Test
    void payFineDecreasesBalance() {
        user.addFine(20);
        user.payFine(5);
        assertEquals(15, user.getFineBalance());
    }

    @Test
    void payFineResetsBalanceIfOverpay() {
        user.addFine(10);
        user.payFine(20);
        assertEquals(0, user.getFineBalance());
    }

    @Test
    void payFineThrowsIfNegativeOrZero() {
        assertThrows(IllegalArgumentException.class, () -> user.payFine(0));
        assertThrows(IllegalArgumentException.class, () -> user.payFine(-5));
    }

    @Test
    void canBorrowReturnsTrueIfNoFines() {
        assertTrue(user.canBorrow());
    }

    @Test
    void canBorrowReturnsFalseIfFinesExist() {
        user.addFine(5);
        assertFalse(user.canBorrow());
    }

    @Test
    void getNameAndIdReturnCorrectValues() {
        assertEquals("Alice", user.getName());
        assertEquals("U123", user.getId());
    }

    @Test
    void getEmailAndSetEmailWorkCorrectly() {
        assertEquals("alice@example.com", user.getEmail());
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void equalsAndHashCodeWorkBasedOnId() {
        User sameIdUser = new User("Bob", "U123", "bob@example.com");
        User differentUser = new User("Charlie", "U456", "charlie@example.com");

        assertEquals(user, sameIdUser);
        assertEquals(user.hashCode(), sameIdUser.hashCode());
        assertNotEquals(user, differentUser);
        assertNotEquals(user.hashCode(), differentUser.hashCode());
    }
}
