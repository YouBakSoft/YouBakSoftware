package domain;

import service.BookService;
import service.CDService;
import service.MediaService;
import service.MultiMediaService;
import service.ReportFine;
import service.UserService;
import presentation.ConsoleColors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
     * Applies fines for overdue books and CDs
     * and prints fine info to the console.
     *
     * @param userService service to apply fines
     * @since 1.0
     */
    private void applyFines(UserService userService) {

        applyFinesForMedia(
                bookService.getOverdueMedia(),
                bookService,
                userService,
                "📚"
        );

        applyFinesForMedia(
                cdService.getOverdueMedia(),
                cdService,
                userService,
                "💿"
        );

        lastFineDate = LocalDate.now();
    }

    private <T extends Media> boolean applyFinesForMedia(
            List<T> overdueList,
            MediaService<T> service,
            UserService userService,
            String icon
    ) {
        boolean updated = false;

        for (T media : overdueList) {
            User borrower = media.getBorrowedBy();

            // Combined conditions → fixed bad smell
            if (borrower == null ||
                media.getDueDate() == null ||
                media.getFineApplied() > 0) {
                continue;
            }

            long overdueDays = ChronoUnit.DAYS.between(media.getDueDate(), LocalDate.now());

            if (overdueDays > 0) {
                int fine = service.calculateFine(media);
                userService.applyFine(borrower, fine);

                ReportFine.generateFineReceipt(borrower, fine, false, media);

                System.out.println(ConsoleColors.RED +
                        " Fine issued to " + borrower.getName() +
                        " (" + borrower.getId() + "): " + fine + " NIS 💰" +
                        " | Overdue by " + overdueDays + " days | " + icon + " " + media.getTitle() +
                        ConsoleColors.RESET);

                media.setFineApplied(1);
                updated = true;
            }
        }

        if (updated) service.writeToFile(overdueList);
        return updated;
    }



}
