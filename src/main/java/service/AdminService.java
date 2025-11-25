package service;
import domain.Admin;

public class AdminService {
	
    private Admin admin;
    private boolean loggedIn = false;

    public AdminService(Admin admin) {
        this.admin = admin;
    }

    public boolean login(String username, String password) {
        if(admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
            loggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() {
        loggedIn = false;
    }

    public boolean isLoggedIn() { 
    	return loggedIn;
    	}

}
