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

/**
 * The {@code UserInterface} class provides a console-based interface for library users.
 * Users can borrow media, view overdue media, and pay fines.
 * 
 * <p>Example usage:
 * <pre><code>
 * User user = userService.getUserById("123");
 * UserInterface ui = new UserInterface(user, bookService, cdService, userService);
 * ui.showMenu();
 * </code></pre>
 * 
 * @see User
 * @see BookService
 * @see CDService
 * @see UserService
 * @see BookFineStrategy
 * @see CDFineStrategy
 */
public class UserInterface {

    private static final int LEFT_WIDTH = 45;
    private static final int RIGHT_WIDTH = 70;

    private BookService bookService;
    private CDService cdService;
    private UserService userService;
    private Scanner sc = new Scanner(System.in);
    private User user;

    /**
     * Constructs a {@code UserInterface} for the specified user and services.
     * 
     * @param user the library user
     * @param bookService the service for managing books
     * @param cdService the service for managing CDs
     * @param userService the service for managing users
     */
    public UserInterface(User user, BookService bookService, CDService cdService, UserService userService) {
        this.user = user;
        this.bookService = bookService;
        this.cdService = cdService;
        this.userService = userService;
        bookService.setFineStrategy(new BookFineStrategy());
        cdService.setFineStrategy(new CDFineStrategy());
        EmailNotifier notifier = new EmailNotifier(new RealEmailService());
        bookService.addObserver(notifier);
        cdService.addObserver(notifier);
    }

    public void showMenu() {
        while (true) {
            int totalWidth = LEFT_WIDTH + RIGHT_WIDTH;
            System.out.println("=".repeat(totalWidth));
            String title = "YouBak Library";
            int padding = (totalWidth - title.length()) / 2;
            System.out.println(" ".repeat(Math.max(padding, 0)) + ConsoleColors.CYAN + title + ConsoleColors.RESET);
            System.out.println("-".repeat(totalWidth));
            String[] menu = {
                    " [0] Borrow Media",
                    " [1] My Overdue Media",
                    " [2] Pay Fine",
                    " [3] Logout"
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
            System.out.println("=".repeat(totalWidth));
            System.out.println("💰 Your Fine Balance: " + user.getFineBalance() + " NIS");
            System.out.println("=".repeat(totalWidth));
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


    private void borrowMedia() {
        boolean canBorrowBooks = bookService.canUserBorrow(user, new ArrayList<>(bookService.getOverdueMedia()));
        boolean canBorrowCDs = cdService.canUserBorrow(user, new ArrayList<>(cdService.getOverdueMedia()));

        if (!canBorrowBooks && !canBorrowCDs) {
            System.out.println(" Cannot borrow: you have overdue books/CDs or unpaid fines!");
            pause();
            return;
        }

        System.out.println("Choose media type: 0 - 📚 Book, 1 - 💿 CD");
        String typeChoice = sc.nextLine();
        System.out.print("Enter identifier (ISBN for book / title for CD): ");
        String id = sc.nextLine();
        try {
            if ("0".equals(typeChoice)) {
                if (!canBorrowBooks) {
                    System.out.println("⚠ Cannot borrow books: overdue or unpaid fines!");
                    return;
                }
                Book b = bookService.borrowMedia(user, id);
                System.out.println(" You borrowed 📚 Book: " + b.getTitle() +
                        " | Due: " + b.getDueDate() + " ");

            } else if ("1".equals(typeChoice)) {
                if (!canBorrowCDs) {
                    System.out.println(" Cannot borrow CDs: overdue or unpaid fines!");
                    return;
                }
                CD cd = cdService.borrowMedia(user, id);
                System.out.println(" You borrowed 💿 CD: " + cd.getTitle() +
                        " | Due: " + cd.getDueDate() + " ");

            } else {
                System.out.println(" Invalid media type!");
            }
        } catch (Exception ex) {
            System.out.println(" " + ex.getMessage());
        }
        pause();
    }


    private void showOverdue() {
        List<Media> overdueByUser = new ArrayList<>();
        for (Book b : bookService.getOverdueMedia()) {
            if (b.getBorrowedBy().equals(user)) overdueByUser.add(b);
        }
        for (CD cd : cdService.getOverdueMedia()) {
            if (cd.getBorrowedBy().equals(user)) overdueByUser.add(cd);
        }

        if (overdueByUser.isEmpty()) {
            System.out.println(" You have no overdue media!");
            pause();
            return;
        }

        System.out.println("-----  YOUR OVERDUE MEDIA  -----");
        for (Media m : overdueByUser) {
            int fine = 0;
            if (m instanceof Book b) {
                fine = bookService.calculateFine(b);
            } else if (m instanceof CD cd) {
                fine = cdService.calculateFine(cd);
            }

            System.out.println(
                    (m instanceof Book ? "📚 Book: " : "💿 CD: ") + m.getTitle() +
                    " | Due: " + m.getDueDate() +
                    " | Fine: " + fine + " NIS 💰"
            );
        }
        pause();
    }


    private void payFine() {
        if (user.getFineBalance() <= 0) {
            System.out.println(" You have no fines to pay!");
            pause();
            return;
        }
        System.out.println("💰 Your current fine balance: " + user.getFineBalance() + " NIS");
        System.out.print("Enter amount to pay: ");
        try {
            double amount = Double.parseDouble(sc.nextLine());

            if (amount <= 0) {
                System.out.println(" Amount must be greater than 0!");
            } else if (amount > user.getFineBalance()) {
                System.out.println(" You cannot pay more than your current fine balance!");
            } else {
                userService.payFine(user, amount, bookService, cdService);
                user = userService.getAllUsers().stream()
                        .filter(u -> u.equals(user))
                        .findFirst()
                        .orElse(user);

                System.out.println(" Paid " + amount + " NIS. Remaining balance: " + user.getFineBalance() + " NIS 💰");

                if (user.getFineBalance() == 0) {
                    System.out.println(" All your overdue media fines have been cleared!");
                }
            }
        } catch (NumberFormatException ex) {
            System.out.println(" Invalid amount entered!");
        }
        pause();
    }


    private void pause() {
        System.out.print("Press Enter to continue...");
        sc.nextLine();
    }
}
