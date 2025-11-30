package service;

import domain.CD;
import domain.Media;
import domain.User;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CDService implements MediaService<CD> {

    private final String FILE_PATH = "data/cds.txt";
    private FineStrategy fineStrategy;
    private List<Observer> observers = new ArrayList<>();
    private UserService userService;

    public CDService() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Cannot create cds.txt", e);
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

    // ====================== FILE OPERATIONS ======================
    private List<CD> readCDsFromFile() {
        List<CD> cds = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                CD cd = new CD(parts[0].trim(), parts[1].trim(), parts[2].trim());
                cd.setAvailable(Boolean.parseBoolean(parts[3].trim()));

                if (parts.length >= 5 && !"null".equals(parts[4].trim())) {
                    try {
                        cd.setDueDate(LocalDate.parse(parts[4].trim()));
                    } catch (Exception e) {
                        System.out.println("Warning: invalid date for CD " + cd.getTitle());
                    }
                }

                if (parts.length >= 6 && userService != null) {
                    String userId = parts[5].trim();
                    User u = userService.getAllUsers().stream()
                            .filter(user -> user.getId().equals(userId))
                            .findFirst()
                            .orElse(null);
                    cd.setBorrowedBy(u);
                }

                cds.add(cd);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading CDs file", e);
        }
        return cds;
    }

    private void writeCDsToFile(List<CD> cds) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (CD cd : cds) {
                String userId = (cd.getBorrowedBy() != null) ? cd.getBorrowedBy().getId() : "null";
                String due = (cd.getDueDate() != null) ? cd.getDueDate().toString() : "null";
                bw.write(String.join(";", cd.getTitle(), cd.getArtist(), cd.getId(),
                        Boolean.toString(cd.isAvailable()), due, userId));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing CDs file", e);
        }
    }

    // ====================== MEDIA SERVICE METHODS ======================
    @Override
    public CD addMedia(CD cd) {
        if (cd.getId() == null || cd.getId().isEmpty()) 
            throw new IllegalArgumentException("CD ID cannot be null");

        List<CD> cds = readCDsFromFile();
        for (CD c : cds) {
            if (c.getId().equals(cd.getId()))
                throw new IllegalArgumentException("CD with same ID already exists");
        }
        cds.add(cd);
        writeCDsToFile(cds);
        return cd;
    }

    @Override
    public CD borrowMedia(User user, String id) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");

        List<CD> cds = readCDsFromFile();
        if (!canUserBorrow(user, new ArrayList<>(cds))) {
            throw new IllegalStateException("Cannot borrow CD: overdue media or unpaid fines");
        }

        for (CD cd : cds) {
            if (cd.getId().equals(id)) {
                if (!cd.isAvailable()) throw new IllegalStateException("CD already borrowed");
                cd.borrow(user);
                writeCDsToFile(cds);
                return cd;
            }
        }

        throw new IllegalArgumentException("CD not found");
    }

    @Override
    public List<CD> getOverdueMedia() {
        return readCDsFromFile().stream()
                .filter(cd -> !cd.isAvailable())
                .filter(cd -> cd.getDueDate() != null)
                .filter(cd -> cd.getBorrowedBy() != null)
                .filter(cd -> LocalDate.now().isAfter(cd.getDueDate()))
                .collect(Collectors.toList());
    }

    @Override
    public int calculateFine(CD cd) {
        if (cd.getDueDate() == null || cd.isAvailable()) return 0;
        long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(cd.getDueDate(), LocalDate.now());
        if (overdueDays > 0 && fineStrategy != null) {
            return fineStrategy.calculateFine((int) overdueDays);
        }
        return 0;
    }


    @Override
    public void returnAllMediaForUser(User user) {
        List<CD> cds = readCDsFromFile();
        for (CD cd : cds) {
            if (user.equals(cd.getBorrowedBy())) {
                cd.setAvailable(true);
                cd.setBorrowedBy(null);
                cd.setDueDate(null);
                cd.setFineApplied(0);
            }
        }
        writeCDsToFile(cds);
    }

    @Override
    public List<CD> search(String query) {
        if (query == null) return new ArrayList<>();
        String q = query.toLowerCase();
        return readCDsFromFile().stream()
                .filter(cd -> cd.getTitle().toLowerCase().contains(q) || cd.getArtist().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    // ====================== ADDITIONAL UTILITIES ======================
    public boolean canUserBorrow(User user, List<Media> allMedia) {
        if (!user.canBorrow()) return false;
        for (Media m : allMedia) {
            if (!m.isAvailable() && user.equals(m.getBorrowedBy()) && m.isOverdue()) return false;
        }
        return true;
    }

    public void sendReminders(List<User> users) {
        List<CD> overdueCDs = getOverdueMedia();
        for (User user : users) {
            long count = overdueCDs.stream()
                    .filter(cd -> user.equals(cd.getBorrowedBy()))
                    .count();
            if (count > 0) {
                notifyObservers(user, "You have " + count + " overdue CD(s).");
            }
        }
    }
}
