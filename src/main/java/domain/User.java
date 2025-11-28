package domain;

public class User {

	private String name;
    private String id;
    private double fineBalance;

    public User(String name, String id) {
        if (name == null || id == null) {
            throw new IllegalArgumentException("Name and ID cannot be null");
        }
        this.name = name;
        this.id = id;
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
}
