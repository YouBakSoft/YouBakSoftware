package tests;

import domain.Media;
import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FineStrategy;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class mediaTests {

    private Media media;
    private User user;
    private FineStrategy fineStrategy;

    @BeforeEach
    void setup() {
        media = new Media("Test Media") {
            @Override
            public void borrow(User user) {
                this.borrowedBy = user;
                this.available = false;
                this.dueDate = LocalDate.now().plusDays(5);
            }
        };
        user = mock(User.class);
        fineStrategy = mock(FineStrategy.class);
        media.setFineStrategy(fineStrategy);
    }

    @Test
    void isOverdueReturnsFalseIfNoDueDate() {
        assertFalse(media.isOverdue());
    }

    @Test
    void isOverdueReturnsFalseIfDueDateInFuture() {
        media.setDueDate(LocalDate.now().plusDays(3));
        assertFalse(media.isOverdue());
    }

    @Test
    void isOverdueReturnsTrueIfPastDueDate() {
        media.setDueDate(LocalDate.now().minusDays(2));
        assertTrue(media.isOverdue());
    }

    @Test
    void calculateFineReturnsZeroIfAvailable() {
        media.setAvailable(true);
        media.setDueDate(LocalDate.now().minusDays(5));
        assertEquals(0, media.calculateFine());
    }

    @Test
    void calculateFineReturnsZeroIfNoStrategy() {
        media.setFineStrategy(null);
        media.setAvailable(false);
        media.setDueDate(LocalDate.now().minusDays(5));
        assertEquals(0, media.calculateFine());
    }

    @Test
    void calculateFineUsesStrategyWhenOverdue() {
        media.setAvailable(false);
        media.setDueDate(LocalDate.now().minusDays(3));

        when(fineStrategy.calculateFine(3)).thenReturn(15);

        assertEquals(15, media.calculateFine());
        verify(fineStrategy).calculateFine(3);
    }

    @Test
    void calculateFineReturnsZeroIfNotOverdue() {
        media.setAvailable(false);
        media.setDueDate(LocalDate.now().plusDays(2));
        assertEquals(0, media.calculateFine());
    }
}
