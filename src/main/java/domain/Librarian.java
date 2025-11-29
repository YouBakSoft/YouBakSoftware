package domain;

import service.BookService;
import java.time.LocalDate;

public class Librarian extends Staff {

    private BookService bookService;

    public Librarian(String userName, String password, BookService bookService) {
        super(userName, password);
        this.bookService = bookService;
    }

    public void checkOverdueAndIssueFines() {
        bookService.getOverdueBooks().forEach(book -> {
            if (!book.isAvailable() && book.getDueDate() != null && book.getBorrowedBy() != null) {
                long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(book.getDueDate(), LocalDate.now());
                if (overdueDays > 28) {
                    int fine = bookService.calculateFineForBook(book);
                    book.getBorrowedBy().addFine(fine);
                    System.out.println("Fine issued to " + book.getBorrowedBy().getName() + ": " + fine + " NIS");
                }
            }
        });
    }
}
