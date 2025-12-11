package service;

import domain.CD;
import domain.Media;
import domain.User;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class to manage {@link CD} objects.
 * Extends {@link MultiMediaService} and provides functionality for adding, borrowing,
 * searching, and persisting CDs to a file.
 *
 * <p>Example usage:
 * <pre><code>
 * CDService cdService = new CDService();
 * CD cd = new CD("Album Title", "Artist Name", "CD123");
 * cdService.addMedia(cd);
 * User user = new User("Alice", "U001", "alice@example.com");
 * cdService.borrowMedia(user, "CD123");
 * </code></pre>
 *
 * @since 1.0
 * @see CD
 * @see MultiMediaService
 */
public class CDService extends MultiMediaService<CD> {

    /** Path to the file where CDs are stored */
    private final String FILE_PATH = "data/cds.txt";

    /**
     * Constructs a CDService and ensures the data file exists.
     * If the file or directories do not exist, they will be created.
     *
     * @since 1.0
     */
    public CDService() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                boolean created = file.createNewFile(); 
                if (created) {
                    System.out.println("cds.txt file created successfully.");
                } else {
                    System.out.println("cds.txt already exists.");
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot create cds.txt", e);
            }
        }
    }


    /**
     * Returns a list of all CDs in the system.
     *
     * @return List of {@link CD}
     * @since 1.0
     */
    public List<CD> getAllMedia() {
        return readFromFile();
    }

    /**
     * Adds a new CD to the system.
     * Validates non-null/non-empty ID.
     * Throws an exception if a CD with the same ID already exists.
     *
     * @param cd The {@link CD} to add
     * @return The added CD
     * @throws IllegalArgumentException If validation fails or ID already exists
     * @since 1.0
     */
    @Override
    public CD addMedia(CD cd) {
        if (cd.getId() == null || cd.getId().isEmpty()) 
            throw new IllegalArgumentException("CD ID cannot be null");

        List<CD> cds = readFromFile();
        for (CD c : cds) {
            if (c.getId().equals(cd.getId()))
                throw new IllegalArgumentException("CD with same ID already exists");
        }
        cds.add(cd);
        writeToFile(cds);
        return cd;
    }

    /**
     * Borrows a CD for a given user by ID.
     * Checks user eligibility (overdue CDs or unpaid fines).
     *
     * @param user The {@link User} borrowing the CD
     * @param id   The ID of the CD to borrow
     * @return The borrowed {@link CD}
     * @throws IllegalArgumentException If the CD or user is invalid
     * @throws IllegalStateException    If the CD is already borrowed or user cannot borrow
     * @since 1.0
     */
    @Override
    public CD borrowMedia(User user, String id) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");

        List<CD> cds = readFromFile();
        if (!canUserBorrow(user, new ArrayList<>(cds))) {
            throw new IllegalStateException("Cannot borrow CD: overdue media or unpaid fines");
        }
        for (CD cd : cds) {
            if (cd.getId().equals(id)) {
                if (!cd.isAvailable()) throw new IllegalStateException("CD already borrowed");
                cd.borrow(user);
                writeToFile(cds);
                return cd;
            }
        }

        throw new IllegalArgumentException("CD not found");
    }

    /**
     * Searches CDs by title, artist, or ID (case-insensitive).
     *
     * @param query The search string
     * @return List of {@link CD} that match the query
     * @since 1.0
     */
    @Override
    public List<CD> search(String query) {
        if (query == null) return new ArrayList<>();

        String q = query.toLowerCase();

        return readFromFile().stream()
                .filter(cd ->
                        cd.getTitle().toLowerCase().contains(q) ||
                        cd.getArtist().toLowerCase().contains(q) ||
                        cd.getId().toLowerCase().contains(q)
                )
                .toList();
    }

    /**
     * Reads CDs from the storage file.
     *
     * @return List of {@link CD} read from the file
     * @throws RuntimeException If file cannot be read
     * @since 1.0
     */
    @Override
    protected List<CD> readFromFile() {
        List<CD> cds = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = br.readLine()) != null) {
                CD cd = parseCdLine(line);
                if (cd != null) {
                    cds.add(cd);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading CDs file", e);
        }

        return cds;
    }
    private CD parseCdLine(String line) {
        String[] parts = line.split(";");
        if (parts.length < 4) return null;

        CD cd = new CD(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim()
        );

        cd.setAvailable(Boolean.parseBoolean(parts[3].trim()));

        setDueDate(parts, cd);
        setBorrower(parts, cd);
        setFine(parts, cd);

        return cd;
    }
    private void setDueDate(String[] parts, CD cd) {
        if (parts.length < 5) return;

        String rawDate = parts[4].trim();
        if ("null".equals(rawDate)) return;

        try {
            cd.setDueDate(LocalDate.parse(rawDate));
        } catch (Exception e) {
            System.out.println("Warning: invalid date for CD " + cd.getTitle());
        }
    }

    private void setBorrower(String[] parts, CD cd) {
        if (parts.length < 6 || userService == null) return;

        String userId = parts[5].trim();

        User u = userService.getAllUsers()
                .stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);

        cd.setBorrowedBy(u);
    }

    private void setFine(String[] parts, CD cd) {
        if (parts.length >= 7) {
            cd.setFineApplied(Integer.parseInt(parts[6].trim()));
        } else {
            cd.setFineApplied(0);
        }
    }

    /**
     * Writes a list of CDs to the storage file.
     *
     * @param list List of {@link CD} to write
     * @throws RuntimeException If file cannot be written
     * @since 1.0
     */
    @Override
    public void writeToFile(List<CD> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (CD cd : list) {
                String userId = (cd.getBorrowedBy() != null)
                        ? cd.getBorrowedBy().getId()
                        : "null";

                String due = (cd.getDueDate() != null)
                        ? cd.getDueDate().toString()
                        : "null";

                bw.write(String.join(";",
                        cd.getTitle(),
                        cd.getArtist(),
                        cd.getId(),
                        Boolean.toString(cd.isAvailable()),
                        due,
                        userId,
                        Integer.toString(cd.getFineApplied())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing CDs file", e);
        }
    }
}
