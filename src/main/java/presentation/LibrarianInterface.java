package presentation;

import java.time.LocalDate;

import java.util.*;
import domain.*;
import service.*;
/**
 * The {@code LibrarianInterface} class provides a console-based interface
 * for librarians to manage overdue media and issue fines in the library system.
 * 
 * <p>Example usage:
 * <pre><code>
 * Librarian librarian = new Librarian("libUser");
 * LibrarianInterface libInterface = new LibrarianInterface(librarian, userService, bookService, cdService);
 * libInterface.showMenu();
 * </code></pre>
 * 
 * @see Librarian
 * @see UserService
 * @see BookService
 * @see CDService
 */

public class LibrarianInterface {

    private Scanner sc = new Scanner(System.in);
    private Librarian librarian;
    private UserService userService;
    private BookService bookService;
    private CDService cdService;

    private final int leftWidth = 60;
    private final int rightWidth = 50;

    
    /**
     * Constructs a LibrarianInterface for the specified librarian and services.
     *
     * @param librarian the librarian using this interface
     * @param userService the user service for managing users
     * @param bookService the book service for managing books
     * @param cdService the CD service for managing CDs
     */

    public LibrarianInterface(Librarian librarian, UserService userService, BookService bookService, CDService cdService) {
        this.librarian = librarian;
        this.userService = userService;
        this.bookService = bookService;
        this.cdService = cdService;
    }

    public void showMenu() {
        while (true) {
            ConsoleUtils.clearConsole();
            printHeader();
            printSplitMediaAndUsers();

            System.out.print(ConsoleColors.YELLOW + "\nChoose: " + ConsoleColors.RESET);
            String choice = sc.nextLine();

            switch (choice) {
                case "0" -> detectOverdueMedia();
                case "1" -> { return; } // Logout
                default -> {
                    System.out.println(ConsoleColors.RED + "❌ Invalid choice!" + ConsoleColors.RESET);
                    pause();
                }
            }
        }
    }

    private void printHeader() {
        int totalWidth = leftWidth + rightWidth;
        System.out.println("=".repeat(totalWidth));
        String title = " LIBRARIAN MENU ";
        int padding = (totalWidth - title.length()) / 2;
        System.out.println(" ".repeat(Math.max(padding, 0)) + ConsoleColors.CYAN + title + ConsoleColors.RESET);
        System.out.println("-".repeat(totalWidth));
        String[] menu = {
                " [0] Detect Overdue Media & Issue Fines",
                " [1] Logout"
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

            User u = m.getBorrowedBy();
            String right;
            if (u != null) {
                String status = "OK";
                if (m.getDueDate() != null && m.getDueDate().isBefore(LocalDate.now())) {
                    status = "OVERDUE";
                }

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

    /**
     * Pads the given text to the right with spaces to the specified width.
     * 
     * @param text the text to pad
     * @param width the total width of the returned string
     * @return the padded string
     */
    private String padRight(String text, int width) {
        if (text.length() >= width) return text.substring(0, width);
        return text + " ".repeat(width - text.length());
    }

    private void detectOverdueMedia() {
        System.out.println(ConsoleColors.GREEN + " Checking overdue media..." + ConsoleColors.RESET);
        librarian.checkOverdueAndIssueFines(userService);
        System.out.println(ConsoleColors.GREEN + " Overdue check complete." + ConsoleColors.RESET);
        pause();
    }

    private void pause() {
        System.out.println("\nPress Enter to continue...");
        sc.nextLine();
    }
}
