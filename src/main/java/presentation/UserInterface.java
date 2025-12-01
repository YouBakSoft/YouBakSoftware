package presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import domain.Book;
import domain.CD;
import domain.Media;
import domain.User;
import service.BookService;
import service.CDService;
import service.BookFineStrategy;
import service.CDFineStrategy;
import service.EmailNotifier;
import service.RealEmailService;
import service.UserService;

public class UserInterface {

    private static final int LEFT_WIDTH = 45;
    private static final int RIGHT_WIDTH = 70;

    private BookService bookService;
    private CDService cdService;
    private UserService userService;
    private Scanner sc = new Scanner(System.in);
    private User user;

    public UserInterface(User user, BookService bookService, CDService cdService, UserService userService) {
        this.user = user;
        this.bookService = bookService;
        this.cdService = cdService;
        this.userService = userService;

        // Setup fine strategies
        bookService.setFineStrategy(new BookFineStrategy());
        cdService.setFineStrategy(new CDFineStrategy());

        // Setup email notifications
        EmailNotifier notifier = new EmailNotifier(new RealEmailService());
        bookService.addObserver(notifier);
        cdService.addObserver(notifier);
    }

    public void showMenu() {
        while (true) {
            int totalWidth = LEFT_WIDTH + RIGHT_WIDTH;

            // ================= HEADER =================
            System.out.println("=".repeat(totalWidth));
            String title = "YouBak Library";
            int padding = (totalWidth - title.length()) / 2;
            System.out.println(" ".repeat(Math.max(padding, 0)) + ConsoleColors.CYAN + title + ConsoleColors.RESET);
            System.out.println("-".repeat(totalWidth));

            // ================= MENU =================
            String[] menu = {
                    "📚 [0] Borrow Media",
                    "⏰ [1] My Overdue Media",
                    "💰 [2] Pay Fine",
                    "🚪 [3] Logout"
            };

            StringBuilder menuLine = new StringBuilder();
            int lineLength = 0;
            for (String option : menu) {
                if (lineLength + option.length() + 4 > totalWidth) {
                    System.out.println(menuLine);
                    menuLine = new StringBuilder();
                    lineLength = 0;
                }
                menuLine.append(option).append("    ");
                lineLength += option.length() + 4;
            }
            if (menuLine.length() > 0) System.out.println(menuLine);

            // ================= FINE BALANCE =================
            System.out.println("=".repeat(totalWidth));
            System.out.println("💰 Your Fine Balance: " + user.getFineBalance() + " NIS");
            System.out.println("=".repeat(totalWidth));

            // ================= MEDIA LIST =================
            System.out.printf("|%-20s |%-10s |%-8s |%-10s |%n",
                    "TITLE", "ID", "TYPE", "AVAILABLE");
            System.out.println("-".repeat(totalWidth));

            List<Media> allMedia = new ArrayList<>();
            allMedia.addAll(bookService.getAllMedia());
            allMedia.addAll(cdService.getAllMedia());

            for (Media m : allMedia) {
                String available = m.isAvailable() ? "Yes" : "No";
                String type;
                String id;

                if (m instanceof Book b) {
                    type = "Book";
                    id = b.getIsbn();
                } else if (m instanceof CD cd) {
                    type = "CD";
                    id = cd.getId();
                } else {
                    type = "Unknown";
                    id = "-";
                }

                System.out.printf("|%-20s |%-10s |%-8s |%-10s |%n",
                        m.getTitle(),
                        id,
                        type,
                        available);
            }


            System.out.println("=".repeat(totalWidth));

            // ================= USER CHOICE =================
            System.out.print("Choose: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "0" -> borrowMedia();
                case "1" -> showOverdue();
                case "2" -> payFine();
                case "3" -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ====================== BORROW MEDIA ======================
    private void borrowMedia() {
        System.out.println("Choose media type: 0 - Book, 1 - CD");
        String typeChoice = sc.nextLine();

        System.out.print("Enter identifier (ISBN for book / title for CD): ");
        String id = sc.nextLine();

        try {
            if ("0".equals(typeChoice)) {
                if (!bookService.canUserBorrow(user, new ArrayList<>(bookService.getOverdueMedia()))) {
                    System.out.println("Cannot borrow: overdue books or unpaid fines!");
                    return;
                }
                Book b = bookService.borrowMedia(user, id);
                System.out.println("You borrowed Book: " + b.getTitle() +
                        " | Due: " + b.getDueDate());

            } else if ("1".equals(typeChoice)) {
                if (!cdService.canUserBorrow(user, new ArrayList<>(cdService.getOverdueMedia()))) {
                    System.out.println("Cannot borrow: overdue CDs or unpaid fines!");
                    return;
                }
                CD cd = cdService.borrowMedia(user, id);
                System.out.println("You borrowed CD: " + cd.getTitle() +
                        " | Due: " + cd.getDueDate());

            } else {
                System.out.println("Invalid media type!");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        pause();
    }

    private void showOverdue() {
        List<Media> allOverdue = new ArrayList<>();
        allOverdue.addAll(bookService.getOverdueMedia());
        allOverdue.addAll(cdService.getOverdueMedia());

        if (allOverdue.isEmpty()) {
            System.out.println("No overdue media!");
            pause();
            return;
        }

        System.out.println("----- OVERDUE MEDIA -----");
        for (Media m : allOverdue) {
            int fine = 0;
            if (m instanceof Book b) {
                fine = bookService.calculateFine(b);
            } else if (m instanceof CD cd) {
                fine = cdService.calculateFine(cd);
            }

            User borrower = m.getBorrowedBy();
            System.out.println("Media: " + m.getTitle() +
                    " | User: " + borrower.getName() +
                    " | ID: " + borrower.getId() +
                    " | Due: " + m.getDueDate() +
                    " | Fine: " + fine + " NIS");
        }
        pause();
    }

    // ====================== PAY FINE ======================
    private void payFine() {
        System.out.print("Amount: ");

        try {
            double amount = Double.parseDouble(sc.nextLine());
            userService.payFine(user, amount, bookService, cdService);

            // Refresh user from file
            user = userService.getAllUsers().stream()
                    .filter(u -> u.equals(user))
                    .findFirst()
                    .orElse(user);

            System.out.println("Paid " + amount + " NIS. Remaining: " + user.getFineBalance());

            if (user.getFineBalance() == 0) {
                System.out.println("All your overdue media have been returned automatically!");
            }

        } catch (Exception ex) {
            System.out.println("Invalid amount!");
        }
        pause();
    }

    private void pause() {
        System.out.print("Press Enter to continue...");
        sc.nextLine();
    }
}
