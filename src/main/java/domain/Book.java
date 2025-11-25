package domain;

public class Book {
	
    private String title;
    private String author;
    private String isbn;
    private boolean isBorrowed;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isBorrowed = false;
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
    public boolean isBorrowed() {
    	return isBorrowed; 
    	}
    public void setBorrowed(boolean borrowed) {
    	isBorrowed = borrowed;
    	}

}
