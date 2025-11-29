package domain;

public class Staff {
    
	private String userName;
    private String password;
    private boolean loggedIn = false;

    public Staff(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.loggedIn = false;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
