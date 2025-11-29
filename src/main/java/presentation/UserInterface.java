package presentation;

import java.util.List;
import java.util.Scanner;

import domain.Book;
import domain.User;
import service.BookService;
import service.BookFineStrategy;
import service.EmailNotifier;
import service.MockEmailService;

public class UserInterface {

    private BookService bookService;
    private Scanner sc = new Scanner(System.in);
    private User user;

    public UserInterface(User user) {
        this.user = user;
        bookService = new BookService();
        bookService.setFineStrategy(new BookFineStrategy());
        bookService.addObserver(new EmailNotifier(new MockEmailService()));
    }

    public void showMenu() {
        while (true) {
            ConsoleUtils.printHeader("USER MENU");
            String[] options = {"Borrow Book", "My Overdue Books", "Pay Fine", "Logout"};
            ConsoleUtils.printMenu(options);

            System.out.print(ConsoleColors.YELLOW + "Choose: " + ConsoleColors.RESET);
            String choice = sc.nextLine();

            switch (choice) {
                case "0" -> borrowBook();
                case "1" -> showOverdue();
                case "2" -> payFine();
                case "3" -> { return; }
                default -> System.out.println(ConsoleColors.RED + "Invalid choice!" + ConsoleColors.RESET);
            }
        }
    }

    private void borrowBook() {
        System.out.print("Book ISBN: "); String isbn = sc.nextLine();
        try {
            Book b = bookService.borrowBook(user, isbn);
            System.out.println(ConsoleColors.GREEN + "You borrowed: " + b.getTitle() + " | Due: " + b.getDueDate() + ConsoleColors.RESET);
        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + ex.getMessage() + ConsoleColors.RESET);
        }
    }

    private void showOverdue() {
        List<Book> overdue = bookService.getOverdueBooks();
        boolean hasOverdue = false;
        for (Book b : overdue) {
            if (!b.isAvailable()) {
                int fine = bookService.calculateFineForBook(b);
                System.out.println(ConsoleColors.RED + b.getTitle() + " | Due: " + b.getDueDate() + " | Fine: " + fine + " NIS" + ConsoleColors.RESET);
                hasOverdue = true;
            }
        }
        if (!hasOverdue) System.out.println(ConsoleColors.GREEN + "No overdue books!" + ConsoleColors.RESET);
    }

    private void payFine() {
        System.out.print("Amount: ");
        try {
            double amount = Double.parseDouble(sc.nextLine());
            user.payFine(amount);
            System.out.println(ConsoleColors.GREEN + "Paid " + amount + " NIS. Remaining: " + user.getFineBalance() + ConsoleColors.RESET);
        } catch (Exception ex) {
            System.out.println(ConsoleColors.RED + "Invalid amount!" + ConsoleColors.RESET);
        }
    }
}
