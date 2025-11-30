package domain;

import java.io.*;

import service.BookService;
import service.UserService;

public class Admin extends Staff {

    public Admin(String userName, String password) {
        super(userName, password); 
    }

    public static void loginThrow(String username, String password) throws IOException {
        File file = new File("data/admins.txt");
        if (!file.exists()) {
            throw new IllegalArgumentException("Admin file not found!");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(password)) return;
            }
        }

        throw new IllegalArgumentException("Invalid credentials!");
    }

    public void login(String password) throws IOException {
        Admin.loginThrow(this.getUserName(), password);
        this.setLoggedIn(true);
    }

    public void logout() {
        if (!this.isLoggedIn()) throw new IllegalStateException("Admin is not logged in!");
        this.setLoggedIn(false);
    }
    
    public boolean unregisterUser(User user, UserService userService, BookService bookService) {

        if (!this.isLoggedIn())
            throw new IllegalStateException("Admin must be logged in");

        if (user.getFineBalance() > 0)
            throw new IllegalStateException("User has unpaid fines");

        if (bookService.hasActiveLoans(user))
            throw new IllegalStateException("User still has borrowed books");

        return userService.unregisterUser(user);
    }

}
