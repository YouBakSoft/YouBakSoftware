package service;

import domain.User;

public class EmailNotifier implements Observer {

    private final EmailService emailService;

    public EmailNotifier(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void notify(User user, String message) {
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            System.out.println("Cannot send email: user " + user.getName() + " has no email.");
            return;
        }
        emailService.sendEmail(email, message);
    }

}
