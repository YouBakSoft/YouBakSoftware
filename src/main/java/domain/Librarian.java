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

public class Librarian extends Staff {

    private BookService bookService;
    private CDService cdService;
    private LocalDate lastFineDate = null;

    public Librarian(String userName, String password, BookService bookService, CDService cdService) {
        super(userName, password);
        this.bookService = bookService;
        this.cdService = cdService;
    }
    
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

    
    private void applyFines(UserService userService) {
        // Get overdue media lists
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
            if (cd.getFineApplied() > 0) continue;  // skip if fine already applied
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
