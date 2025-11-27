package domain;

import java.io.*;

public class Admin {
	
    private String userName;
    private String password;
    private String mes;
    private boolean loggedIn = false;

    public Admin(String userName, String password) {
        this.setUserName(userName);
        this.setPassword(password);
        
        new File("data").mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/admins.txt", true))) {
            bw.write(userName + "," + password);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
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
        Admin.loginThrow(this.userName, password); 
        this.loggedIn = true;
    }
	
    public void logout() {
        if (!this.loggedIn) {
            throw new IllegalStateException("Admin is not logged in!");
        }
        this.loggedIn = false;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
