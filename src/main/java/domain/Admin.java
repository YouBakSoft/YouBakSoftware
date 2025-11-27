package domain;

import java.io.*;
import java.util.*;

public class Admin {
	
    private String userName;
    private String password;

    public Admin(String userName, String password) {
        this.setUserName(userName);
        this.setPassword(password);
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
	
    public static void login(String username, String password) throws IOException {
        loginThrow(username, password);
        Session.login(username);
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
}
