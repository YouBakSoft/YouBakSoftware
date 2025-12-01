package domain;

import java.util.Objects;

public class User {

    private String name;
    private String id;
    private String email;
    private double fineBalance;

    public User(String name, String id ,String email) {
        if (name == null || id == null || email == null) {
            throw new IllegalArgumentException("Name and ID cannot be null");
        }
        this.name = name;
        this.id = id;
        this.email = email;
        this.fineBalance = 0;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public double getFineBalance() {
        return fineBalance;
    }

    public void setFineBalance(double fineBalance) {
        this.fineBalance = fineBalance;
    }

    public void addFine(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        fineBalance += amount;
    }

    public void payFine(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount >= fineBalance) {
            fineBalance = 0;
        } else {
            fineBalance -= amount;
        }
    }

    public boolean canBorrow() {
        return fineBalance == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User u)) return false;
        return Objects.equals(id, u.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
