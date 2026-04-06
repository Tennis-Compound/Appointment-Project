public class SimpleNotificationService {
    public void sendBookingConfirmation(String userEmail, String message) {
        System.out.println("\n=== EMAIL NOTIFICATION ===");
        System.out.println("To: " + userEmail);
        System.out.println("Subject: Appointment Booking Confirmation");
        System.out.println("Message: " + message);
        System.out.println("Status: Sent successfully!");
        System.out.println("=========================\n");
    }
    
    public void sendCancellationNotice(String userEmail, String message) {
        System.out.println("\n=== EMAIL NOTIFICATION ===");
        System.out.println("To: " + userEmail);
        System.out.println("Subject: Appointment Cancellation");
        System.out.println("Message: " + message);
        System.out.println("Status: Sent successfully!");
        System.out.println("=========================\n");
    }
    
    public void sendModificationNotice(String userEmail, String oldDetails, String newDetails) {
        System.out.println("\n=== EMAIL NOTIFICATION ===");
        System.out.println("To: " + userEmail);
        System.out.println("Subject: Appointment Modified");
        System.out.println("Previous Appointment: " + oldDetails);
        System.out.println("New Appointment: " + newDetails);
        System.out.println("Status: Sent successfully!");
        System.out.println("=========================\n");
    }
}
