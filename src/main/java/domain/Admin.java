package domain;

import java.io.*;

import service.BookService;
import service.UserService;

/**
 * Represents an Admin user.
 * Admins can manage users and perform other administrative tasks.
 * Extends {@link Staff}.
 *
 * <p>Example usage:
 * <pre><code>
 * Admin admin = new Admin("admin1", "password123");
 * admin.login("password123");
 * User user = userService.getUser("user1");
 * boolean removed = admin.unregisterUser(user, userService, bookService);
 * admin.logout();
 * </code></pre>
 *
 * @since 1.0
 * @see Staff
 * @see UserService
 * @see BookService
 */
public class Admin extends Staff {

    /**
     * Creates a new Admin with a username and password.
     *
     * @param userName the admin's username
     * @param password the admin's password
     * @since 1.0
     */
    public Admin(String userName, String password) {
        super(userName, password); 
    }
    
    

    /**
     * Checks if the given username and password match an admin in "data/admins.txt".
     * Throws an exception if credentials are invalid or the file is missing.
     *
     * @param username the admin username
     * @param password the admin password
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the file is missing or credentials are wrong
     * @since 1.0
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
     * Logs in this admin using their password.
     *
     * @param password the admin password
     * @throws IOException if reading the file fails
     * @throws IllegalArgumentException if credentials are invalid
     * @since 1.0
     */
    public void login(String password) throws IOException {
        Admin.loginThrow(this.getUserName(), password);
        this.setLoggedIn(true);
    }

    /**
     * Logs out the admin.
     *
     * @throws IllegalStateException if the admin is not logged in
     * @since 1.0
     */
    public void logout() {
        if (!this.isLoggedIn()) throw new IllegalStateException("Admin is not logged in!");
        this.setLoggedIn(false);
    }
    
    /**
     * Unregisters a user from the system.
     * Admin must be logged in, the user must have no unpaid fines,
     * and must have returned all borrowed books.
     *
     * @param user the user to remove
     * @param userService service to manage users
     * @param bookService service to check active loans
     * @return true if the user was successfully unregistered
     * @throws IllegalStateException if admin is not logged in, user has fines, or user has borrowed books
     * @since 1.0
     * @see UserService#unregisterUser(User)
     * @see BookService#hasActiveLoans(User)
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
