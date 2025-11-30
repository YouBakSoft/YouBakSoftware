package presentation;

import java.time.LocalDate;
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
        bookService.setFineStrategy(new BookFineStrategy()); // 10 NIS per overdue day
        cdService.setFineStrategy(new CDFineStrategy());     // 20 NIS per overdue day

        // Setup email notifications
        EmailNotifier notifier = new EmailNotifier(new RealEmailService());
        bookService.addObserver(notifier);
        cdService.addObserver(notifier);
    }

    public void showMenu() {
        while (true) {
            ConsoleUtils.printHeader("USER MENU");
            String[] options = {"Borrow Media", "My Overdue Media", "Pay Fine", "Logout"};
            ConsoleUtils.printMenu(options);

            System.out.print(ConsoleColors.YELLOW + "Choose: " + ConsoleColors.RESET);
            String choice = sc.nextLine();

            switch (choice) {
                case "0" -> borrowMedia();
                case "1" -> showOverdue();
                case "2" -> payFine();
                case "3" -> { return; }
                default -> System.out.println(ConsoleColors.RED + "Invalid choice!" + ConsoleColors.RESET);
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
                    System.out.println(ConsoleColors.RED + "Cannot borrow: overdue books or unpaid fines!" + ConsoleColors.RESET);
                    return;
                }
                Book b = bookService.borrowMedia(user, id);
                System.out.println(ConsoleColors.GREEN + "You borrowed Book: " + b.getTitle() +
                        " | Due: " + b.getDueDate() + ConsoleColors.RESET);

            } else if ("1".equals(typeChoice)) {
                if (!cdService.canUserBorrow(user, new ArrayList<>(cdService.getOverdueMedia()))) {
                    System.out.println(ConsoleColors.RED + "Cannot borrow: overdue CDs or unpaid fines!" + ConsoleColors.RESET);
                    return;
                }
                CD cd = cdService.borrowMedia(user, id);
                System.out.println(ConsoleColors.GREEN + "You borrowed CD: " + cd.getTitle() +
                        " | Due: " + cd.getDueDate() + ConsoleColors.RESET);

            } else {
                System.out.println(ConsoleColors.RED + "Invalid media type!" + ConsoleColors.RESET);
            }
        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + ex.getMessage() + ConsoleColors.RESET);
        }
    }

    private void showOverdue() {
        List<Media> allOverdue = new ArrayList<>();
        allOverdue.addAll(bookService.getOverdueMedia());
        allOverdue.addAll(cdService.getOverdueMedia());

        if (allOverdue.isEmpty()) {
            System.out.println(ConsoleColors.GREEN + "No overdue media!" + ConsoleColors.RESET);
            return;
        }

        System.out.println(ConsoleColors.RED + "----- OVERDUE MEDIA -----" + ConsoleColors.RESET);

        for (Media m : allOverdue) {
            int fine = 0;
            if (m instanceof Book b) {
                fine = bookService.calculateFine(b);  // Use BookService + BookFineStrategy
            } else if (m instanceof CD cd) {
                fine = cdService.calculateFine(cd);  // Use CDService + CDFineStrategy
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

            System.out.println(ConsoleColors.GREEN +
                    "Paid " + amount + " NIS. Remaining: " + user.getFineBalance() +
                    ConsoleColors.RESET);

            if (user.getFineBalance() == 0) {
                System.out.println(ConsoleColors.GREEN +
                        "All your overdue media have been returned automatically!" +
                        ConsoleColors.RESET);
            }

        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + "Invalid amount!" + ConsoleColors.RESET);
        }
    }
    private void pause() {
        System.out.print(ConsoleColors.YELLOW + "Press Enter to continue..." + ConsoleColors.RESET);
        sc.nextLine();
    }
}
