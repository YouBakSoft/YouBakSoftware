package presentation;

import domain.Admin;
import domain.Librarian;
import domain.User;
import service.*;

import java.util.Scanner;

public class LibrarySystem {

    private static final int TOTAL_WIDTH = 120;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Services
        UserService userService = new UserService();
        BookService bookService = new BookService();
        CDService cdService = new CDService();

        // Fine strategies
        bookService.setFineStrategy(new BookFineStrategy());
        cdService.setFineStrategy(new CDFineStrategy());

        LogIn loginHandler = new LogIn(userService);

        while (true) {
            printHeader("YOUBAK LIBRARY SYSTEM");

            // Menu options
            String[] options = {
                    "👑 Admin Login",
                    "👤 User Login",
                    "📚 Librarian Login",
                    "🚪 Exit"
            };

            printCenteredMenu(options);

            System.out.print(ConsoleColors.YELLOW + "Choose: " + ConsoleColors.RESET);
            String choice = sc.nextLine();

            switch (choice) {
                case "0" -> {
                    Admin admin = loginHandler.adminLogin();
                    if (admin != null) {
                        Dashboard dashboard = new Dashboard(admin, bookService, cdService, userService);
                        dashboard.showMenu();
                    }
                }
                case "1" -> {
                    User user = loginHandler.userLogin();
                    if (user != null) {
                        UserInterface ui = new UserInterface(user, bookService, cdService, userService);
                        ui.showMenu();
                    }
                }
                case "2" -> {
                    Librarian lib = loginHandler.libLogin(bookService, cdService);
                    if (lib != null) {
                        LibrarianInterface li = new LibrarianInterface(lib, userService);
                        li.showMenu();
                    }
                }
                case "3" -> {
                    System.out.println(ConsoleColors.GREEN + "Goodbye!" + ConsoleColors.RESET);
                    System.exit(0);
                }
                default -> System.out.println(ConsoleColors.RED + "Invalid choice!" + ConsoleColors.RESET);
            }
        }
    }

    // ================== HEADER ==================
    private static void printHeader(String title) {
        System.out.println("=".repeat(TOTAL_WIDTH));
        int padding = (TOTAL_WIDTH - title.length()) / 2;
        System.out.println(" ".repeat(Math.max(padding, 0)) + ConsoleColors.YELLOW + title + ConsoleColors.RESET);
        System.out.println("-".repeat(TOTAL_WIDTH));
    }

    // ================== CENTERED MENU ==================
    private static void printCenteredMenu(String[] options) {
        int totalWidth = TOTAL_WIDTH;

        // Join options into a single line with spacing
        String menuLine = String.join("    ", options);

        // Calculate left padding to center the whole menu
        int padding = (totalWidth - menuLine.length()) / 2;
        if (padding < 0) padding = 0;

        System.out.println(" ".repeat(padding) + menuLine);
        System.out.println("=".repeat(totalWidth));
    }
}
