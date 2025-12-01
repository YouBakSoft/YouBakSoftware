package domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import service.FineStrategy;

public abstract class Media {

    protected String title;
    protected boolean available;
    protected LocalDate dueDate;
    protected User borrowedBy;
    protected int fineApplied;
    protected FineStrategy fineStrategy;

    public Media(String title) {
        if (title == null) {
            throw new IllegalArgumentException("Title must not be null");
        }
        this.title = title;
        this.available = true;
        this.borrowedBy = null;
        this.dueDate = null;
        this.fineApplied = 0;
    }

    public abstract void borrow(User user);


    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }
    
    public void setFineStrategy(FineStrategy strategy) {
        this.fineStrategy = strategy;
    }
    
    public int calculateFine() {
        if (dueDate == null || available || fineStrategy == null) return 0;

        long overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        if (overdueDays <= 0) return 0;

        return fineStrategy.calculateFine((int) overdueDays);
    }

    public String getTitle() {
    	return title; 
    	}
    public boolean isAvailable() {
    	return available;
    	}
    public void setAvailable(boolean available) {
    	this.available = available;
    	}
    public LocalDate getDueDate() {
    	return dueDate; 
    	}
    public void setDueDate(LocalDate dueDate) { 
    	this.dueDate = dueDate; 
    	}
    public User getBorrowedBy() {
    	return borrowedBy;
    	}
    public void setBorrowedBy(User borrowedBy) {
    	this.borrowedBy = borrowedBy; 
    	}
    public int getFineApplied() {
    	return fineApplied; 
    	}
    public void setFineApplied(int fineApplied) {
    	this.fineApplied = fineApplied;
    	}

}
