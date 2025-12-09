package service;

import domain.Book;
import domain.Media;
import domain.User;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class to manage {@link Book} objects.
 * Extends {@link MultiMediaService} and provides functionality for adding, borrowing,
 * searching, and persisting books to a file.
 *
 * <p>Example usage:
 * <pre><code>
 * BookService bookService = new BookService();
 * Book newBook = new Book("Java 101", "John Doe", "123456789");
 * bookService.addMedia(newBook);
 * List&lt;Book&gt; allBooks = bookService.getAllMedia();
 * Book borrowed = bookService.borrowMedia(user, "123456789");
 * List&lt;Book&gt; searchResults = bookService.search("Java");
 * </code></pre>
 *
 * @since 1.0
 */
public class BookService extends MultiMediaService<Book> {

    /** Path to the file where books are stored */
    private final String FILE_PATH = "data/books.txt";

    /**
     * Constructs a BookService and ensures the data file exists.
     * If the file or directories do not exist, they will be created.
     *
     * @since 1.0
     */
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

    /**
     * Returns a list of all books in the system.
     *
     * @return List of {@link Book}
     * @since 1.0
     */
    public List<Book> getAllMedia() {
        return readFromFile();
    }

    /**
     * Adds a new book to the system.
     * Validates non-null title, author, and ISBN.
     * Throws an exception if a book with the same ISBN already exists.
     *
     * @param book The {@link Book} to add
     * @return The added book
     * @throws IllegalArgumentException If validation fails or ISBN already exists
     * @since 1.0
     */
    @Override
    public Book addMedia(Book book) {
        if (book.getTitle() == null || book.getAuthor() == null || book.getIsbn() == null) {
            throw new IllegalArgumentException("Title, author, and ISBN cannot be null");
        }
        List<Book> books = readFromFile();
        for (Book b : books) {
            if (b.getIsbn().equals(book.getIsbn())) {
                throw new IllegalArgumentException("Book with same ISBN already exists");
            }
        }
        books.add(book);
        writeToFile(books);
        return book;
    }

    /**
     * Borrows a book for a given user by ISBN.
     * Checks user eligibility (overdue books or unpaid fines).
     *
     * @param user The {@link User} borrowing the book
     * @param isbn The ISBN of the book to borrow
     * @return The borrowed {@link Book}
     * @throws IllegalArgumentException If the book or user is invalid
     * @throws IllegalStateException    If the book is already borrowed or user cannot borrow
     * @since 1.0
     */
    @Override
    public Book borrowMedia(User user, String isbn) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");

        List<Media> allMedia = new ArrayList<>(readFromFile());
        if (!canUserBorrow(user, allMedia)) {
            throw new IllegalStateException("Cannot borrow books: overdue media or unpaid fines");
        }

        List<Book> books = readFromFile();
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                if (!b.isAvailable()) throw new IllegalStateException("Book already borrowed");
                b.borrow(user);
                writeToFile(books);
                return b;
            }
        }
        throw new IllegalArgumentException("Book not found");
    }

    /**
     * Searches books by title, author, or ISBN (case-insensitive).
     *
     * @param query The search string
     * @return List of {@link Book} that match the query
     * @since 1.0
     */
    @Override
    public List<Book> search(String query) {
        if (query == null) return new ArrayList<>();
        String q = query.toLowerCase();
        return readFromFile().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q)
                        || b.getAuthor().toLowerCase().contains(q)
                        || b.getIsbn().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    /**
     * Reads books from the storage file.
     *
     * @return List of {@link Book} read from the file
     * @throws RuntimeException If file cannot be read
     * @since 1.0
     */
    @Override
    protected List<Book> readFromFile() {
        List<Book> books = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 5) continue;

                Book b = new Book(parts[0].trim(), parts[1].trim(), parts[2].trim());
                b.setAvailable(Boolean.parseBoolean(parts[3].trim()));

                if (!"null".equals(parts[4].trim())) {
                    try {
                        b.setDueDate(LocalDate.parse(parts[4].trim()));
                    } catch (Exception e) {
                        System.out.println("Warning: invalid date for book " + b.getTitle());
                    }
                }
                if (parts.length >= 6 && userService != null) {
                    String userId = parts[5].trim();
                    User u = userService.getAllUsers().stream()
                            .filter(user -> user.getId().equals(userId))
                            .findFirst().orElse(null);
                    b.setBorrowedBy(u);
                }
                if (parts.length >= 7) {
                    b.setFineApplied(Integer.parseInt(parts[6].trim()));
                } else {
                    b.setFineApplied(0);
                }
                books.add(b);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading books file", e);
        }
        return books;
    }

    /**
     * Writes a list of books to the storage file.
     *
     * @param list List of {@link Book} to write
     * @throws RuntimeException If file cannot be written
     * @since 1.0
     */
    @Override
    public void writeToFile(List<Book> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Book b : list) {
                String userId = (b.getBorrowedBy() != null) ? b.getBorrowedBy().getId() : "null";
                bw.write(String.join(";",
                        b.getTitle(),
                        b.getAuthor(),
                        b.getIsbn(),
                        Boolean.toString(b.isAvailable()),
                        b.getDueDate() != null ? b.getDueDate().toString() : "null",
                        userId,
                        Integer.toString(b.getFineApplied())));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing books file", e);
        }
    }
}
