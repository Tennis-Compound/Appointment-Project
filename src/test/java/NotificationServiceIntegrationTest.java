import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

public class NotificationServiceIntegrationTest {

    private NotificationService mockEmailService;
    private NotificationManager notificationManager;

    @BeforeEach
    void setup() {
        mockEmailService = mock(NotificationService.class);
        notificationManager = new NotificationManager(mockEmailService);
    }

    @Test
    void shouldSendAllNotificationTypes() {
        String userEmail = "test@example.com";
        String bookingDetails  = "Slot: Monday 10am";
        String cancelDetails   = "Slot: Tuesday 2pm";
        String oldDetails      = "Old: Monday 10am";
        String newDetails      = "New: Wednesday 2pm";

        notificationManager.sendBookingConfirmation(userEmail, bookingDetails);
        notificationManager.sendCancellationNotice(userEmail, cancelDetails);
        notificationManager.sendModificationNotice(userEmail, oldDetails, newDetails);

        verify(mockEmailService).sendBookingConfirmation(
                eq(userEmail), contains("successfully booked"));
        verify(mockEmailService).sendCancellationNotice(
                eq(userEmail), contains("has been cancelled"));
        verify(mockEmailService).sendReminder(
                eq(userEmail), contains("has been modified"));
    }

    @Test
    void bookingConfirmationMessageShouldContainPassedDetails() {
        String userEmail = "test@example.com";
        String bookingDetails = "Room 101, 10am";

        notificationManager.sendBookingConfirmation(userEmail, bookingDetails);

        verify(mockEmailService).sendBookingConfirmation(
                eq(userEmail), contains(bookingDetails));
    }

    @Test
    void cancellationMessageShouldContainPassedDetails() {
        String userEmail = "test@example.com";
        String cancelDetails = "Cancelled slot: Thursday 3pm";

        notificationManager.sendCancellationNotice(userEmail, cancelDetails);

        verify(mockEmailService).sendCancellationNotice(
                eq(userEmail), contains(cancelDetails));
    }

    @Test
    void modificationMessageShouldContainBothOldAndNewDetails() {
        String userEmail = "test@example.com";
        String oldDetails = "Old: Monday 10am";
        String newDetails = "New: Wednesday 2pm";

        notificationManager.sendModificationNotice(userEmail, oldDetails, newDetails);

        verify(mockEmailService).sendReminder(eq(userEmail), contains(oldDetails));
        verify(mockEmailService).sendReminder(eq(userEmail), contains(newDetails));
    }

    @Test
    void shouldSendExactlyOneNotificationPerCall() {
        String userEmail = "test@example.com";

        notificationManager.sendBookingConfirmation(userEmail, "details");

        verify(mockEmailService, times(1)).sendBookingConfirmation(eq(userEmail), anyString());
        verify(mockEmailService, never()).sendReminder(anyString(), anyString());
        verify(mockEmailService, never()).sendCancellationNotice(anyString(), anyString());
    }

    @Test
    void shouldHandleMultipleReminderSchedulingAndCancellation() {
        String userEmail = "test@example.com";
        long futureTime1 = System.currentTimeMillis() + 48L * 60 * 60 * 1000; // 48h ahead
        long futureTime2 = System.currentTimeMillis() + 72L * 60 * 60 * 1000; // 72h ahead

        notificationManager.scheduleReminder(userEmail, 1, "Reminder 1", futureTime1);
        notificationManager.scheduleReminder(userEmail, 2, "Reminder 2", futureTime2);
        notificationManager.cancelReminder(1);

        // No immediate interactions — reminders fire in the future
        verifyNoInteractions(mockEmailService);
    }

    @Test
    void cancellingNonExistentReminderShouldNotThrow() {
        assertDoesNotThrow(() -> notificationManager.cancelReminder(9999));
        verifyNoInteractions(mockEmailService);
    }
}
