package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import domain.Book;
import domain.User;

public class BookService {
	
	 private final String FILE_PATH = "data/books.txt";

	    public BookService() {
	        File file = new File(FILE_PATH);
	        if (!file.exists()) {
	            file.getParentFile().mkdirs();  
	            try {
	                file.createNewFile();
	            } catch (IOException e) {
	                throw new RuntimeException("Cannot create books.txt", e);
	            }
	        }
	    }
	    
	    // read books from file
	    private List<Book> readBooksFromFile() {
	        List<Book> books = new ArrayList<>();
	        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] parts = line.split(";");
	                if (parts.length == 5) {
	                    Book b = new Book(parts[0], parts[1], parts[2]);
	                    b.setAvailable(Boolean.parseBoolean(parts[3]));

	                    if (!parts[4].equals("null")) {
	                        b.setDueDate(LocalDate.parse(parts[4]));
	                    }
	                    books.add(b);
	                }
	            }
	        } catch (IOException e) {
	            throw new RuntimeException("Error reading from file", e);
	        }
	        return books;
	    }

	    // write books to file 
	    private void writeBooksToFile(List<Book> books) {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
	            for (Book b : books) {
	            	bw.write(
	            		    b.getTitle() + ";" +
	            		    b.getAuthor() + ";" +
	            		    b.getIsbn() + ";" +
	            		    b.isAvailable() + ";" +
	            		    b.getDueDate()
	            		);
	                bw.newLine();
	            }

	        } catch (IOException e) {
	            throw new RuntimeException("Error writing to file", e);
	        }
	    }

	    // add book 
	    public Book addBook(String title, String author, String isbn) {

	        if (title == null || author == null || isbn == null) {
	            throw new IllegalArgumentException("title, author and isbn cannot be null");
	        }
	        List<Book> books = readBooksFromFile();
	        // check duplicate isbn
	        for (Book b : books) {
	            if (b.getIsbn().equals(isbn)) {
	                throw new IllegalArgumentException("Book with same ISBN already exists");
	            }
	        }
	        Book newBook = new Book(title, author, isbn);
	        books.add(newBook);
	        writeBooksToFile(books);
	        return newBook;
	    }

	    // search book 
	    public List<Book> search(String query) {
	        List<Book> result = new ArrayList<>();
	        if (query == null) return result;
	        String q = query.toLowerCase();
	        List<Book> books = readBooksFromFile();
	        for (Book b : books) {
	            if (b.getTitle().toLowerCase().contains(q) ||
	                b.getAuthor().toLowerCase().contains(q) ||
	                b.getIsbn().toLowerCase().contains(q)) {
	                result.add(b);
	            }
	        }
	        return result;
	    }
	    
	    // borrow book 
	    public Book borrowBook(User user, String isbn) {
	        if (user == null) throw new IllegalArgumentException("User cannot be null");
	        if (!user.canBorrow()) {
	            throw new IllegalStateException("Cannot borrow books until full fine is paid");
	        }
	        List<Book> books = readBooksFromFile();
	        for (Book b : books) {
	            if (b.getIsbn().equals(isbn)) {
	                if (!b.isAvailable()) {
	                    throw new IllegalArgumentException("Book already borrowed");
	                }
	                b.setAvailable(false);
	                b.setDueDate(LocalDate.now().plusDays(28));

	                writeBooksToFile(books);
	                return b;
	            }
	        }
	        throw new IllegalArgumentException("Book not found");
	    }

	    
	    // over due book
	    public List<Book> getOverdueBooks() {
	        List<Book> books = readBooksFromFile();
	        List<Book> overdue = new ArrayList<>();
	        LocalDate today = LocalDate.now();
	        for (Book b : books) {
	            if (!b.isAvailable() && b.getDueDate() != null && today.isAfter(b.getDueDate())) {
	                overdue.add(b);
	            }
	        }
	        return overdue;
	    }
}
