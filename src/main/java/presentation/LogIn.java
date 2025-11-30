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

    private Scanner sc = new Scanner(System.in);
    private UserService userService;  
    public LogIn(UserService userService) {
        this.userService = userService;
    }

    // ================= ADMIN LOGIN =================
    public Admin adminLogin() {
        ConsoleUtils.printHeader("ADMIN LOGIN");
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        try {
            Admin admin = new Admin(username, password);
            admin.login(password); 
            System.out.println(ConsoleColors.GREEN + "Admin login successful!" + ConsoleColors.RESET);
            return admin;
        } catch (IOException ex) {
            System.out.println(ConsoleColors.RED + "Admin file error!" + ConsoleColors.RESET);
        } catch (IllegalArgumentException ex) {
            System.out.println(ConsoleColors.RED + ex.getMessage() + ConsoleColors.RESET);
        }
        return null;
    }


    // ================= USER LOGIN =================
    public User userLogin() {
        ConsoleUtils.printHeader("USER LOGIN");
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("ID: ");
        String id = sc.nextLine();

        User user = userService.getAllUsers().stream()
                .filter(u -> u.getName().equals(name) && u.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (user == null) {
            System.out.println(ConsoleColors.RED + "User not found!" + ConsoleColors.RESET);
            return null;   
        }

        System.out.println(ConsoleColors.GREEN + "Welcome " + name + "!" + ConsoleColors.RESET);
        return user;  
    }

    // ================= LIBRARIAN LOGIN =================
    public Librarian libLogin(BookService bookService, CDService cdService) {
        ConsoleUtils.printHeader("LIBRARIAN LOGIN");
        System.out.print("Username: ");
        String user = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        try {
            Librarian.loginThrow(user, pass);
            System.out.println(ConsoleColors.GREEN + "Librarian login successful!" + ConsoleColors.RESET);
            return new Librarian(user, pass, bookService, cdService);  
        } catch (IOException ex) {
            System.out.println(ConsoleColors.RED + "Librarian file error!" + ConsoleColors.RESET);
        } catch (IllegalArgumentException ex) {
            System.out.println(ConsoleColors.RED + ex.getMessage() + ConsoleColors.RESET);
        }
        return null;
    }

}
