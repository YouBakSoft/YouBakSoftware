package domain;

/**
 * Represents a generic staff member in the library system.
 * This class provides basic properties and behaviors for all staff types,
 * such as Admins and Librarians.
 */
public class Staff {

    /** The username of the staff member */
    private String userName;

    /** The password of the staff member */
    private String password;

    /** Indicates whether the staff member is currently logged in */
    private boolean loggedIn = false;

    /**
     * Constructs a new Staff member with the specified username and password.
     * Initially, the staff member is not logged in.
     *
     * @param userName the username of the staff member
     * @param password the password of the staff member
     */
    public Staff(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.loggedIn = false;
    }

    /**
     * Returns the username of the staff member.
     *
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username of the staff member.
     *
     * @param userName the new username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns the password of the staff member.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the staff member.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks whether the staff member is currently logged in.
     *
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Sets the login status of the staff member.
     *
     * @param loggedIn true if the staff member is logged in, false otherwise
     */
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
