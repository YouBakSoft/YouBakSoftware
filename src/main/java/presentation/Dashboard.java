package presentation;

import domain.*;
import service.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.time.LocalDate;

public class Dashboard {

    private BookService bookService;
    private CDService cdService;
    private UserService userService;
    private Scanner sc = new Scanner(System.in);
    private Admin admin;

    private final int LEFT_WIDTH = 60;
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
        cdService.setFineStrategy(new CDFineStrategy());
        cdService.addObserver(new EmailNotifier(new RealEmailService()));
    }

    public void showMenu() {
        while (true) {
            ConsoleUtils.clearConsole();
            printHeader();
            printSplitMediaAndUsers();

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

    private void printHeader() {
        int totalWidth = LEFT_WIDTH + RIGHT_WIDTH;
        System.out.println("=".repeat(totalWidth));
        String title = "ADMIN DASHBOARD";
        int padding = (totalWidth - title.length()) / 2;
        System.out.println(" ".repeat(Math.max(padding, 0)) + ConsoleColors.YELLOW + title + ConsoleColors.RESET);

        System.out.println("-".repeat(totalWidth));

        // Menu options
        String[] menu = {
        	    "➕ [0] Add Book/CD",
        	    "🔍 [1] Search Book/CD",
        	    "📚 [2] Borrow Book/CD",
        	    "⏰ [3] Show Overdue",
        	    "✉️ [4] Send Reminders",
        	    "👤➕ [5] Add Librarian",
        	    "👤❌ [6] Unregister User",
        	    "🚪 [7] Logout"
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
    }

    private void printSplitMediaAndUsers() {
        List<Media> allMedia = new ArrayList<>();
        allMedia.addAll(bookService.getAllMedia());
        allMedia.addAll(cdService.getAllMedia());
        List<Media> overdue = getAllOverdueMedia();
        int colTitle = 20, colId = 10, colType = 8, colAvail = 10;
        int colUName = 20, colUID = 10, colStatus = 10;
        
        String leftHeader = String.format("%-" + colTitle + "s | %-" + colId + "s | %-" + colType + "s | %-" + colAvail + "s",
                "TITLE", "ID", "TYPE", "AVAILABLE");
        String rightHeader = String.format("%-" + colUName + "s | %-" + colUID + "s | %-" + colStatus + "s",
                "USER NAME", "ID", "STATUS");

        System.out.println("|" + padRight(leftHeader, colTitle + colId + colType + colAvail + 9) + " || " +
                           padRight(rightHeader, colUName + colUID + colStatus + 6) + "|");
        System.out.println("-".repeat(colTitle + colId + colType + colAvail + colUName + colUID + colStatus + 17));

        for (Media m : allMedia) {
            String type = (m instanceof Book) ? "Book" : "CD";
            String id = (m instanceof Book) ? ((Book) m).getIsbn() :
                        (m instanceof CD) ? ((CD) m).getId() : "-";
            String avail = m.isAvailable() ? "Yes" : "No";

            String left = String.format("%-" + colTitle + "s | %-" + colId + "s | %-" + colType + "s | %-" + colAvail + "s",
                    m.getTitle(), id, type, avail);

            String right = "";
            User u = m.getBorrowedBy();
            if (u != null) {
                String status = overdue.contains(m) ? "OVERDUE" : "OK";

                right = String.format("%-" + colUName + "s | %-" + colUID + "s | %-" + colStatus + "s",
                        u.getName(), u.getId(), status);
            } else {
                right = String.format("%-" + colUName + "s | %-" + colUID + "s | %-" + colStatus + "s",
                        "", "", "");
            }

            System.out.println("|" + padRight(left, colTitle + colId + colType + colAvail + 9) + " || " +
                               padRight(right, colUName + colUID + colStatus + 6) + "|");
        }

        System.out.println("=".repeat(colTitle + colId + colType + colAvail + colUName + colUID + colStatus + 17));
    }






    private String padRight(String text, int width) {
        if (text.length() >= width) return text.substring(0, width);
        return text + " ".repeat(width - text.length());
    }

    private List<Media> getAllOverdueMedia() {
        List<Media> overdue = new ArrayList<>();
        overdue.addAll(bookService.getOverdueMedia());
        overdue.addAll(cdService.getOverdueMedia());
        return overdue;
    }

    private void addBookOrCD() {
        System.out.print("📦 Type (book/cd): ");
        String type = sc.nextLine().trim().toLowerCase();

        System.out.print("📖 Title: ");
        String title = sc.nextLine();

        System.out.print("✍ Author/Artist: ");
        String author = sc.nextLine();

        System.out.print("🆔 ID/ISBN: ");
        String id = sc.nextLine();

        try {
            if (type.equals("book")) {
                Book b = new Book(title, author, id);
                bookService.addMedia(b);
                System.out.println(ConsoleColors.GREEN + "✅ Added Book: " + b.getTitle() + ConsoleColors.RESET);

            } else if (type.equals("cd")) {
                CD c = new CD(title, author, id);
                cdService.addMedia(c);
                System.out.println(ConsoleColors.GREEN + "✅ Added CD: " + c.getTitle() + ConsoleColors.RESET);

            } else {
                System.out.println(ConsoleColors.RED + "❌ Unknown type!" + ConsoleColors.RESET);
            }

        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + "⚠️ " + ex.getMessage() + ConsoleColors.RESET);
        }

        pause();
    }

    private void searchBookOrCD() {
        System.out.print("🔍 Query: ");
        String q = sc.nextLine();

        List<Book> books = bookService.search(q);
        List<CD> cds = cdService.search(q);

        if (books.isEmpty() && cds.isEmpty()) {
            System.out.println(ConsoleColors.RED + "❌ No media found!" + ConsoleColors.RESET);

        } else {
            books.forEach(b -> System.out.println(
                    "📘 Book: " + b.getTitle() +
                    " | ✍ " + b.getAuthor() +
                    " | 🆔 ISBN: " + b.getIsbn() +
                    " | " + (b.isAvailable()
                            ? ConsoleColors.GREEN + "✅ Available"
                            : ConsoleColors.RED + "❌ Borrowed")
                            + ConsoleColors.RESET
            ));

            cds.forEach(c -> System.out.println(
                    "💿 CD: " + c.getTitle() +
                    " | 🎤 " + c.getArtist() +
                    " | 🆔 ID: " + c.getId() +
                    " | " + (c.isAvailable()
                            ? ConsoleColors.GREEN + "✅ Available"
                            : ConsoleColors.RED + "❌ Borrowed")
                            + ConsoleColors.RESET
            ));
        }

        pause();
    }


    private void borrowMedia() {
        System.out.print("👤 User Name: ");
        String name = sc.nextLine();

        System.out.print("🆔 User ID: ");
        String id = sc.nextLine();

        User user = userService.getAllUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseGet(() -> {
                    User newUser = new User(name, id);
                    userService.addUser(newUser);
                    return newUser;
                });

        System.out.print("📦 Type (book/cd): ");
        String type = sc.nextLine().trim().toLowerCase();

        System.out.print("📌 ID/ISBN: ");
        String mediaId = sc.nextLine();

        try {
            Media m;
            if (type.equals("book"))
                m = bookService.borrowMedia(user, mediaId);
            else if (type.equals("cd"))
                m = cdService.borrowMedia(user, mediaId);
            else
                throw new IllegalArgumentException("❌ Unknown media type");

            System.out.println(
                    ConsoleColors.GREEN + "✅ " + user.getName() +
                    " borrowed: " + m.getTitle() +
                    " | 📅 Due: " + m.getDueDate() +
                    ConsoleColors.RESET
            );

        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + "❌ " + ex.getMessage() + ConsoleColors.RESET);
        }

        pause();
    }


    private void showOverdue() {
        List<Media> overdue = getAllOverdueMedia();

        if (overdue.isEmpty()) {
            System.out.println(ConsoleColors.GREEN + "✅ No overdue media!" + ConsoleColors.RESET);

        } else {
            System.out.println(ConsoleColors.RED + "⏰ ----- OVERDUE MEDIA ----- ⏰" + ConsoleColors.RESET);

            for (Media m : overdue) {
                User u = m.getBorrowedBy();

                int fine = (m instanceof Book)
                        ? bookService.calculateFine((Book) m)
                        : cdService.calculateFine((CD) m);

                String mediaIcon = (m instanceof Book) ? "📘" : "💿";

                System.out.println(
                        mediaIcon + " Media: " + m.getTitle() +
                        " | 👤 User: " + u.getName() +
                        " | 🆔 ID: " + u.getId() +
                        " | 📅 Due: " + m.getDueDate() +
                        " | 💰 Fine: " + ConsoleColors.YELLOW + fine + " NIS" + ConsoleColors.RESET
                );
            }
        }

        pause();
    }


    private void sendReminders() {
        List<Media> overdue = getAllOverdueMedia();

        if (overdue.isEmpty()) {
            System.out.println(ConsoleColors.GREEN + "✅ No overdue media!" + ConsoleColors.RESET);
            pause();
            return;
        }

        Set<User> notified = new HashSet<>();

        for (Media m : overdue) {
            User u = m.getBorrowedBy();
            if (u == null || notified.contains(u)) continue;

            System.out.println(
                    ConsoleColors.RED +
                    "📢 Reminder Sent -> 👤 User: " + u.getName() +
                    " | 🆔 ID: " + u.getId() +
                    ConsoleColors.RESET
            );

            if (m instanceof Book)
                bookService.sendReminders(List.of(u), "book");
            else if (m instanceof CD)
                cdService.sendReminders(List.of(u), "CD");

            notified.add(u);
        }

        pause();
    }

    private void addLibrarian() {
        System.out.print("👤 Username: ");
        String username = sc.nextLine().trim();

        System.out.print("🔑 Password: ");
        String password = sc.nextLine().trim();

        File file = new File("./data/librarians.txt");

        try {
            file.getParentFile().mkdirs();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(username + "," + password);
                bw.newLine();
            }

            System.out.println(ConsoleColors.GREEN + "✅ Librarian added successfully!" + ConsoleColors.RESET);

        } catch (Exception ex) {
            System.out.println(
                    ConsoleColors.RED + "❌ Failed to add librarian: " + ex.getMessage() + ConsoleColors.RESET
            );
        }

        pause();
    }

    private void unregisterUser() {
        System.out.print("🗑️ User ID to unregister: ");
        String id = sc.nextLine();

        User user = userService.getAllUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (user == null) {
            System.out.println(ConsoleColors.RED + "❌ User not found!" + ConsoleColors.RESET);
            pause();
            return;
        }

        // Check if the user has active loans
        boolean hasLoans = bookService.hasActiveLoans(user) || cdService.hasActiveLoans(user);

        if (hasLoans) {
            System.out.println(
                    ConsoleColors.RED +
                    "⛔ Cannot unregister user: they have borrowed media!" +
                    ConsoleColors.RESET
            );
            pause();
            return;
        }

        try {
            boolean success = admin.unregisterUser(user, userService, bookService);

            if (success) {
                System.out.println(
                        ConsoleColors.GREEN +
                        "✅ User unregistered successfully." +
                        ConsoleColors.RESET
                );
            }

        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + "❌ " + ex.getMessage() + ConsoleColors.RESET);
        }

        pause();
    }



    private void pause() {
        System.out.print(ConsoleColors.YELLOW + "Press Enter to continue..." + ConsoleColors.RESET);
        sc.nextLine();
    }
}
