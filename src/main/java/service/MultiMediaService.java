package service;

import domain.Book;
import domain.Media;
import domain.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class MultiMediaService<T extends Media>
        implements MediaService<T> {

    protected FineStrategy fineStrategy;
    protected List<Observer> observers = new ArrayList<>();
    protected UserService userService;

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

    protected void notifyObservers(User user, String message) {
        for (Observer o : observers) o.notify(user, message);
    }

    @Override
    public int calculateFine(T media) {
        if (media.getDueDate() == null || media.isAvailable()) return 0;

        long overdueDays = java.time.temporal.ChronoUnit.DAYS
                .between(media.getDueDate(), LocalDate.now());

        if (overdueDays > 0 && fineStrategy != null) {
            return fineStrategy.calculateFine((int) overdueDays);
        }
        return 0;
    }

    public boolean canUserBorrow(User user, List<Media> allMedia) {
        if (!user.canBorrow()) return false;

        for (Media m : allMedia) {
            if (!m.isAvailable()
                    && user.equals(m.getBorrowedBy())
                    && m.isOverdue()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean hasActiveLoans(User user) {
        if (user == null) return false;
        return readFromFile().stream()
                .anyMatch(m -> !m.isAvailable()
                        && user.equals(m.getBorrowedBy()));
    }



    @Override
    public void returnAllMediaForUser(User user) {
        List<T> mediaList = readFromFile();

        for (T m : mediaList) {
            if (user.equals(m.getBorrowedBy())) {
                m.setAvailable(true);
                m.setBorrowedBy(null);
                m.setDueDate(null);
                m.setFineApplied(0);
            }
        }
        writeToFile(mediaList);
    }

    @Override
    public List<T> getOverdueMedia() {
        return readFromFile().stream()
                .filter(m -> !m.isAvailable())
                .filter(m -> m.getDueDate() != null)
                .filter(m -> m.getBorrowedBy() != null)
                .filter(m -> LocalDate.now().isAfter(m.getDueDate()))
                .toList();
    }
    
    public void sendReminders(List<User> users, String mediaLabel) {
        List<T> overdue = getOverdueMedia();

        for (User user : users) {
            long count = overdue.stream()
                    .filter(m -> user.equals(m.getBorrowedBy()))
                    .count();

            if (count > 0) {
                notifyObservers(user,
                        "You have " + count + " overdue " + mediaLabel + "(s).");
            }
        }
    }
    

    protected abstract List<T> readFromFile();
    protected abstract void writeToFile(List<T> list);
}
