package service;

/**
 * Implementation of {@link FineStrategy} for CDs.
 * Calculates fines based on the number of overdue days.
 *
 * <p>Example usage:
 * <pre><code>
 * FineStrategy cdFine = new CDFineStrategy();
 * int fine = cdFine.calculateFine(5); // returns 50
 * </code></pre>
 *
 * @since 1.0
 * @see FineStrategy
 */
public class CDFineStrategy implements FineStrategy {

    /**
     * Calculates the fine for a CD based on overdue days.
     * The fine is 10 units per day.
     *
     * @param overdueDays The number of days the CD is overdue
     * @return The calculated fine
     * @since 1.0
     */
    @Override
    public int calculateFine(int overdueDays) {
        return 10 * overdueDays; 
    }

}
