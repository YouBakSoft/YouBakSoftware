package service;

/**
 * Strategy interface for calculating fines for overdue media.
 * Implementations define how fines are computed based on the number of overdue days.
 *
 * <p>Example usage:
 * <pre><code>
 * FineStrategy bookStrategy = new BookFineStrategy();
 * int fine = bookStrategy.calculateFine(5); // 5 days overdue
 * </code></pre>
 *
 * @since 1.0
 * @see BookFineStrategy
 * @see CDFineStrategy
 */
public interface FineStrategy {

    /**
     * Calculates the fine based on the number of overdue days.
     *
     * @param overdueDays the number of days the item is overdue
     * @return the calculated fine amount
     * @since 1.0
     */
    int calculateFine(int overdueDays);
}
