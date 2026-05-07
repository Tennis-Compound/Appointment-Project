public class SimpleNotificationService implements NotificationService {

    public SimpleNotificationService() {}

    @Override
    public void sendBookingConfirmation(String email, String message) {
        System.out.println("Booking confirmation sent to " + email + ": " + message);
    }

    @Override
    public void sendCancellationNotice(String email, String message) {
        System.out.println("Cancellation notice sent to " + email + ": " + message);
    }

    @Override
    public void sendReminder(String email, String message) {
        System.out.println("Reminder sent to " + email + ": " + message);
    }

    public void sendModificationNotice(String email, String oldDetails, String newDetails) {
        System.out.println("Modification notice sent to " + email
            + " | Old: " + oldDetails + " | New: " + newDetails);
    }
}
