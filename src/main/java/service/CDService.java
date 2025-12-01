package service;

import domain.CD;
import domain.Media;
import domain.User;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CDService extends MultiMediaService<CD> {

    private final String FILE_PATH = "data/cds.txt";

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
    public List<CD> getAllMedia() {
        return readFromFile();
    }

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

	@Override
	protected List<CD> readFromFile() {
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
                if (parts.length >= 7) {
                    cd.setFineApplied(Integer.parseInt(parts[6].trim()));
                } else {
                    cd.setFineApplied(0);
                }
                cds.add(cd);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading CDs file", e);
        }
        return cds;
	}

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
	                    Integer.toString(cd.getFineApplied()  
	            )));
	            bw.newLine();
	        }
	    } catch (IOException e) {
	        throw new RuntimeException("Error writing CDs file", e);
	    }
	}

}
