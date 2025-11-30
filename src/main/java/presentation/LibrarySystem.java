package presentation;

import domain.Admin;
import domain.Librarian;
import domain.User;
import domain.Book;
import domain.CD;
import service.*;

import java.util.List;
import java.util.Scanner;

public class LibrarySystem {

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
            ConsoleUtils.printHeader("EPIC LIBRARY SYSTEM");
            String[] options = {"Admin Login", "User Login", "Librarian Login", "Exit"};
            ConsoleUtils.printMenu(options);
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
                case "1" -> { // User
                    User user = loginHandler.userLogin();
                    if (user != null) {
                        UserInterface ui = new UserInterface(user, bookService, cdService, userService);
                        ui.showMenu();
                    }
                }
                case "2" -> { // Librarian
                    Librarian lib = loginHandler.libLogin(bookService,cdService);
                    if (lib != null) {
                        LibrarianInterface li = new LibrarianInterface(lib, userService);
                        li.showMenu();
                    }
                }
                case "3" -> { // Exit
                    System.out.println(ConsoleColors.GREEN + "Goodbye!" + ConsoleColors.RESET);
                    System.exit(0);
                }
                default -> System.out.println(ConsoleColors.RED + "Invalid choice!" + ConsoleColors.RESET);
            }
        }
    }
}
