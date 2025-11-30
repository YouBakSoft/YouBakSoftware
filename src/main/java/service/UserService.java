package service;

import domain.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final String FILE_PATH = "data/users.txt";

    public UserService() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try { file.createNewFile(); } 
            catch (IOException e) { throw new RuntimeException(e); }
        }
    }
    


    public void addUser(User user) {

        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                return;
            }
        }

        users.add(user);
        saveUsers(users);
    }


    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length != 3) continue;

                User u = new User(parts[0], parts[1]);
                double fine = Double.parseDouble(parts[2]);
                u.setFineBalance(fine);

                users.add(u);
            }
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
        return users;
    }
    
    public void addFine(User user, double amount) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        user.addFine(amount);                  // update in memory
        saveUsers(getAllUsers());              // persist changes to file
    }
    
    public void payFine(User user, double amount, BookService bookService, CDService cdService) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (amount <= 0) throw new IllegalArgumentException("Invalid amount");

        List<User> users = getAllUsers();

        for (User u : users) {
            if (u.equals(user)) {
                u.payFine(amount);  // deduct amount

                // Automatically return all overdue media if balance is zero
                if (u.getFineBalance() == 0) {
                    if (bookService != null) bookService.returnAllMediaForUser(u);
                    if (cdService != null) cdService.returnAllMediaForUser(u);
                }
                break;
            }
        }

        saveUsers(users);
    }

    

    
    public void applyFine(User borrower, double fine) {
        if (borrower == null || fine <= 0) return;

        // Load all users
        List<User> users = getAllUsers();

        // Find the user and update fine
        for (User u : users) {
            if (u.equals(borrower)) {
                u.addFine(fine);
                break;
            }
        }
        saveUsers(users);
    }
    
    public boolean unregisterUser(User user) {
        if (user == null) return false;

        List<User> users = getAllUsers();
        boolean removed = users.removeIf(u -> u.equals(user));

        if (removed) {
            saveUsers(users);
        }
        return removed;
    }





    public void saveUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User u : users) {
                bw.write(u.getName() + ";" + u.getId() + ";" + u.getFineBalance());
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
