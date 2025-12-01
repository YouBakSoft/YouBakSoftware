package domain;

import service.BookService;
import service.CDService;
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
        applyFines(overdueBooks, userService);
        List<CD> overdueCDs = cdService.getOverdueMedia();
        applyFines(overdueCDs, userService);
        lastFineDate = today;
    }

    
    private <T extends Media> void applyFines(List<T> mediaList, UserService userService) {
        for (T m : mediaList) {
            User borrower = m.getBorrowedBy();
            if (borrower == null || m.getDueDate() == null) continue;

            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(m.getDueDate(), LocalDate.now());
            if (overdueDays > 0) {
                int fine = 0;
                if (m instanceof Book) {
                    fine = bookService.calculateFine((Book) m);
                } else if (m instanceof CD) {
                    fine = cdService.calculateFine((CD) m);
                }
                userService.applyFine(borrower, fine);
                System.out.println(ConsoleColors.RED + "Fine issued to " + borrower.getName() +
                        " (" + borrower.getId() + "): " + fine + " NIS" +
                        " | Overdue by " + overdueDays + " days | " + m.getTitle() + ConsoleColors.RESET);
            }
        }
    }
}
