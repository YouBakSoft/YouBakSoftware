package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Book;
import domain.BookService;
import domain.User;

class bookTests {

	private BookService service;

	@BeforeEach
	void clearFile() {
	    try (FileWriter fw = new FileWriter("data/books.txt")) {
	        fw.write("");    
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
    @BeforeEach
    void setup() {
        service = new BookService();
    }

    // tests for add book 
    @Test
    void addBookSuccess() {
        Book b = service.addBook("Clean Code", "Robert Martin", "111");

        assertNotNull(b);
        assertEquals("Clean Code", b.getTitle());
        assertEquals("Robert Martin", b.getAuthor());
        assertEquals("111", b.getIsbn());
        assertTrue(b.isAvailable());
    }

    @Test
    void addBookDuplicateIsbn() {
        service.addBook("Book1", "A", "123");
        assertThrows(IllegalArgumentException.class, () -> {
            service.addBook("Book2", "B", "123");
        });
    }

    @Test
    void addBookNoTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.addBook(null, "Author", "999");
        });
    }

    @Test
    void addBookNoAuthor() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.addBook("Title", null, "999");
        });
    }

    @Test
    void addBookNoIsbn() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.addBook("Title", "Author", null);
        });
    }

    // tests for search book 
    @Test
    void searchByTitleSuccess() {
        service.addBook("Clean Code", "Robert Martin", "111");
        service.addBook("Effective Java", "Joshua Bloch", "222");

        List<Book> results = service.search("Clean");
        assertEquals(1, results.size());
        assertEquals("Clean Code", results.get(0).getTitle());
    }

    @Test
    void searchByAuthorSuccess() {
        service.addBook("Head First Java", "Kathy Sierra", "333");

        List<Book> results = service.search("Kathy");
        assertEquals(1, results.size());
        assertEquals("333", results.get(0).getIsbn());
    }

    @Test
    void searchByIsbnSuccess() {
        service.addBook("Some Book", "Author", "555");

        List<Book> results = service.search("555");
        assertEquals(1, results.size());
        assertEquals("Some Book", results.get(0).getTitle());
    }

    @Test
    void searchNoMatchByTitle() {
        service.addBook("Clean Code", "Robert Martin", "111");

        List<Book> results = service.search("Java");
        assertTrue(results.isEmpty());
    }

    @Test
    void searchNoMatchByAuthor() {
        service.addBook("Clean Code", "Robert Martin", "111");

        List<Book> results = service.search("Kathy");
        assertTrue(results.isEmpty());
    }

    @Test
    void searchNoMatchByIsbn() {
        service.addBook("Clean Code", "Robert Martin", "111");

        List<Book> results = service.search("999");
        assertTrue(results.isEmpty());
    }
    
    // tests for borrow book 
    @Test
    void borrowBookSuccess() {
    	User user = new User("Baker", "100");
        service.addBook("Clean Code", "Robert Martin", "111");

        Book b = service.borrowBook(user ,"111");

        assertFalse(b.isAvailable());
        assertEquals(LocalDate.now().plusDays(28), b.getDueDate());
    }

    @Test
    void borrowBookAlreadyBorrowed() {
    	User user = new User("Baker", "100");
        service.addBook("Clean Code", "Robert Martin", "111");
        service.borrowBook(user ,"111");

        assertThrows(IllegalArgumentException.class, () -> {
            service.borrowBook(user ,"111");
        });
    }

    @Test
    void borrowBookNotFound() {
    	User user = new User("Baker", "100");
        assertThrows(IllegalArgumentException.class, () -> {
            service.borrowBook(user,"999");
        });
    }
    
    //tests for over due book 
    @Test
    void overdueBookDetection() {
    	User user = new User("Baker", "100");
        Book b = service.addBook("Old Book", "Author A", "101");

         
        service.borrowBook(user ,"101");

         
        List<Book> allBooks = service.search("");  
        for (Book book : allBooks) {
            if (book.getIsbn().equals("101")) {
                book.setDueDate(LocalDate.now().minusDays(30));
            }
        }
    
        try {
            java.lang.reflect.Method writeMethod = BookService.class.getDeclaredMethod("writeBooksToFile", List.class);
            writeMethod.setAccessible(true);
            writeMethod.invoke(service, allBooks);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to write test book data");
        }

         
        List<Book> overdueBooks = service.getOverdueBooks();
        
        assertEquals(1, overdueBooks.size());
        assertEquals("101", overdueBooks.get(0).getIsbn());
    }

    // tests for pay fine 
    
    @Test
    void borrowBookWithFineShouldFail() {
        User user = new User("Baker", "100");
        service.addBook("Clean Code", "Robert Martin", "111");

        user.addFine(50);   
        assertFalse(user.canBorrow());

        assertThrows(IllegalStateException.class, () -> {
            service.borrowBook(user, "111");
        });
    }

    @Test
    void payFullFineCanBorrow() {
        User user = new User("Alice", "U01");
        service.addBook("Clean Code", "Robert Martin", "111");

        user.addFine(50);    
        user.payFine(50);    
        assertEquals(0, user.getFineBalance());
        assertTrue(user.canBorrow());

        Book b = service.borrowBook(user, "111");
        assertFalse(b.isAvailable());
        assertEquals(LocalDate.now().plusDays(28), b.getDueDate());
    }


    
    @Test
    void search_caseInsensitive() {
        service.addBook("CLEAN CODE", "ROBERT MARTIN", "111");

        assertEquals(1, service.search("clean").size());
        assertEquals(1, service.search("martin").size());
        assertEquals(1, service.search("111").size());
    }
}
