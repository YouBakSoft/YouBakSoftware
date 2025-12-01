package domain;

import java.io.*;

import service.BookService;
import service.UserService;

/**
 * Represents an Admin user who has privileges to manage other users and perform administrative tasks.
 * Admin extends {@link Staff} and provides methods for logging in, logging out, and unregistering users.
 */
public class Admin extends Staff {

    /**
     * Constructs a new Admin with the specified username and password.
     *
     * @param userName the username of the admin
     * @param password the password of the admin
     */
    public Admin(String userName, String password) {
        super(userName, password); 
    }

    /**
     * Validates the provided admin credentials against the "data/admins.txt" file.
     * Throws an exception if the credentials are invalid or the file is missing.
     *
     * @param username the admin username
     * @param password the admin password
     * @throws IOException if there is an error reading the admin file
     * @throws IllegalArgumentException if the file is missing or credentials are invalid
     */
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

    /**
     * Logs in the admin using the provided password.
     *
     * @param password the admin password
     * @throws IOException if there is an error reading the admin file
     * @throws IllegalArgumentException if credentials are invalid
     */
    public void login(String password) throws IOException {
        Admin.loginThrow(this.getUserName(), password);
        this.setLoggedIn(true);
    }

    /**
     * Logs out the admin.
     *
     * @throws IllegalStateException if the admin is not currently logged in
     */
    public void logout() {
        if (!this.isLoggedIn()) throw new IllegalStateException("Admin is not logged in!");
        this.setLoggedIn(false);
    }
    
    /**
     * Unregisters a user from the system.
     * This operation can only be performed if the admin is logged in, the user has no outstanding fines,
     * and the user has no active borrowed books.
     *
     * @param user the user to be unregistered
     * @param userService the user service to perform the unregistration
     * @param bookService the book service to check for active loans
     * @return true if the user was successfully unregistered, false otherwise
     * @throws IllegalStateException if the admin is not logged in, the user has unpaid fines,
     *                               or the user still has borrowed books
     */
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
