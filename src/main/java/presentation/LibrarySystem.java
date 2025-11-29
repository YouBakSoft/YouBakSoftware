package presentation;

import java.util.Scanner;

import domain.Admin;
import domain.User;

public class LibrarySystem {

	public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LogIn loginHandler = new LogIn();

        while (true) {
            ConsoleUtils.printHeader("EPIC LIBRARY SYSTEM");
            String[] options = {"Admin Login", "User Login", "Exit"};
            ConsoleUtils.printMenu(options);
            System.out.print(ConsoleColors.YELLOW + "Choose: " + ConsoleColors.RESET);
            String choice = sc.nextLine();

            switch (choice) {
                case "0" -> {
                    Admin admin = loginHandler.adminLogin();
                    if (admin != null) new Dashboard().showMenu();
                }
                case "1" -> {
                    User user = loginHandler.userLogin();
                    new UserInterface(user).showMenu();
                }
                case "2" -> {
                    System.out.println(ConsoleColors.GREEN + "Goodbye!" + ConsoleColors.RESET);
                    System.exit(0);
                }
                default -> System.out.println(ConsoleColors.RED + "Invalid choice!" + ConsoleColors.RESET);
            }
        }
    }

}
