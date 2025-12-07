package tests;

import org.junit.jupiter.api.Test;
import service.BookFineStrategy;

import static org.junit.jupiter.api.Assertions.*;

class bookFineStrategyTests {

    private final BookFineStrategy strategy = new BookFineStrategy();

    @Test
    void calculateFineReturnsCorrectAmount() {
        assertEquals(20, strategy.calculateFine(1));
        assertEquals(40, strategy.calculateFine(2));
        assertEquals(200, strategy.calculateFine(10));
    }

    @Test
    void calculateFineReturnsZeroIfNoOverdue() {
        assertEquals(0, strategy.calculateFine(0));
    }

    @Test
    void calculateFineHandlesNegativeOverdueDays() {
        assertEquals(-20, strategy.calculateFine(-1)); 
    }
}
