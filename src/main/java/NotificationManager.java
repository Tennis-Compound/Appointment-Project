import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class NotificationManager {
    private final NotificationService notificationService;
    private final Map<Integer, Timer> reminderTimers = new ConcurrentHashMap<>();
    
    public NotificationManager(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    public void sendBookingConfirmation(String userEmail, String appointmentDetails) {
        String message = createBookingConfirmationMessage(appointmentDetails);
        notificationService.sendBookingConfirmation(userEmail, message);
    }
    
    public void sendCancellationNotice(String userEmail, String appointmentDetails) {
        String message = createCancellationMessage(appointmentDetails);
        notificationService.sendCancellationNotice(userEmail, message);
    }
    
    public void scheduleReminder(String userEmail, int appointmentId, 
                               String appointmentDetails, long appointmentTime) {
        cancelReminder(appointmentId);
        
        long reminderTime = appointmentTime - (24L * 60 * 60 * 1000);
        long currentTime = System.currentTimeMillis();
        long delay = reminderTime - currentTime;
        
        if (delay > 0) {
            Timer timer = new Timer("Reminder-" + appointmentId, true);
            reminderTimers.put(appointmentId, timer);
            
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String message = createReminderMessage(appointmentDetails);
                    notificationService.sendReminder(userEmail, message);
                    reminderTimers.remove(appointmentId);
                }
            }, delay);
            
            System.out.println("Reminder scheduled for appointment ID: " + appointmentId);
        }
    }
    
    public void cancelReminder(int appointmentId) {
        Timer timer = reminderTimers.remove(appointmentId);
        if (timer != null) {
            timer.cancel();
            System.out.println("Reminder cancelled for appointment ID: " + appointmentId);
        }
    }
    
    public void sendModificationNotice(String userEmail, String oldDetails, String newDetails) {
        String message = createModificationMessage(oldDetails, newDetails);
        notificationService.sendReminder(userEmail, message);
    }
    
    private String createBookingConfirmationMessage(String appointmentDetails) {
        return "Your appointment has been successfully booked!\n\n" +
               "Appointment Details:\n" + appointmentDetails + "\n\n" +
               "Important Information:\n" +
               "• Please arrive 10 minutes before your scheduled time\n" +
               "• Bring any necessary documents\n" +
               "• If you need to reschedule, visit our system\n\n" +
               "Thank you for using our Appointment System!\n" +
               "For inquiries: support@appointmentsystem.com";
    }
    
    private String createCancellationMessage(String appointmentDetails) {
        return "Your appointment has been cancelled.\n\n" +
               "Cancelled Appointment Details:\n" + appointmentDetails + "\n\n" +
               "If this was a mistake, please book a new appointment through our system.\n\n" +
               "Thank you for understanding.\n" +
               "Appointment System Team";
    }
    
    private String createReminderMessage(String appointmentDetails) {
        return "This is a friendly reminder about your upcoming appointment.\n\n" +
               "Appointment Details:\n" + appointmentDetails + "\n\n" +
               "Reminder Details:\n" +
               "• Time remaining: 24 hours\n" +
               "• Please arrive 10 minutes early\n" +
               "• Bring required documents\n\n" +
               "If you need to reschedule or cancel, please visit our system.\n\n" +
               "We look forward to seeing you!";
    }
    
    private String createModificationMessage(String oldDetails, String newDetails) {
        return "Your appointment has been modified.\n\n" +
               "Previous Appointment:\n" + oldDetails + "\n\n" +
               "New Appointment:\n" + newDetails + "\n\n" +
               "Please update your calendar accordingly.\n" +
               "If you didn't request this change, please contact us immediately.\n\n" +
               "Thank you!";
    }
}
