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
 * Represents a Librarian in the library system.
 * A Librarian is a type of {@link Staff} who can manage borrowed media,
 * check overdue items, and issue fines to users.
 */
public class Librarian extends Staff {

    /** Service for managing books */
    private BookService bookService;

    /** Service for managing CDs */
    private CDService cdService;

    /** Tracks the last date fines were applied to prevent multiple fines in a single day */
    private LocalDate lastFineDate = null;

    /**
     * Constructs a new Librarian with the specified username, password,
     * and services for managing books and CDs.
     *
     * @param userName the username of the librarian
     * @param password the password of the librarian
     * @param bookService the service used for book management
     * @param cdService the service used for CD management
     */
    public Librarian(String userName, String password, BookService bookService, CDService cdService) {
        super(userName, password);
        this.bookService = bookService;
        this.cdService = cdService;
    }

    /**
     * Validates librarian login credentials by checking against the "data/librarians.txt" file.
     *
     * @param username the librarian's username
     * @param password the librarian's password
     * @throws IOException if an error occurs reading the file
     * @throws IllegalArgumentException if the file does not exist or credentials are invalid
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
     * Checks for overdue books and CDs and issues fines to users if necessary.
     * Ensures fines are applied only once per day.
     *
     * @param userService the service used to apply fines to users
     */
    public void checkOverdueAndIssueFines(UserService userService) {
        LocalDate today = LocalDate.now();
        if (lastFineDate != null && lastFineDate.equals(today)) {
            System.out.println(ConsoleColors.YELLOW + "Fines have already been applied today." + ConsoleColors.RESET);
            return; 
        }
        List<Book> overdueBooks = bookService.getOverdueMedia();
        applyFines(userService);
        List<CD> overdueCDs = cdService.getOverdueMedia();
        applyFines(userService);
        lastFineDate = today;
    }

    /**
     * Applies fines to overdue books and CDs, updates the media files, 
     * and prints fine information to the console.
     *
     * @param userService the service used to apply fines to users
     */
    private void applyFines(UserService userService) {
        // Apply fines to books
        List<Book> overdueBooks = bookService.getOverdueMedia();
        List<CD> overdueCDs = cdService.getOverdueMedia();
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
