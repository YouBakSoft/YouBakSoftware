package service;

import domain.Book;
import domain.Media;
import domain.User;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BookService extends MultiMediaService<Book> {

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
    public List<Book> getAllMedia() {
        return readFromFile();
    }


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
