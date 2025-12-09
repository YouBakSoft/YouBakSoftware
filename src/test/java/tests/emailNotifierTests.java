package tests;

import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.EmailNotifier;
import service.EmailService;

import static org.mockito.Mockito.*;

class emailNotifierTests {

    private EmailService emailService;
    private EmailNotifier notifier;
    private User userWithEmail;
    private User userWithoutEmail;

    @BeforeEach
    void setup() {
        emailService = mock(EmailService.class); 
        notifier = new EmailNotifier(emailService);
        userWithEmail = new User("Alice", "U001", "alice@example.com");
        userWithoutEmail = new User("BoB", "U002", "placeholder@example.com");
        userWithoutEmail.setEmail(null);
    }

    @Test
    void notifyUserWithEmail() {
        notifier.notify(userWithEmail, "Test message");
        verify(emailService, times(1))
                .sendEmail("alice@example.com", "Test message");
    }

    @Test
    void notifyUserWithoutEmail() {
        notifier.notify(userWithoutEmail, "Test message");
        verify(emailService, never()).sendEmail(anyString(), anyString());
    }

    @Test
    void notifyUserWithBlankEmail() {
        User blankEmailUser = new User("Charlie", "U003", "placeholder@example.com");
        blankEmailUser.setEmail("   "); 
        notifier.notify(blankEmailUser, "Hello");
        verify(emailService, never()).sendEmail(anyString(), anyString());
    }
}
