/**
 * A simple, lightweight implementation of {@link NotificationService} that
 * logs messages to standard output instead of sending real emails.
 *
 * Use this class in development or demos when SMTP is not configured.
 */
public class SimpleNotificationService implements NotificationService {

    @Override
    public void sendReminder(String userEmail, String message) {
        log("REMINDER", userEmail, message);
    }

    @Override
    public void sendBookingConfirmation(String userEmail, String message) {
        log("BOOKING CONFIRMATION", userEmail, message);
    }

    @Override
    public void sendCancellationNotice(String userEmail, String message) {
        log("CANCELLATION NOTICE", userEmail, message);
    }

    /**
     * Sends a modification notice describing what changed.
     *
     * @param userEmail  recipient address
     * @param oldDetails description of the previous appointment
     * @param newDetails description of the updated appointment
     */
    public void sendModificationNotice(String userEmail,
                                       String oldDetails,
                                       String newDetails) {
        String body = "Previous: " + oldDetails + "\nNew: " + newDetails;
        log("MODIFICATION NOTICE", userEmail, body);
    }

    private void log(String type, String userEmail, String message) {
        System.out.println("[SimpleNotificationService] " + type
                + " -> " + userEmail + ": " + message);
    }
}
