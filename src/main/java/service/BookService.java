package service;
import domain.Book;
import java.util.ArrayList;
import java.util.List;


public class BookService {

    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
    	books.add(book); 
    	}

    public List<Book> searchBook(String query) {
        List<Book> results = new ArrayList<>();
        for(Book b : books) {
            if(b.getTitle().contains(query) || b.getAuthor().contains(query) || b.getIsbn().contains(query))
                results.add(b);
        }
        return results;
    }
}
