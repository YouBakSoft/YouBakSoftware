package tests;

import domain.CD;
import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.CDService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;

class cdServiceTest {

    private CDService cdService;
    private User user;

    @BeforeEach
    void setup() {
        cdService = new CDService();
        user = new User("Alice", "U1", "alice@example.com");

        // Clear the file before each test
        cdService.writeToFile(List.of());
    }

    @Test
    void borrowCDSuccessfully() {
        CD cd = new CD("Album 1", "Artist 1", "CD123");
        cdService.addMedia(cd);

        CD borrowed = cdService.borrowMedia(user, "CD123");
        assertFalse(borrowed.isAvailable());
        assertEquals(user, borrowed.getBorrowedBy());
        assertNotNull(borrowed.getDueDate());
    }

    @Test
    void cannotBorrowAlreadyBorrowedCD() {
        CD cd = new CD("Album 1", "Artist 1", "CD123");
        cdService.addMedia(cd);
        cdService.borrowMedia(user, "CD123");

        User user2 = new User("Bob", "U2", "bob@example.com");
        Exception ex = assertThrows(IllegalStateException.class,
                () -> cdService.borrowMedia(user2, "CD123"));
        assertEquals("CD already borrowed", ex.getMessage());
    }

    @Test
    void cannotBorrowIfUserHasFines() {
        CD cd = new CD("Album 1", "Artist 1", "CD123");
        cdService.addMedia(cd);
        user.addFine(15.0);

        Exception ex = assertThrows(IllegalStateException.class,
                () -> cdService.borrowMedia(user, "CD123"));
        assertEquals("Cannot borrow CD: overdue media or unpaid fines", ex.getMessage());
    }

    @Test
    void searchCDByTitleArtistId() {
        CD cd1 = new CD("Album One", "Artist 1", "CD123");
        CD cd2 = new CD("Album Two", "Artist 2", "CD456");
        cdService.addMedia(cd1);
        cdService.addMedia(cd2);

        List<CD> results1 = cdService.search("Album One");
        assertEquals(1, results1.size());
        List<CD> results2 = cdService.search("Artist 2");
        assertEquals(1, results2.size());
        List<CD> results3 = cdService.search("CD123");
        assertEquals(1, results3.size());
        List<CD> results4 = cdService.search("Nonexistent");
        assertEquals(0, results4.size());
    }

    @Test
    void cannotAddCDWithSameID() {
        CD cd1 = new CD("Album 1", "Artist 1", "CD123");
        CD cd2 = new CD("Album 2", "Artist 2", "CD123");
        cdService.addMedia(cd1);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> cdService.addMedia(cd2));
        assertEquals("CD with same ID already exists", ex.getMessage());
    }
    

    @Test
    void borrowMediaNullUserThrows() {
        CD cd = new CD("Album X", "Artist X", "CD999");
        cdService.addMedia(cd);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> cdService.borrowMedia(null, "CD999"));
        assertEquals("User cannot be null", ex.getMessage());
    }

    @Test
    void readFromFileInvalidDateHandled() throws IOException {
        // simulate invalid date in file
        File f = new File("data/cds.txt");
        f.getParentFile().mkdirs();
        try (var bw = new java.io.BufferedWriter(new java.io.FileWriter(f))) {
            bw.write("Title;Artist;CD999;true;invalid-date;null;0");
            bw.newLine();
        }

        List<CD> cds = cdService.getAllMedia();
        assertEquals(1, cds.size());
        assertNull(cds.get(0).getDueDate()); // invalid date should be handled
    }

    @Test
    void writeToFileHandlesUserNull() {
        CD cd = new CD("Album Y", "Artist Y", "CD999");
        cd.setBorrowedBy(null); // ensure branch where user is null
        assertDoesNotThrow(() -> cdService.writeToFile(List.of(cd)));
    }
}
