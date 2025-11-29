package domain;

import java.time.LocalDate;

public class Book {
	
	private String title;
    private String author;
    private String isbn;
    private boolean available;
    private LocalDate dueDate;
    private User borrowedBy;
    private int fineApplied = 0;


    public Book(String title, String author, String isbn) {
        if (title == null || author == null || isbn == null) {
            throw new IllegalArgumentException("title, author and isbn must not be null");
        }

        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = true;  
        this.dueDate = null;
        this.borrowedBy=null;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getTitle() { 
    	return title; 
    }
    public String getAuthor() { 
    	return author; 
    }
    public String getIsbn() { 
    	return isbn; 
    }
    public boolean isAvailable() { 
    	return available; 
    }

    public void setAvailable(boolean available) {
        this.available = available;
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

