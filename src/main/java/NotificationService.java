
public interface NotificationService {
    void sendReminder(String userEmail, String message);
    void sendBookingConfirmation(String userEmail, String message);
    void sendCancellationNotice(String userEmail, String message);
}
