package domain;

import service.BookService;
import service.CDService;
import service.ReportFine;
import service.UserService;
import presentation.ConsoleColors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a Librarian in the library.
 * A Librarian is a type of {@link Staff} who can manage borrowed books and CDs,
 * check for overdue items, and issue fines to users.
 *
 * <p>Example usage:
 * <pre><code>
 * Librarian librarian = new Librarian("lib1", "password123", bookService, cdService);
 * librarian.checkOverdueAndIssueFines(userService);
 * </code></pre>
 *
 * @since 1.0
 * @see Staff
 * @see BookService
 * @see CDService
 * @see UserService
 */
public class Librarian extends Staff {

    /** Service for managing books */
    private BookService bookService;

    /** Service for managing CDs */
    private CDService cdService;

    /** Last date fines were applied, to prevent multiple fines in a single day */
    private LocalDate lastFineDate = null;

    /**
     * Creates a new Librarian with a username, password, and media services.
     *
     * @param userName the librarian's username
     * @param password the librarian's password
     * @param bookService service for managing books
     * @param cdService service for managing CDs
     * @since 1.0
     */
    public Librarian(String userName, String password, BookService bookService, CDService cdService) {
        super(userName, password);
        this.bookService = bookService;
        this.cdService = cdService;
    }

    /**
     * Checks the librarian's credentials against the "data/librarians.txt" file.
     *
     * @param username librarian username
     * @param password librarian password
     * @throws IOException if the file can't be read
     * @throws IllegalArgumentException if credentials are invalid or file is missing
     * @since 1.0
     */
    public static void loginThrow(String username, String password) throws IOException {
        File file = new File("./data/librarians.txt"); 
        if (!file.exists()) throw new IllegalArgumentException("Librarian file not found!");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return; 
                }
            }
        }
        throw new IllegalArgumentException("Invalid credentials!");
    }

    /**
     * Checks for overdue books and CDs and applies fines if needed.
     * Ensures fines are applied only once per day.
     *
     * @param userService service to apply fines to users
     * @since 1.0
     */
    public void checkOverdueAndIssueFines(UserService userService) {
        LocalDate today = LocalDate.now();
        if (lastFineDate != null && lastFineDate.equals(today)) {
            System.out.println(ConsoleColors.YELLOW + "Fines have already been applied today." + ConsoleColors.RESET);
            return; 
        }
        applyFines(userService);
        lastFineDate = today;
    }

    /**
     * Applies fines to overdue books and CDs, updates the media files,
     * and prints fine info to the console.
     *
     * @param userService service to apply fines
     * @since 1.0
     */
    private void applyFines(UserService userService) {
        // Apply fines to books
        List<Book> overdueBooks = bookService.getOverdueMedia();
        boolean booksUpdated = false;

        for (Book b : overdueBooks) {
            User borrower = b.getBorrowedBy();
            if (borrower == null || b.getDueDate() == null) continue;
            if (b.getFineApplied() > 0) continue;

            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(b.getDueDate(), LocalDate.now());
            if (overdueDays > 0) {
                int fine = bookService.calculateFine(b);
                userService.applyFine(borrower, fine);
                ReportFine.generateFineReceipt(borrower, fine, false, b);
                System.out.println(ConsoleColors.RED + " Fine issued to " + borrower.getName() +
                        " (" + borrower.getId() + "): " + fine + " NIS 💰" +
                        " | Overdue by " + overdueDays + " days | 📚 " + b.getTitle() + ConsoleColors.RESET);
                b.setFineApplied(1);
                booksUpdated = true;
            }
        }
        if (booksUpdated) bookService.writeToFile(overdueBooks);

        // Apply fines to CDs
        List<CD> overdueCDs = cdService.getOverdueMedia();
        boolean cdsUpdated = false;

        for (CD cd : overdueCDs) {
            User borrower = cd.getBorrowedBy();
            if (borrower == null || cd.getDueDate() == null) continue;
            if (cd.getFineApplied() > 0) continue;

            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(cd.getDueDate(), LocalDate.now());
            if (overdueDays > 0) {
                int fine = cdService.calculateFine(cd);
                userService.applyFine(borrower, fine);
                ReportFine.generateFineReceipt(borrower, fine, false, cd);
                System.out.println(ConsoleColors.RED + " Fine issued to " + borrower.getName() +
                        " (" + borrower.getId() + "): " + fine + " NIS 💰" +
                        " | Overdue by " + overdueDays + " days | 💿 " + cd.getTitle() + ConsoleColors.RESET);
                cd.setFineApplied(1);
                cdsUpdated = true;
            }
        }
        if (cdsUpdated) cdService.writeToFile(overdueCDs);

        lastFineDate = LocalDate.now();
    }
}
