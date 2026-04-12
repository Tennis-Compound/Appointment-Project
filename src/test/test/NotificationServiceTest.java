import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

public class NotificationServiceTest {

    private NotificationService mockEmailService;
    private NotificationManager notificationManager;

    @BeforeEach
    void setup() {
        mockEmailService = mock(NotificationService.class);
        notificationManager = new NotificationManager(mockEmailService);
    }

    @Test
    void shouldSendBookingConfirmation() {
        String userEmail = "test@example.com";
        String details = "Appointment booked successfully";

        notificationManager.sendBookingConfirmation(userEmail, details);

        // NotificationManager wraps the details inside a full message —
        // verify the service was called with the email and a message that
        // contains the key phrase from createBookingConfirmationMessage().
        verify(mockEmailService).sendBookingConfirmation(
                eq(userEmail),
                contains("successfully booked"));
    }

    @Test
    void shouldSendBookingConfirmationContainingOriginalDetails() {
        String userEmail = "test@example.com";
        String details = "Monday 10am Room 5";

        notificationManager.sendBookingConfirmation(userEmail, details);

        verify(mockEmailService).sendBookingConfirmation(
                eq(userEmail),
                contains(details));
    }

    @Test
    void shouldSendCancellationNotice() {
        String userEmail = "test@example.com";
        String details = "Appointment cancelled";

        notificationManager.sendCancellationNotice(userEmail, details);

        // createCancellationMessage wraps the details — check key phrase
        verify(mockEmailService).sendCancellationNotice(
                eq(userEmail),
                contains("has been cancelled"));
    }

    @Test
    void shouldSendCancellationNoticeContainingOriginalDetails() {
        String userEmail = "test@example.com";
        String details = "Slot on Tuesday 2pm";

        notificationManager.sendCancellationNotice(userEmail, details);

        verify(mockEmailService).sendCancellationNotice(
                eq(userEmail),
                contains(details));
    }

    @Test
    void shouldSendModificationNotice() {
        String userEmail = "test@example.com";
        String oldDetails = "Old appointment: 2025-01-15 10:00";
        String newDetails = "New appointment: 2025-01-16 14:00";

        notificationManager.sendModificationNotice(userEmail, oldDetails, newDetails);

        verify(mockEmailService).sendReminder(eq(userEmail), contains("modified"));
    }

    @Test
    void shouldSendModificationNoticeContainingBothDetails() {
        String userEmail = "test@example.com";
        String oldDetails = "Old: Monday 10am";
        String newDetails = "New: Wednesday 2pm";

        notificationManager.sendModificationNotice(userEmail, oldDetails, newDetails);

        verify(mockEmailService).sendReminder(eq(userEmail), contains(oldDetails));
        verify(mockEmailService).sendReminder(eq(userEmail), contains(newDetails));
    }
}
