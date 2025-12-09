package tests;

import domain.Book;
import domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import service.ReportFine;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class reportFineTest {

    private final String folder = "receipts";

    @AfterEach
    void cleanup() {
        // Delete all files in the receipts folder after each test
        File dir = new File(folder);
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
        }
    }

    @Test
    void testGenerateFineReceiptWithBook() {
        // Updated constructor: id, name, email
        User user = new User("123", "Alice", "alice@example.com");
        Book book = new Book("Java Programming", "Author", "ISBN123");

        // Call method
        ReportFine.generateFineReceipt(user, 50.0, false, book);

        // Verify that a PDF file was created
        File dir = new File(folder);
        assertTrue(dir.exists() && dir.isDirectory(), "Receipts folder should exist");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".pdf"));
        assertNotNull(files);
        assertTrue(files.length > 0, "PDF receipt should be created");

        boolean foundIssued = false;
        for (File f : files) {
            if (f.getName().contains("Issued")) {
                foundIssued = true;
                break;
            }
        }
        assertTrue(foundIssued, "PDF filename should include 'Issued'");
    }

    @Test
    void testGenerateFineReceiptWithoutMedia() {
        // Updated constructor: id, name, email
        User user = new User("456", "Bob", "bob@example.com");

        ReportFine.generateFineReceipt(user, 20.0, true, null);

        File dir = new File(folder);
        assertTrue(dir.exists());
        File[] files = dir.listFiles((d, name) -> name.endsWith(".pdf"));
        assertNotNull(files);
        assertTrue(files.length > 0);

        boolean foundPaid = false;
        for (File f : files) {
            if (f.getName().contains("Paid")) {
                foundPaid = true;
                break;
            }
        }
        assertTrue(foundPaid, "PDF filename should include 'Paid'");
    }
}
