package tests;

import domain.User;
import org.junit.jupiter.api.*;
import service.BookService;
import service.CDService;
import service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class userServiceTest {

    private UserService userService;
    private BookService bookService;
    private CDService cdService;
    private List<User> memoryUsers;

    @BeforeEach
    void setUp() {
        memoryUsers = new ArrayList<>();

        userService = new UserService() {
            @Override
            public List<User> getAllUsers() {
                return new ArrayList<>(memoryUsers);
            }

            @Override
            public void saveUsers(List<User> users) {
                memoryUsers = new ArrayList<>(users);
            }
        };

        bookService = mock(BookService.class);
        cdService = mock(CDService.class);
    }

    @Test
    void testAddUserAndRetrieve() {
        User user = new User("Alice", "U001", "alice@example.com");
        userService.addUser(user);

        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("Alice", users.get(0).getName());
    }

    @Test
    void testAddDuplicateUser() {
        User user = new User("Alice", "U001", "alice@example.com");
        userService.addUser(user);
        userService.addUser(user); // duplicate
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    void testAddFineAndNullUser() {
        User user = new User("Bob", "U002", "bob@example.com");
        userService.addUser(user);

        userService.addFine(user, 50);
        assertEquals(50, userService.getAllUsers().get(0).getFineBalance());

        assertThrows(IllegalArgumentException.class, () -> userService.addFine(null, 10));
    }

    @Test
    void testApplyFineBranches() {
        User user = new User("Frank", "U006", "frank@example.com");
        userService.addUser(user);

        // valid fine
        userService.applyFine(user, 30);
        assertEquals(30, userService.getAllUsers().get(0).getFineBalance());

        // null user
        userService.applyFine(null, 10);

        // negative fine
        userService.applyFine(user, -5);

        // user not in list
        User outsider = new User("Ghost", "U999", "ghost@example.com");
        userService.applyFine(outsider, 20);

        assertEquals(30, userService.getAllUsers().get(0).getFineBalance());
    }

    @Test
    void testPayFinePartialAndFull() {
        User user = new User("Charlie", "U003", "charlie@example.com");
        userService.addUser(user);
        userService.addFine(user, 100);

        // partial payment
        userService.payFine(user, 40, bookService, cdService);
        assertEquals(60, userService.getAllUsers().get(0).getFineBalance());
        verify(bookService, never()).returnAllMediaForUser(user);
        verify(cdService, never()).returnAllMediaForUser(user);

        // full payment
        userService.payFine(user, 60, bookService, cdService);
        assertEquals(0, userService.getAllUsers().get(0).getFineBalance());
        verify(bookService).returnAllMediaForUser(user);
        verify(cdService).returnAllMediaForUser(user);
    }

    @Test
    void testPayFineNullInvalidAmounts() {
        User user = new User("Ivy", "U009", "ivy@example.com");
        userService.addUser(user);
        userService.addFine(user, 10);
        userService.payFine(user, 10, null, null); 
        assertEquals(0, userService.getAllUsers().get(0).getFineBalance());
        userService.addFine(user, 20);
        assertThrows(IllegalArgumentException.class,
                () -> userService.payFine(user, 0, bookService, cdService));
        assertThrows(IllegalArgumentException.class,
                () -> userService.payFine(user, 25, bookService, cdService)); 
        assertThrows(IllegalArgumentException.class,
                () -> userService.payFine(user, -5, bookService, cdService));
    }


    @Test
    void testUnregisterUserAndNull() {
        User user = new User("Eve", "U005", "eve@example.com");
        userService.addUser(user);

        boolean removed = userService.unregisterUser(user);
        assertTrue(removed);
        assertEquals(0, userService.getAllUsers().size());

        // removing again
        assertFalse(userService.unregisterUser(user));
        // null user
        assertFalse(userService.unregisterUser(null));
    }
}
