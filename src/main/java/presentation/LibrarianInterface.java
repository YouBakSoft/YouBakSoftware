package presentation;

import java.util.Scanner;
import domain.Librarian;
import service.UserService;

public class LibrarianInterface {

    private Scanner sc = new Scanner(System.in);
    private Librarian librarian;
    private UserService userService;

    public LibrarianInterface(Librarian librarian, UserService userService) {
        this.librarian = librarian;
        this.userService = userService;
    }

    public void showMenu() {
        while (true) {
            ConsoleUtils.printHeader("LIBRARIAN MENU");
            String[] options = {
                "Detect Overdue Media & Issue Fines",
                "Logout"
            };
            ConsoleUtils.printMenu(options);

            System.out.print(ConsoleColors.YELLOW + "Choose: " + ConsoleColors.RESET);
            String choice = sc.nextLine();

            switch (choice) {
                case "0" -> detectOverdueMedia();
                case "1" -> { return; }
                default -> System.out.println(ConsoleColors.RED + "Invalid choice!" + ConsoleColors.RESET);
            }
        }
    }

    private void detectOverdueMedia() {
        System.out.println(ConsoleColors.GREEN + "Checking overdue media..." + ConsoleColors.RESET);

        // This will handle both books and CDs
        librarian.checkOverdueAndIssueFines(userService);

        System.out.println(ConsoleColors.GREEN + "Overdue check complete." + ConsoleColors.RESET);
    }
}
