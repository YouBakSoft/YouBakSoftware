package service;

import domain.Media;
import domain.User;
import java.util.List;

public interface MediaService<T extends Media> {

    T addMedia(T media);
    T borrowMedia(User user, String identifier);
    List<T> getOverdueMedia();
    int calculateFine(T media);
    void returnAllMediaForUser(User user);
    List<T> search(String query);
}
