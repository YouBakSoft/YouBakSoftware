package presentation;

import domain.Book;
import domain.User;
import service.*;
import presentation.ConsoleUtils;
import java.util.List;
import java.util.Scanner;

public class Dashboard {

    private BookService bookService;
    private UserService userService;
    private Scanner sc = new Scanner(System.in);

    private final int LEFT_WIDTH = 50;
    private final int RIGHT_WIDTH = 50;

    public Dashboard() {
    	userService = new UserService();           // create users first
    	bookService = new BookService();
    	bookService.setUserService(userService);   // inject it
    	bookService.setFineStrategy(new BookFineStrategy());
    	bookService.addObserver(new EmailNotifier(new MockEmailService()));

    }

    // ====================== MAIN MENU ======================
    public void showMenu() {
        while (true) {
        	ConsoleUtils .clearConsole();
            printSplitHeader();
            printSplitMenuWithUsers();

            System.out.print(ConsoleColors.YELLOW + "\nChoose: " + ConsoleColors.RESET);
            String choice = sc.nextLine();

            switch (choice) {
                case "0" -> addBook();
                case "1" -> searchBook();
                case "2" -> borrowBook();
                case "3" -> showOverdue();
                case "4" -> sendReminders();
                case "5" -> { return; }
                default -> {
                    System.out.println(ConsoleColors.RED + "Invalid choice!" + ConsoleColors.RESET);
                    pause();
                }
            }
        }
    }

    // ====================== SPLIT SCREEN ======================
    private void printSplitHeader() {

        String leftTop = "=".repeat(LEFT_WIDTH);
        String rightTop = "=".repeat(RIGHT_WIDTH);

        // TOP BORDER
        System.out.println(leftTop + rightTop);

        // CENTERED TITLES
        String leftTitle  = ConsoleUtils.padRight("", (LEFT_WIDTH  - "ADMIN DASHBOARD".length()) / 2)
                           + "ADMIN DASHBOARD";
        leftTitle = ConsoleUtils.padRight(leftTitle, LEFT_WIDTH - 2);

        String rightTitle = ConsoleUtils.padRight("", (RIGHT_WIDTH - "USER OVERVIEW".length()) / 2)
                           + "USER OVERVIEW";
        rightTitle = ConsoleUtils.padRight(rightTitle, RIGHT_WIDTH - 2);

        System.out.println(
                "|" + ConsoleColors.YELLOW + leftTitle  + ConsoleColors.RESET + "|" +
                "|" + ConsoleColors.CYAN   + rightTitle + ConsoleColors.RESET + "|"
        );

        // BOTTOM BORDER OF HEADER
        System.out.println(leftTop + rightTop);
    }



    private void printSplitMenuWithUsers() {

        List<User> users = userService.getAllUsers();
        List<Book> overdueBooks = bookService.getOverdueBooks();

        String[] menu = {
                "  [0] Add Book",
                "  [1] Search Book",
                "  [2] Borrow Book",
                "  [3] Show Overdue",
                "  [4] Send Reminders",
                "  [5] Logout"
        };

        int max = Math.max(menu.length, users.size());

        for (int i = 0; i < max; i++) {

            // ---------- LEFT SIDE ----------
            String left = (i < menu.length) ? menu[i] : "";
            left = "|" + ConsoleUtils.padRight(left, LEFT_WIDTH - 2) + "|";

            // ---------- RIGHT SIDE ----------
            String right = "";

            if (i < users.size()) {
                User u = users.get(i);

                boolean hasOverdue = overdueBooks.stream()
                        .anyMatch(b -> b.getBorrowedBy() != null &&
                                       b.getBorrowedBy().getId().equals(u.getId()));

                String statusText = hasOverdue ? "OVERDUE" : "OK";
                String statusColor = hasOverdue ? ConsoleColors.RED : ConsoleColors.GREEN;

                // build text WITHOUT colors
                String plainRight =
                        " Name: " + u.getName() +
                        " | ID: " + u.getId() +
                        " | " + statusText;

                // pad WITHOUT colors
                plainRight = ConsoleUtils.padRight(plainRight, RIGHT_WIDTH - 2);

                // inject color ONLY into the status word
                plainRight = plainRight.replace(
                        statusText,
                        statusColor + statusText + ConsoleColors.RESET
                );

                right = "|" + plainRight + "|";
            } else {
                right = "|" + ConsoleUtils.padRight("", RIGHT_WIDTH - 2) + "|";
            }

            System.out.println(left + right);
        }

        // ---------- BOTTOM BORDER ----------
        System.out.println("=".repeat(LEFT_WIDTH) + "=".repeat(RIGHT_WIDTH));
    }



    // ====================== ADD BOOK ======================
    private void addBook() {
        System.out.print("Title: "); String title = sc.nextLine();
        System.out.print("Author: "); String author = sc.nextLine();
        System.out.print("ISBN: "); String isbn = sc.nextLine();

        try {
            Book b = bookService.addBook(title, author, isbn);
            System.out.println(ConsoleColors.GREEN + "Added Book: " + b.getTitle() + ConsoleColors.RESET);
        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + ex.getMessage() + ConsoleColors.RESET);
        }
        pause();
    }

    // ====================== SEARCH BOOK ======================
    private void searchBook() {
        System.out.print("Query: "); String q = sc.nextLine();
        List<Book> results = bookService.search(q);

        if (results.isEmpty())
            System.out.println(ConsoleColors.RED + "No books found!" + ConsoleColors.RESET);
        else
            results.forEach(b ->
                    System.out.println(b.getTitle() + " | " + b.getAuthor() +
                            " | ISBN: " + b.getIsbn() + " | Available: " + b.isAvailable())
            );
        pause();
    }

    // ====================== BORROW BOOK ======================
    private void borrowBook() {
        System.out.print("User Name: "); String name = sc.nextLine();
        System.out.print("User ID: "); String id = sc.nextLine();

        User user = userService.getAllUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseGet(() -> {
                    User newUser = new User(name, id);
                    userService.addUser(newUser);
                    return newUser;
                });

        System.out.print("Book ISBN: "); String isbn = sc.nextLine();
        try {
            Book b = bookService.borrowBook(user, isbn);
            System.out.println(ConsoleColors.GREEN + user.getName() +
                    " borrowed: " + b.getTitle() +
                    " | Due: " + b.getDueDate() + ConsoleColors.RESET);
        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + ex.getMessage() + ConsoleColors.RESET);
        }
        pause();
    }

    // ====================== SHOW OVERDUE ======================
    private void showOverdue() {
        List<Book> overdue = bookService.getOverdueBooks();

        if (overdue.isEmpty())
            System.out.println(ConsoleColors.GREEN + "No overdue books!" + ConsoleColors.RESET);
        else {
            System.out.println(ConsoleColors.RED + "----- OVERDUE BOOKS -----" + ConsoleColors.RESET);
            overdue.forEach(b -> {
                User u = b.getBorrowedBy();
                int fine = bookService.calculateFineForBook(b);
                System.out.println("Book: " + b.getTitle() +
                        " | User: " + u.getName() +
                        " | ID: " + u.getId() +
                        " | Due: " + b.getDueDate() +
                        " | Fine: " + fine + " NIS");
            });
        }
        pause();
    }

    // ====================== SEND REMINDERS ======================
    private void sendReminders() {
        List<Book> overdue = bookService.getOverdueBooks();

        if (overdue.isEmpty()) {
            System.out.println(ConsoleColors.GREEN + "No overdue books!" + ConsoleColors.RESET);
            pause();
            return;
        }

        for (Book b : overdue) {
            if (b.getBorrowedBy() == null) continue;

            User u = b.getBorrowedBy();
            int fine = bookService.calculateFineForBook(b);

            System.out.println(ConsoleColors.RED +
                    "Reminder -> User: " + u.getName() +
                    " | ID: " + u.getId() +
                    " | Book: " + b.getTitle() +
                    " | Due: " + b.getDueDate() +
                    " | Fine: " + fine + " NIS" +
                    ConsoleColors.RESET);

            bookService.sendReminders(List.of(u));
        }
        pause();
    }
    private void pause() {
    	System.out.print(ConsoleColors.YELLOW + "Press Enter to continue..." + ConsoleColors.RESET); sc.nextLine(); }

}
