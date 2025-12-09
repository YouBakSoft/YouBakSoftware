package tests;
import domain.User;
import domain.Media;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.MultiMediaService;
import service.FineStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class multiMediaServiceTest {

    private MultiMediaService<Media> service;
    private Media media;
    private User user;
    private FineStrategy fineStrategy;

    @BeforeEach
    void setup() {
        fineStrategy = mock(FineStrategy.class);
        when(fineStrategy.calculateFine(anyInt())).thenReturn(10);
        media = new Media("Test Media") {
            @Override
            public void borrow(User user) {
                this.setBorrowedBy(user);
                this.setAvailable(false);
                this.setDueDate(LocalDate.now().minusDays(3));
            }
        };
        media.setFineStrategy(fineStrategy);
        user = mock(User.class);
        when(user.canBorrow()).thenReturn(true);
        service = new MultiMediaService<>() {
            private final List<Media> storage = new ArrayList<>();
            @Override
            protected List<Media> readFromFile() {
            	return storage;
            }
            @Override
            protected void writeToFile(List<Media> list) {
            	storage.clear();
            	storage.addAll(list);
            }
            @Override
            public Media addMedia(Media media) {
            	storage.add(media);
            	return media;
            }
            @Override
            public Media borrowMedia(User user, String identifier) {
            	media.borrow(user);
            	return media;
            }
            @Override
            public List<Media> search(String query) {
            	return List.of(media);
            }
        };
        service.setFineStrategy(fineStrategy);
    }

    @Test
    void calculateFineDelegatesToStrategy() {
        media.borrow(user);
        int fine = service.calculateFine(media);
        assertEquals(10, fine);
        verify(fineStrategy).calculateFine(3); 
    }


    @Test
    void getOverdueMediaIncludesOverdue() {
        media.borrow(user); 
        service.addMedia(media);
        List<Media> overdue = service.getOverdueMedia();
        assertTrue(overdue.contains(media));
    }


    @Test
    void canUserBorrowReturnsFalseIfUserCannotBorrow() {
        when(user.canBorrow()).thenReturn(false);
        assertFalse(service.canUserBorrow(user, List.of(media)));
    }

    @Test
    void returnAllMediaForUserMarksMediaAsReturned() {
        service.addMedia(media);
        media.borrow(user);
        service.returnAllMediaForUser(user);
        assertTrue(media.isAvailable());
        assertNull(media.getBorrowedBy());
        assertNull(media.getDueDate());
        assertEquals(0, media.getFineApplied());
    }
    
    @Test
    void calculateFineReturnsZeroIfAvailable() {
        media.setAvailable(true);
        media.setDueDate(LocalDate.now().minusDays(3));
        assertEquals(0, service.calculateFine(media));
    }

    @Test
    void calculateFineReturnsZeroIfNoDueDate() {
        media.setAvailable(false);
        media.setDueDate(null);
        assertEquals(0, service.calculateFine(media));
    }

    @Test
    void canUserBorrowReturnsFalseIfOverdueMediaExists() {
        media.borrow(user);
        List<Media> allMedia = List.of(media);
        assertFalse(service.canUserBorrow(user, allMedia));
    }

    @Test
    void hasActiveLoansReturnsFalseIfNoLoans() {
        assertFalse(service.hasActiveLoans(user));
    }

    @Test
    void hasActiveLoansReturnsTrueIfLoansExist() {
        media.borrow(user);
        service.addMedia(media);
        assertTrue(service.hasActiveLoans(user));
    }

    @Test
    void sendRemindersNotifiesObservers() {
        var observer = mock(service.Observer.class);
        service.addObserver(observer);
        media.borrow(user);
        service.addMedia(media);
        service.sendReminders(List.of(user), "Book");
        verify(observer).notify(user, "You have 1 overdue Book(s).");
    }

}
