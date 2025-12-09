package tests;

import domain.CD;
import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class cdTests {

    private CD cd;
    private User user;

    @BeforeEach
    void setup() {
        cd = new CD("Thriller", "Jackson", "CD1");
        user = mock(User.class);
    }


    @Test
    void borrowCDSuccessfully() {
        cd.borrow(user);
        assertFalse(cd.isAvailable());
        assertEquals(user, cd.getBorrowedBy());
        assertEquals(LocalDate.now().plusDays(7), cd.getDueDate());
    }

    @Test
    void cannotBorrowAlreadyBorrowedCD() {
        cd.borrow(user);
        User anotherUser = mock(User.class);

        Exception ex = assertThrows(IllegalStateException.class,
                () -> cd.borrow(anotherUser));
        assertEquals("CD is already borrowed", ex.getMessage());
    }


    @Test
    void getIdReturnsCorrectValue() {
        assertEquals("CD1", cd.getId());
    }

    @Test
    void getArtistReturnsCorrectValue() {
        assertEquals("Jackson", cd.getArtist());
    }
}
