package service;

import domain.User;

public class EmailNotifier implements Observer{
	
	private EmailService emailService;

    public EmailNotifier(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void notify(User user, String message) {
        emailService.sendEmail(user.getId(), message);
    }

}
