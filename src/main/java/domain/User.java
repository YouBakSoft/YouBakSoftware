package domain;

import java.util.Objects;

/**
 * Represents a user of the library system.
 * Users can borrow media, have fines, and are identified by a unique ID.
 */
public class User {

    /** The name of the user */
    private String name;

    /** The unique ID of the user */
    private String id;

    /** The email address of the user */
    private String email;

    /** The current fine balance of the user */
    private double fineBalance;

    /**
     * Constructs a new User with the specified name, ID, and email.
     * The initial fine balance is 0.
     *
     * @param name the user's name
     * @param id the user's unique ID
     * @param email the user's email address
     * @throws IllegalArgumentException if name, ID, or email is null
     */
    public User(String name, String id, String email) {
        if (name == null || id == null || email == null) {
            throw new IllegalArgumentException("Name and ID cannot be null");
        }
        this.name = name;
        this.id = id;
        this.email = email;
        this.fineBalance = 0;
    }

    /**
     * Returns the name of the user.
     *
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the unique ID of the user.
     *
     * @return the user's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the current fine balance of the user.
     *
     * @return the fine balance
     */
    public double getFineBalance() {
        return fineBalance;
    }

    /**
     * Sets the fine balance of the user.
     *
     * @param fineBalance the new fine balance
     */
    public void setFineBalance(double fineBalance) {
        this.fineBalance = fineBalance;
    }

    /**
     * Adds a fine to the user's balance.
     *
     * @param amount the fine amount to add
     * @throws IllegalArgumentException if the amount is not positive
     */
    public void addFine(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        fineBalance += amount;
    }

    /**
     * Pays a portion or all of the user's fine balance.
     *
     * @param amount the amount to pay
     * @throws IllegalArgumentException if the amount is not positive
     */
    public void payFine(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount >= fineBalance) {
            fineBalance = 0;
        } else {
            fineBalance -= amount;
        }
    }

    /**
     * Checks if the user can borrow media (i.e., has no unpaid fines).
     *
     * @return true if the fine balance is 0, false otherwise
     */
    public boolean canBorrow() {
        return fineBalance == 0;
    }

    /**
     * Returns the email of the user.
     *
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Checks if this user is equal to another object.
     * Two users are equal if their IDs are equal.
     *
     * @param o the object to compare
     * @return true if the other object is a User with the same ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User u)) return false;
        return Objects.equals(id, u.id);
    }

    /**
     * Returns the hash code of the user, based on the ID.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
