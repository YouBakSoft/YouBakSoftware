package domain;

/**
 * Represents a staff member in the library.
 * This is a base class for all staff types like Admins and Librarians.
 *
 * <p>Example usage:
 * <pre><code>
 * Staff staff = new Staff("johnDoe", "password123");
 * String username = staff.getUserName();
 * staff.setLoggedIn(true);
 * boolean loggedIn = staff.isLoggedIn();
 * </code></pre>
 *
 * @since 1.0
 * @see Admin
 * @see Librarian
 */
public class Staff {

    /** Staff member's username */
    private String userName;

    /** Staff member's password */
    private String password;

    /** True if the staff member is currently logged in */
    private boolean loggedIn = false;

    /**
     * Create a new Staff member with username and password.
     * Initially not logged in.
     *
     * @param userName the staff username
     * @param password the staff password
     * @since 1.0
     */
    public Staff(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.loggedIn = false;
    }

    /**
     * Get the username.
     *
     * @return staff username
     * @since 1.0
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the username.
     *
     * @param userName staff username
     * @since 1.0
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get the password.
     *
     * @return staff password
     * @since 1.0
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password.
     *
     * @param password staff password
     * @since 1.0
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Check if the staff member is logged in.
     *
     * @return true if logged in, false otherwise
     * @since 1.0
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Set login status of the staff member.
     *
     * @param loggedIn true to mark as logged in, false otherwise
     * @since 1.0
     */
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
