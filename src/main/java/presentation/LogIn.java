package presentation;

import java.io.IOException;
import java.util.Scanner;

import domain.Admin;
import domain.User;

public class LogIn {

    private Scanner sc = new Scanner(System.in);

    public Admin adminLogin() {
        ConsoleUtils.printHeader("ADMIN LOGIN");
        System.out.print("Username: ");
        String user = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        try {
            Admin.loginThrow(user, pass);
            System.out.println(ConsoleColors.GREEN + "Admin login successful!" + ConsoleColors.RESET);
            return new Admin(user, pass);
        } catch (IOException ex) {
            System.out.println(ConsoleColors.RED + "Admin file error!" + ConsoleColors.RESET);
        } catch (IllegalArgumentException ex) {
            System.out.println(ConsoleColors.RED + ex.getMessage() + ConsoleColors.RESET);
        }
        return null;
    }

    public User userLogin() {
        ConsoleUtils.printHeader("USER LOGIN");
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("ID: ");
        String id = sc.nextLine();

        User user = new User(name, id);
        System.out.println(ConsoleColors.GREEN + "Welcome " + name + "!" + ConsoleColors.RESET);
        return user;
    }
}
