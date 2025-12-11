package presentation;

import domain.Admin;
import domain.Librarian;
import domain.User;
import service.*;

import java.util.Scanner;

/**
 * The {@code LibrarySystem} class contains the main entry point for the YouBak Library application.
 * It provides a console-based interface for logging in as an admin, user, or librarian, and
 * navigating to their respective dashboards or interfaces.
 * 
 * <p>Example usage:
 * <pre><code>
 * java LibrarySystem
 * </code></pre>
 * 
 * <p>Features:
 * <ul>
 *     <li>Admin login and dashboard access</li>
 *     <li>User login and library interface</li>
 *     <li>Librarian login and management interface</li>
 *     <li>Console-based menu navigation</li>
 * </ul>
 * 
 * @see Admin
 * @see User
 * @see Librarian
 * @see Dashboard
 * @see UserInterface
 * @see LibrarianInterface
 * @see LogIn
 */
public class LibrarySystem {

    private static final int TOTAL_WIDTH = 120;

    /**
     * The main entry point of the application. Displays the main menu for
     * login options and delegates to the respective interfaces.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserService userService = new UserService();
        BookService bookService = new BookService();
        CDService cdService = new CDService();
        bookService.setFineStrategy(new BookFineStrategy());
        cdService.setFineStrategy(new CDFineStrategy());
        bookService.setUserService(userService);
        cdService.setUserService(userService);
        LogIn loginHandler = new LogIn(userService);

        boolean running = true; // loop control flag
        while (running) {
            printHeader("YOUBAK LIBRARY SYSTEM");
            String[] options = {
                    " Admin Login",
                    " User Login",
                    " Librarian Login",
                    " Exit"
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
                        LibrarianInterface li = new LibrarianInterface(lib, userService, bookService, cdService);
                        li.showMenu();
                    }
                }
                case "3" -> {
                    System.out.println(ConsoleColors.GREEN + "Goodbye!" + ConsoleColors.RESET);
                    running = false; // stop the loop instead of System.exit()
                }
                default -> System.out.println(ConsoleColors.RED + "Invalid choice!" + ConsoleColors.RESET);
            }
        }

    }

    /**
     * Prints a centered header with a title for the console menu.
     * 
     * @param title the title to display
     */
    private static void printHeader(String title) {
        System.out.println("=".repeat(TOTAL_WIDTH));
        int padding = (TOTAL_WIDTH - title.length()) / 2;
        System.out.println(" ".repeat(Math.max(padding, 0)) + ConsoleColors.YELLOW + title + ConsoleColors.RESET);
        System.out.println("-".repeat(TOTAL_WIDTH));
    }
    /**
     * Prints a menu with options centered in the console.
     * 
     * @param options the menu options to display
     */
    private static void printCenteredMenu(String[] options) {
        int totalWidth = TOTAL_WIDTH;
        String menuLine = String.join("    ", options);
        int padding = (totalWidth - menuLine.length()) / 2;
        if (padding < 0) padding = 0;

        System.out.println(" ".repeat(padding) + menuLine);
        System.out.println("=".repeat(totalWidth));
    }
}
