package presentation;

import domain.*;
import service.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Stream;

public class Dashboard {

    private BookService bookService;
    private CDService cdService;  // New CD service
    private UserService userService;
    private Scanner sc = new Scanner(System.in);
    private Admin admin;
    private final int LEFT_WIDTH = 50;
    private final int RIGHT_WIDTH = 50;

    public Dashboard(Admin loggedInAdmin, BookService bookService, CDService cdService, UserService userService) {
        this.admin = loggedInAdmin;
        this.bookService = bookService;
        this.cdService = cdService;
        this.userService = userService;

        // Setup strategies & notifications
        bookService.setUserService(userService);
        bookService.setFineStrategy(new BookFineStrategy());
        bookService.addObserver(new EmailNotifier(new RealEmailService()));

        cdService.setUserService(userService);
        cdService.setFineStrategy(new CDFineStrategy()); // 20 NIS/day
        cdService.addObserver(new EmailNotifier(new RealEmailService()));
    }

    // ====================== MAIN MENU ======================
    public void showMenu() {
        while (true) {
            ConsoleUtils.clearConsole();
            printSplitHeader();
            printSplitMenuWithUsers();

            System.out.print(ConsoleColors.YELLOW + "\nChoose: " + ConsoleColors.RESET);
            String choice = sc.nextLine();

            switch (choice) {
                case "0" -> addBookOrCD();
                case "1" -> searchBookOrCD();
                case "2" -> borrowMedia();
                case "3" -> showOverdue();
                case "4" -> sendReminders();
                case "5" -> addLibrarian();
                case "6" -> unregisterUser();
                case "7" -> { return; } // Logout
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
        System.out.println(leftTop + rightTop);

        String leftTitle = ConsoleUtils.padRight("", (LEFT_WIDTH - "ADMIN DASHBOARD".length()) / 2)
                + "ADMIN DASHBOARD";
        leftTitle = ConsoleUtils.padRight(leftTitle, LEFT_WIDTH - 2);

        String rightTitle = ConsoleUtils.padRight("", (RIGHT_WIDTH - "USER OVERVIEW".length()) / 2)
                + "USER OVERVIEW";
        rightTitle = ConsoleUtils.padRight(rightTitle, RIGHT_WIDTH - 2);

        System.out.println(
                "|" + ConsoleColors.YELLOW + leftTitle + ConsoleColors.RESET + "|" +
                "|" + ConsoleColors.CYAN + rightTitle + ConsoleColors.RESET + "|"
        );

        System.out.println(leftTop + rightTop);
    }

    private void printSplitMenuWithUsers() {
        List<User> users = userService.getAllUsers();
        List<Media> overdueMedia = getAllOverdueMedia();

        String[] menu = {
                "  [0] Add Book/CD",
                "  [1] Search Book/CD",
                "  [2] Borrow Book/CD",
                "  [3] Show Overdue",
                "  [4] Send Reminders",
                "  [5] Add Librarian",
                "  [6] Unregister User",
                "  [7] Logout"
        };

        int max = Math.max(menu.length, users.size());

        for (int i = 0; i < max; i++) {
            String left = (i < menu.length) ? menu[i] : "";
            left = "|" + ConsoleUtils.padRight(left, LEFT_WIDTH - 2) + "|";

            String right = "";
            if (i < users.size()) {
                User u = users.get(i);
                boolean hasOverdue = overdueMedia.stream()
                        .anyMatch(m -> m.getBorrowedBy() != null &&
                                       m.getBorrowedBy().getId().equals(u.getId()));
                String statusText = hasOverdue ? "OVERDUE" : "OK";
                String statusColor = hasOverdue ? ConsoleColors.RED : ConsoleColors.GREEN;

                String plainRight = " Name: " + u.getName() +
                        " | ID: " + u.getId() +
                        " | " + statusText;
                plainRight = ConsoleUtils.padRight(plainRight, RIGHT_WIDTH - 2);
                plainRight = plainRight.replace(statusText,
                        statusColor + statusText + ConsoleColors.RESET);

                right = "|" + plainRight + "|";
            } else {
                right = "|" + ConsoleUtils.padRight("", RIGHT_WIDTH - 2) + "|";
            }

            System.out.println(left + right);
        }

        System.out.println("=".repeat(LEFT_WIDTH) + "=".repeat(RIGHT_WIDTH));
    }

    // ====================== ADD MEDIA ======================
    private void addBookOrCD() {
        System.out.print("Type (book/cd): "); String type = sc.nextLine().trim().toLowerCase();
        System.out.print("Title: "); String title = sc.nextLine();
        System.out.print("Author/Artist: "); String author = sc.nextLine();
        System.out.print("ID/ISBN: "); String id = sc.nextLine();

        try {
            if (type.equals("book")) {
            	Book b = new Book(title, author, id);
                bookService.addMedia(b);
                System.out.println(ConsoleColors.GREEN + "Added Book: " + b.getTitle() + ConsoleColors.RESET);
            } else if (type.equals("cd")) {
                CD c = new CD(title, author, id); 
                cdService.addMedia(c);
                System.out.println(ConsoleColors.GREEN + "Added CD: " + c.getTitle() + ConsoleColors.RESET);
            } else {
                System.out.println(ConsoleColors.RED + "Unknown type!" + ConsoleColors.RESET);
            }
        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + ex.getMessage() + ConsoleColors.RESET);
        }
        pause();
    }

    // ====================== SEARCH MEDIA ======================
    private void searchBookOrCD() {
        System.out.print("Query: "); String q = sc.nextLine();

        List<Book> books = bookService.search(q);
        List<CD> cds = cdService.search(q);

        if (books.isEmpty() && cds.isEmpty()) {
            System.out.println(ConsoleColors.RED + "No media found!" + ConsoleColors.RESET);
        } else {
            books.forEach(b -> System.out.println("Book: " + b.getTitle() + " | " + b.getAuthor() +
                    " | ISBN: " + b.getIsbn() + " | Available: " + b.isAvailable()));
            cds.forEach(c -> System.out.println("CD: " + c.getTitle() + " | " + c.getArtist() +
                    " | ID: " + c.getId() + " | Available: " + c.isAvailable()));
        }
        pause();
    }

    // ====================== BORROW MEDIA ======================
    private void borrowMedia() {
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

        System.out.print("Type (book/cd): "); String type = sc.nextLine().trim().toLowerCase();
        System.out.print("ID/ISBN: "); String mediaId = sc.nextLine();

        try {
            Media m;
            if (type.equals("book")) m = bookService.borrowMedia(user, mediaId);
            else if (type.equals("cd")) m = cdService.borrowMedia(user, mediaId);
            else throw new IllegalArgumentException("Unknown media type");

            System.out.println(ConsoleColors.GREEN + user.getName() +
                    " borrowed: " + m.getTitle() + " | Due: " + m.getDueDate() + ConsoleColors.RESET);

        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + ex.getMessage() + ConsoleColors.RESET);
        }
        pause();
    }

    // ====================== SHOW OVERDUE ======================
    private void showOverdue() {
        List<Media> overdue = getAllOverdueMedia(); // returns books + CDs
        if (overdue.isEmpty()) {
            System.out.println(ConsoleColors.GREEN + "No overdue media!" + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.RED + "----- OVERDUE MEDIA -----" + ConsoleColors.RESET);
            for (Media m : overdue) {
                User u = m.getBorrowedBy();
                int fine;
                if (m instanceof Book) fine = bookService.calculateFineForBook((Book)m);
                else fine = cdService.calculateFine((CD)m);

                System.out.println("Media: " + m.getTitle() +
                        " | User: " + u.getName() +
                        " | ID: " + u.getId() +
                        " | Due: " + m.getDueDate() +
                        " | Fine: " + fine + " NIS");
            }
        }
        pause();
    }


    private List<Media> getAllOverdueMedia() {
        List<Media> overdue = new ArrayList<>();
        overdue.addAll(bookService.getOverdueMedia());
        overdue.addAll(cdService.getOverdueMedia());
        return overdue;
    }

    // ====================== SEND REMINDERS ======================
    private void sendReminders() {
        List<Media> overdue = getAllOverdueMedia();
        if (overdue.isEmpty()) {
            System.out.println(ConsoleColors.GREEN + "No overdue media!" + ConsoleColors.RESET);
            pause();
            return;
        }

        Set<User> notified = new HashSet<>();
        for (Media m : overdue) {
            User u = m.getBorrowedBy();
            if (u == null || notified.contains(u)) continue;

            System.out.println(ConsoleColors.RED +
                    "Reminder -> User: " + u.getName() +
                    " | ID: " + u.getId() +
                    ConsoleColors.RESET);

            // Send reminder
            if (m instanceof Book) bookService.sendReminders(List.of(u));
            else if (m instanceof CD) cdService.sendReminders(List.of(u));

            notified.add(u);
        }
        pause();
    }

    // ====================== ADD LIBRARIAN ======================
    private void addLibrarian() {
        System.out.print("Username: "); String username = sc.nextLine().trim();
        System.out.print("Password: "); String password = sc.nextLine().trim();
        File file = new File("./data/librarians.txt");

        try {
            file.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(username + "," + password);
                bw.newLine();
            }
            System.out.println(ConsoleColors.GREEN + "Librarian added successfully!" + ConsoleColors.RESET);
        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + "Failed to add librarian: " + ex.getMessage() + ConsoleColors.RESET);
        }
        pause();
    }

    // ====================== UNREGISTER USER ======================
    private void unregisterUser() {
        System.out.print("User ID to unregister: "); String id = sc.nextLine();

        User user = userService.getAllUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (user == null) {
            System.out.println(ConsoleColors.RED + "User not found!" + ConsoleColors.RESET);
            return;
        }

        try {
            boolean success = admin.unregisterUser(user, userService, bookService); // keep bookService
            if (success) System.out.println(ConsoleColors.GREEN + "User unregistered successfully." + ConsoleColors.RESET);
        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + ex.getMessage() + ConsoleColors.RESET);
        }
        pause();
    }

    private void pause() {
        System.out.print(ConsoleColors.YELLOW + "Press Enter to continue..." + ConsoleColors.RESET);
        sc.nextLine();
    }
}
