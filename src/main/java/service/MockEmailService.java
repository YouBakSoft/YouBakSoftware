package service;

import java.util.ArrayList;
import java.util.List;

public class MockEmailService implements EmailService {
	 private final List<String> sentMessages = new ArrayList<>();  // to store sent messages 

	    @Override
	    public void sendEmail(String to, String message) {
	        sentMessages.add("To: " + to + " | Message: " + message);
	    }

	    public List<String> getSentMessages() {
	        return sentMessages;
	    }

}
