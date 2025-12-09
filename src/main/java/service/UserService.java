package service;

import domain.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing users in the library system.
 * Supports operations such as adding, removing, retrieving users,
 * and managing their fines.
 * 
 * <p>Example usage:
 * <pre><code>
 * UserService userService = new UserService();
 * User user = new User("Alice", "U001", "alice@example.com");
 * userService.addUser(user);
 * userService.addFine(user, 50);
 * userService.payFine(user, 20, bookService, cdService);
 * </code></pre>
 * </p>
 */
public class UserService {

    private final String FILE_PATH = "data/users.txt";

    /**
     * Constructs a UserService and ensures the user data file exists.
     * If the file or directories do not exist, they will be created.
     */
    public UserService() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Retrieves all users from the storage file.
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length != 4) continue;

                String name = parts[0];
                String id = parts[1];
                String email = parts[2];
                double fine = Double.parseDouble(parts[3]);

                User u = new User(name, id, email);
                u.setFineBalance(fine);
                users.add(u);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Saves a list of users to the storage file.
     *
     * @param users the list of users to save
     */
    public void saveUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User u : users) {
                bw.write(u.getName() + ";" + u.getId() + ";" + u.getEmail() + ";" + u.getFineBalance());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new user to the system if a user with the same ID does not exist.
     *
     * @param user the user to add
     */
    public void addUser(User user) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getId().equals(user.getId())) return;}
        users.add(user);
        saveUsers(users);
    }

    /**
     * Adds a fine to a user's balance.
     *
     * @param user   the user to charge
     * @param amount the fine amount to add
     * @throws IllegalArgumentException if user is null
     */
    public void addFine(User user, double amount) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        user.addFine(amount);
        saveUsers(getAllUsers());
    }

    /**
     * Pays a fine for a user and optionally returns all media if fines are cleared.
     *
     * @param user        the user paying the fine
     * @param amount      the amount to pay
     * @param bookService the BookService to return books if fines cleared
     * @param cdService   the CDService to return CDs if fines cleared
     * @throws IllegalArgumentException if user is null or amount is invalid
     */
    public void payFine(User user, double amount, BookService bookService, CDService cdService) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (amount <= 0) throw new IllegalArgumentException("Invalid amount");
        if (amount > user.getFineBalance())
            throw new IllegalArgumentException("Amount cannot exceed current fine balance");

        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.equals(user)) {
                u.payFine(amount);
                ReportFine.generateFineReceipt(u, amount, true, null);
                if (u.getFineBalance() == 0) {
                    if (bookService != null) bookService.returnAllMediaForUser(u);
                    if (cdService != null) cdService.returnAllMediaForUser(u);
                }
                break;
            }
        }
        saveUsers(users);
    }

    /**
     * Applies a fine to a user without validation.
     *
     * @param borrower the user to fine
     * @param fine     the fine amount to apply
     */
    public void applyFine(User borrower, double fine) {
        if (borrower == null || fine <= 0) return;
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.equals(borrower)) {
                u.addFine(fine);
                break;
            }
        }
        saveUsers(users);
    }

    /**
     * Removes a user from the system.
     *
     * @param user the user to remove
     * @return true if the user was removed, false otherwise
     */
    public boolean unregisterUser(User user) {
        if (user == null) return false;
        List<User> users = getAllUsers();
        boolean removed = users.removeIf(u -> u.equals(user));
        if (removed) saveUsers(users);
        return removed;
    }
}
