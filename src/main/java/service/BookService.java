package service;

import domain.Book;
import domain.User;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {

    private final String FILE_PATH = "data/books.txt";
    private FineStrategy fineStrategy;
    private List<Observer> observers = new ArrayList<>();
    private UserService userService; // <-- added UserService reference

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

    // Inject UserService so we can restore borrowedBy when reading books
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setFineStrategy(FineStrategy strategy) {
        this.fineStrategy = strategy;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    private void notifyObservers(User user, String message) {
        for (Observer o : observers) {
            o.notify(user, message);
        }
    }

    private List<Book> readBooksFromFile() {
        List<Book> books = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 5) continue;

                Book b = new Book(parts[0].trim(), parts[1].trim(), parts[2].trim());
                b.setAvailable(Boolean.parseBoolean(parts[3].trim()));

                // Parse due date
                if (!parts[4].trim().equals("null") && !parts[4].trim().isEmpty()) {
                    try {
                        b.setDueDate(LocalDate.parse(parts[4].trim()));
                    } catch (Exception e) {
                        System.out.println("Warning: invalid date for book " + b.getTitle());
                    }
                }

                // Restore borrowedBy
                if (parts.length >= 6 && !parts[5].trim().equals("null") && userService != null) {
                    String userId = parts[5].trim();
                    User u = userService.getAllUsers().stream()
                                        .filter(user -> user.getId().equals(userId))
                                        .findFirst()
                                        .orElse(null);
                    b.setBorrowedBy(u);
                }

                books.add(b);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file", e);
        }
        return books;
    }


    private void writeBooksToFile(List<Book> books) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Book b : books) {
                String borrowedById = (b.getBorrowedBy() != null) ? b.getBorrowedBy().getId() : "null";
                bw.write(
                    b.getTitle() + ";" +
                    b.getAuthor() + ";" +
                    b.getIsbn() + ";" +
                    b.isAvailable() + ";" +
                    (b.getDueDate() != null ? b.getDueDate() : "null") + ";" +
                    borrowedById
                );
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file", e);
        }
    }


    // ====================== BOOK OPERATIONS ======================
    public Book addBook(String title, String author, String isbn) {
        if (title == null || author == null || isbn == null) {
            throw new IllegalArgumentException("title, author and isbn cannot be null");
        }

        List<Book> books = readBooksFromFile();

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

    public Book borrowBook(User user, String isbn) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (!user.canBorrow()) throw new IllegalStateException("Cannot borrow books until full fine is paid");

        List<Book> books = readBooksFromFile();
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                if (!b.isAvailable()) throw new IllegalArgumentException("Book already borrowed");

                b.setAvailable(false);
                // Set due date to 14 days from today (or change for testing overdue)
                b.setDueDate(LocalDate.now().minusDays(2)); // <-- for testing overdue
                b.setBorrowedBy(user);

                writeBooksToFile(books);
                return b;
            }
        }
        throw new IllegalArgumentException("Book not found");
    }

    public List<Book> getOverdueBooks() {

        List<Book> books = readBooksFromFile();

        List<Book> overdue = books.stream()
            .filter(b -> !b.isAvailable()
                      && b.getDueDate() != null
                      && b.getBorrowedBy() != null
                      && LocalDate.now().isAfter(b.getDueDate()))
            .collect(Collectors.toList());

        List<User> users = userService.getAllUsers();

        for (Book b : overdue) {
            User u = b.getBorrowedBy();
            int newFine = calculateFineForBook(b);

            if (newFine > 0) {
                u.addFine(newFine);
                b.setFineApplied(b.getFineApplied() + newFine);
            }
        }

        userService.saveUsers(users);
        writeBooksToFile(books);

        return overdue;
    }






    public int calculateFineForBook(Book book) {
        if (book.getDueDate() == null || book.isAvailable()) return 0;
        int overdueDays = (int) java.time.temporal.ChronoUnit.DAYS.between(book.getDueDate(), LocalDate.now());
        if (overdueDays > 0 && fineStrategy != null) {
            return fineStrategy.calculateFine(overdueDays);
        }
        return 0;
    }

    public void sendReminders(List<User> users) {
        List<Book> overdueBooks = getOverdueBooks();
        for (User user : users) {
            long count = overdueBooks.stream()
                                     .filter(b -> !b.isAvailable() && b.getBorrowedBy().equals(user))
                                     .count();
            if (count > 0) {
                String message = "You have " + count + " overdue book(s).";
                notifyObservers(user, message);
            }
        }
    }
}
