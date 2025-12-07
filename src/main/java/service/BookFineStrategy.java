package service;

/**
 * Implementation of {@link FineStrategy} for books.
 * Calculates fines based on the number of overdue days.
 *
 * <p>Example usage:
 * <pre><code>
 * FineStrategy bookFine = new BookFineStrategy();
 * int fine = bookFine.calculateFine(3); // returns 60
 * </code></pre>
 *
 * @since 1.0
 * @see FineStrategy
 */
public class BookFineStrategy implements FineStrategy {

    /**
     * Calculates the fine for a book based on overdue days.
     * The fine is 20 units per day.
     *
     * @param overdueDays The number of days the book is overdue
     * @return The calculated fine
     * @since 1.0
     */
    @Override
    public int calculateFine(int overdueDays) {
        return 20 * overdueDays; 
    }

}
