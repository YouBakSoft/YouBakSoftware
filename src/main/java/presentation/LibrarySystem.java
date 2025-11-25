package presentation;
import domain.Book;
import domain.Admin;
import service.AdminService;
import service.BookService;
import java.util.List;
import java.util.Scanner;

public class LibrarySystem {

	public static void main(String[] args) {
		
        Admin admin = new Admin("admin", "1234");
        AdminService adminService = new AdminService(admin);
        BookService bookService = new BookService();
        Scanner sc = new Scanner(System.in);

        System.out.println("Login:");
        System.out.print("Username: "); String user = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();

        if(adminService.login(user, pass)) {
            System.out.println("Login successful!");
            System.out.println("1. Add Book  2. Search Book  3. Logout");
            int choice = sc.nextInt(); sc.nextLine();
            if(choice == 1) {
                System.out.print("Title: "); String t = sc.nextLine();
                System.out.print("Author: "); String a = sc.nextLine();
                System.out.print("ISBN: "); String i = sc.nextLine();
                bookService.addBook(new Book(t, a, i));
                System.out.println("Book added!");
            } else if(choice == 2) {
                System.out.print("Search query: "); String q = sc.nextLine();
                List<Book> results = bookService.searchBook(q);
                results.forEach(b -> System.out.println(b.getTitle() + " - " + b.getAuthor() + " - " + b.getIsbn()));
            }
            adminService.logout();
            System.out.println("Logged out!");
        } else {
            System.out.println("Invalid credentials!");
        }

	}

}
