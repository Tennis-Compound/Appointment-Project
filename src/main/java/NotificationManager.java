import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Orchestrates appointment notifications by delegating to an underlying
 * {@link NotificationService} implementation.
 *
 * Refactored: the four near-identical private message-builder methods have been
 * merged into a single buildMessage() helper, eliminating the duplicated-lines
 * SonarCloud finding while keeping ALL message strings identical so existing
 * tests continue to pass.
 */
public class NotificationManager {

    private static final long ONE_DAY_MS = 24L * 60 * 60 * 1_000;

    private final NotificationService notificationService;
    private final Map<Integer, Timer> reminderTimers = new ConcurrentHashMap<>();

    public NotificationManager(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ── public API ────────────────────────────────────────────────────────────

    public void sendBookingConfirmation(String userEmail, String appointmentDetails) {
        String message = buildMessage(
                "Your appointment has been successfully booked!",
                "Appointment Details",
                appointmentDetails,
                "Important Information:\n"
                + "• Please arrive 10 minutes before your scheduled time\n"
                + "• Bring any necessary documents\n"
                + "• If you need to reschedule, visit our system\n\n"
                + "Thank you for using our Appointment System!\n"
                + "For inquiries: support@appointmentsystem.com");
        notificationService.sendBookingConfirmation(userEmail, message);
    }

    public void sendCancellationNotice(String userEmail, String appointmentDetails) {
        String message = buildMessage(
                "Your appointment has been cancelled.",
                "Cancelled Appointment Details",
                appointmentDetails,
                "If this was a mistake, please book a new appointment through our system.\n\n"
                + "Thank you for understanding.\n"
                + "Appointment System Team");
        notificationService.sendCancellationNotice(userEmail, message);
    }

    public void scheduleReminder(String userEmail, int appointmentId,
                                 String appointmentDetails, long appointmentTime) {
        cancelReminder(appointmentId);

        long delay = (appointmentTime - ONE_DAY_MS) - System.currentTimeMillis();
        if (delay > 0) {
            Timer timer = new Timer("Reminder-" + appointmentId, true);
            reminderTimers.put(appointmentId, timer);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String message = buildMessage(
                            "This is a friendly reminder about your upcoming appointment.",
                            "Appointment Details",
                            appointmentDetails,
                            "Reminder Details:\n"
                            + "• Time remaining: 24 hours\n"
                            + "• Please arrive 10 minutes early\n"
                            + "• Bring required documents\n\n"
                            + "If you need to reschedule or cancel, please visit our system.\n\n"
                            + "We look forward to seeing you!");
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
        String message = "Your appointment has been modified.\n\n"
                + "Previous Appointment:\n" + oldDetails + "\n\n"
                + "New Appointment:\n" + newDetails + "\n\n"
                + "Please update your calendar accordingly.\n"
                + "If you didn't request this change, please contact us immediately.\n\n"
                + "Thank you!";
        notificationService.sendReminder(userEmail, message);
    }

    // ── private helper ────────────────────────────────────────────────────────

    private String buildMessage(String headline, String detailsLabel,
                                String details, String footer) {
        return headline + "\n\n"
                + detailsLabel + ":\n" + details + "\n\n"
                + footer;
    }
}
