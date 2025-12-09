package presentation;

import java.io.IOException;
import java.util.Scanner;

import domain.Admin;
import domain.Librarian;
import domain.User;
import service.BookService;
import service.CDService;
import service.UserService;

/**
 * The {@code LogIn} class provides a console-based login interface 
 * for Admins, Users, and Librarians in the library system.
 * 
 * <p>Example usage:
 * <pre><code>
 * UserService userService = new UserService();
 * LogIn login = new LogIn(userService);
 * Admin admin = login.adminLogin();
 * User user = login.userLogin();
 * Librarian librarian = login.libLogin(bookService, cdService);
 * </code></pre>
 * 
 * @see Admin
 * @see User
 * @see Librarian
 * @see UserService
 * @see BookService
 * @see CDService
 */

public class LogIn {

    private static final int TOTAL_WIDTH = 120;
    private Scanner sc = new Scanner(System.in);
    private UserService userService;
    
    /**
     * Constructs a {@code LogIn} instance using the provided {@code UserService}.
     * 
     * @param userService the user service used to manage user login and registration
     */

    public LogIn(UserService userService) {
        this.userService = userService;
    }

    /**
     * Prompts the console for admin credentials and logs in an Admin.
     * 
     * @return the logged-in {@code Admin} object, or {@code null} if login failed
     */
    public Admin adminLogin() {
        printCenteredHeader("ADMIN LOGIN");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine(); 
        try {
            Admin admin = new Admin(username, password);
            admin.login(password);
            System.out.println(ConsoleColors.GREEN + centerText("Admin login successful!") + ConsoleColors.RESET);
            return admin;
        } catch (IOException ex) {
            System.out.println(ConsoleColors.RED + centerText("Admin file error!") + ConsoleColors.RESET);
        } catch (IllegalArgumentException ex) {
            System.out.println(ConsoleColors.RED + centerText(ex.getMessage()) + ConsoleColors.RESET);
        }

        return null;
    }

    /**
     * Prompts the console for user credentials and logs in a User.
     * Creates a new user if not already registered.
     * 
     * @return the logged-in {@code User} object, or {@code null} if login failed
     */
    public User userLogin() {
        printCenteredHeader("USER LOGIN");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String id = scanner.nextLine();
        System.out.print("ID: ");
        String email = scanner.nextLine();
        User user = userService.getAllUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (user == null) {
            user = new User(name, id,email);
            userService.addUser(user);
            System.out.println(ConsoleColors.GREEN + centerText("New user created successfully!") + ConsoleColors.RESET);
        } else if (!user.getName().equals(name)) {
            System.out.println(ConsoleColors.RED + centerText("ID already exists with a different name!") + ConsoleColors.RESET);
            return null;
        }
        System.out.println(ConsoleColors.GREEN + centerText("Welcome " + user.getName() + "!") + ConsoleColors.RESET);
        return user;
    }


    /**
     * Prompts the console for librarian credentials and logs in a Librarian.
     * 
     * @param bookService the {@code BookService} used by the librarian
     * @param cdService the {@code CDService} used by the librarian
     * @return the logged-in {@code Librarian} object, or {@code null} if login failed
     */
    public Librarian libLogin(BookService bookService, CDService cdService) {
        printCenteredHeader("LIBRARIAN LOGIN");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine(); 
        try {
            Librarian.loginThrow(user, pass);
            System.out.println(ConsoleColors.GREEN + centerText("Librarian login successful!") + ConsoleColors.RESET);
            return new Librarian(user, pass, bookService, cdService);
        } catch (IOException ex) {
            System.out.println(ConsoleColors.RED + centerText("Librarian file error!") + ConsoleColors.RESET);
        } catch (IllegalArgumentException ex) {
            System.out.println(ConsoleColors.RED + centerText(ex.getMessage()) + ConsoleColors.RESET);
        }
        return null;
    }


    private static void printCenteredHeader(String title) {
        System.out.println("=".repeat(TOTAL_WIDTH));
        int padding = (TOTAL_WIDTH - title.length()) / 2;
        System.out.println(" ".repeat(Math.max(padding, 0)) + ConsoleColors.PURPLE + title + ConsoleColors.RESET);
        System.out.println("-".repeat(TOTAL_WIDTH));
    }

    private static String promptCentered(String prompt) {
        int padding = (TOTAL_WIDTH - prompt.length()) / 2;
        System.out.print(" ".repeat(Math.max(padding, 0)) + prompt);
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    private static String centerText(String text) {
        int padding = (TOTAL_WIDTH - text.length()) / 2;
        if (padding < 0) padding = 0;
        return " ".repeat(padding) + text;
    }
}
