package tests;

import org.junit.jupiter.api.Test;
import service.CDFineStrategy;

import static org.junit.jupiter.api.Assertions.*;

class cdFineStrategyTests {

    private final CDFineStrategy strategy = new CDFineStrategy();

    @Test
    void calculateFineReturnsCorrectAmount() {
        assertEquals(10, strategy.calculateFine(1));
        assertEquals(50, strategy.calculateFine(5));
        assertEquals(100, strategy.calculateFine(10));
    }

    @Test
    void calculateFineReturnsZeroIfNoOverdue() {
        assertEquals(0, strategy.calculateFine(0));
    }

    @Test
    void calculateFineHandlesNegativeOverdueDays() {
        assertEquals(-10, strategy.calculateFine(-1));
    }
}
