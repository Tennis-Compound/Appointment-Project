import java.util.ArrayList;
import java.util.List;

public class MockNotificationService implements NotificationService {
    private List<String> sentMessages = new ArrayList<>();
    
    @Override
    public void sendReminder(String userEmail, String message) {
        String logEntry = "REMINDER to " + userEmail + ": " + message;
        sentMessages.add(logEntry);
        System.out.println("[MOCK NOTIFICATION] " + logEntry);
    }
    
    @Override
    public void sendBookingConfirmation(String userEmail, String message) {
        String logEntry = "CONFIRMATION to " + userEmail + ": " + message;
        sentMessages.add(logEntry);
        System.out.println("[MOCK NOTIFICATION] " + logEntry);
    }
    
    @Override
    public void sendCancellationNotice(String userEmail, String message) {
        String logEntry = "CANCELLATION to " + userEmail + ": " + message;
        sentMessages.add(logEntry);
        System.out.println("[MOCK NOTIFICATION] " + logEntry);
    }
    
    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }
    
    public void clearMessages() {
        sentMessages.clear();
    }
}
