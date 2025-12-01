package presentation;

import java.io.IOException;
import java.util.Scanner;

import domain.Admin;
import domain.Librarian;
import domain.User;
import service.BookService;
import service.CDService;
import service.UserService;

public class LogIn {

    private static final int TOTAL_WIDTH = 120;
    private Scanner sc = new Scanner(System.in);
    private UserService userService;

    public LogIn(UserService userService) {
        this.userService = userService;
    }

    // ================= ADMIN LOGIN =================
    public Admin adminLogin() {
        printCenteredHeader("ADMIN LOGIN");

        String username = promptCentered("Username: ");
        String password = promptCentered("Password: ");

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

    // ================= USER LOGIN =================
    public User userLogin() {
        printCenteredHeader("USER LOGIN");

        String name = promptCentered("Name: ");
        String id = promptCentered("ID: ");

        // Try to find existing user
        User user = userService.getAllUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (user == null) {
            user = new User(name, id);
            userService.addUser(user);
            System.out.println(ConsoleColors.GREEN + centerText("New user created successfully!") + ConsoleColors.RESET);
        } else if (!user.getName().equals(name)) {
            System.out.println(ConsoleColors.RED + centerText("ID already exists with a different name!") + ConsoleColors.RESET);
            return null;
        }

        System.out.println(ConsoleColors.GREEN + centerText("Welcome " + user.getName() + "!") + ConsoleColors.RESET);
        return user;
    }

    // ================= LIBRARIAN LOGIN =================
    public Librarian libLogin(BookService bookService, CDService cdService) {
        printCenteredHeader("LIBRARIAN LOGIN");

        String user = promptCentered("Username: ");
        String pass = promptCentered("Password: ");

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

    // ==================== HELPERS ====================

    private static void printCenteredHeader(String title) {
        System.out.println("=".repeat(TOTAL_WIDTH));
        int padding = (TOTAL_WIDTH - title.length()) / 2;
        System.out.println(" ".repeat(Math.max(padding, 0)) + ConsoleColors.YELLOW + title + ConsoleColors.RESET);
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
