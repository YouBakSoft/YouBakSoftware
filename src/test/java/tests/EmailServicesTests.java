package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.EmailService;

import static org.mockito.Mockito.*;

class EmailServicesTests {

    private EmailService emailService;

    @BeforeEach
    void setup() {
        emailService = mock(EmailService.class);
    }

    @Test
    void testSendEmailIsCalled() {
        String to = "user@example.com";
        String message = "Hello, this is a test.";

        // Call the method
        emailService.sendEmail(to, message);

        // Verify it was called with correct arguments
        verify(emailService).sendEmail(to, message);
    }
}
