package service;

import domain.Book;
import domain.Media;
import domain.User;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BookService implements MediaService<Book> {

    private final String FILE_PATH = "data/books.txt";
    private FineStrategy fineStrategy;
    private List<Observer> observers = new ArrayList<>();
    private UserService userService;

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
        for (Observer o : observers) o.notify(user, message);
    }
    

    public int calculateFineForBook(Book book) {
        if (!book.isOverdue() || fineStrategy == null) return 0;
        long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(book.getDueDate(), LocalDate.now());
        return fineStrategy.calculateFine((int) overdueDays);
    }


    // ====================== FILE OPERATIONS ======================
    private List<Book> readBooksFromFile() {
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

                books.add(b);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading books file", e);
        }
        return books;
    }

    private void writeBooksToFile(List<Book> books) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Book b : books) {
                String userId = (b.getBorrowedBy() != null) ? b.getBorrowedBy().getId() : "null";
                bw.write(String.join(";",
                        b.getTitle(),
                        b.getAuthor(),
                        b.getIsbn(),
                        Boolean.toString(b.isAvailable()),
                        b.getDueDate() != null ? b.getDueDate().toString() : "null",
                        userId));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing books file", e);
        }
    }

    // ====================== MEDIA SERVICE IMPLEMENTATION ======================
    @Override
    public Book addMedia(Book book) {
        if (book.getTitle() == null || book.getAuthor() == null || book.getIsbn() == null) {
            throw new IllegalArgumentException("Title, author, and ISBN cannot be null");
        }
        List<Book> books = readBooksFromFile();
        for (Book b : books) {
            if (b.getIsbn().equals(book.getIsbn())) {
                throw new IllegalArgumentException("Book with same ISBN already exists");
            }
        }
        books.add(book);
        writeBooksToFile(books);
        return book;
    }

    @Override
    public Book borrowMedia(User user, String isbn) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");

        List<Media> allMedia = new ArrayList<>(readBooksFromFile());
        if (!canUserBorrow(user, allMedia)) {
            throw new IllegalStateException("Cannot borrow books: overdue media or unpaid fines");
        }

        List<Book> books = readBooksFromFile();
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                if (!b.isAvailable()) throw new IllegalStateException("Book already borrowed");
                b.borrow(user); // sets available=false and dueDate automatically
                writeBooksToFile(books);
                return b;
            }
        }
        throw new IllegalArgumentException("Book not found");
    }

    @Override
    public List<Book> getOverdueMedia() {
        return readBooksFromFile().stream()
                .filter(b -> !b.isAvailable())
                .filter(b -> b.getDueDate() != null)
                .filter(b -> b.getBorrowedBy() != null)
                .filter(b -> LocalDate.now().isAfter(b.getDueDate()))
                .collect(Collectors.toList());
    }

    @Override
    public int calculateFine(Book book) {
        if (book.getDueDate() == null || book.isAvailable()) return 0;
        long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(book.getDueDate(), LocalDate.now());
        if (overdueDays > 0 && fineStrategy != null) {
            return fineStrategy.calculateFine((int) overdueDays);
        }
        return 0;
    }


    @Override
    public void returnAllMediaForUser(User user) {
        List<Book> books = readBooksFromFile();
        for (Book b : books) {
            if (user.equals(b.getBorrowedBy())) {
                b.setAvailable(true);
                b.setBorrowedBy(null);
                b.setDueDate(null);
                b.setFineApplied(0);
            }
        }
        writeBooksToFile(books);
    }

    @Override
    public List<Book> search(String query) {
        if (query == null) return new ArrayList<>();
        String q = query.toLowerCase();
        return readBooksFromFile().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q)
                        || b.getAuthor().toLowerCase().contains(q)
                        || b.getIsbn().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    // ====================== ADDITIONAL UTILITIES ======================
    public boolean canUserBorrow(User user, List<Media> allMedia) {
        if (!user.canBorrow()) return false;
        for (Media m : allMedia) {
            if (!m.isAvailable() && user.equals(m.getBorrowedBy()) && m.isOverdue()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasActiveLoans(User user) {
        if (user == null) return false;
        List<Book> books = readBooksFromFile();
        return books.stream().anyMatch(b -> !b.isAvailable() && user.equals(b.getBorrowedBy()));
    }

    public void sendReminders(List<User> users) {
        List<Book> overdueBooks = getOverdueMedia();
        for (User user : users) {
            long count = overdueBooks.stream()
                    .filter(b -> user.equals(b.getBorrowedBy()))
                    .count();
            if (count > 0) {
                notifyObservers(user, "You have " + count + " overdue book(s).");
            }
        }
    }
}
