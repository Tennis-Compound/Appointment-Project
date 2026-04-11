import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        String bookingMessage = "Your appointment is confirmed";
        String cancellationMessage = "Your appointment has been cancelled";
        String oldDetails = "Old: Monday 10am";
        String newDetails = "New: Wednesday 2pm";
        
        notificationManager.sendBookingConfirmation(userEmail, bookingMessage);
        notificationManager.sendCancellationNotice(userEmail, cancellationMessage);
        notificationManager.sendModificationNotice(userEmail, oldDetails, newDetails);
        
        verify(mockEmailService).sendBookingConfirmation(userEmail, contains("Your appointment has been successfully booked"));
        verify(mockEmailService).sendCancellationNotice(userEmail, contains("Your appointment has been cancelled"));
        verify(mockEmailService).sendReminder(eq(userEmail), contains("Your appointment has been modified"));
    }
    
    @Test
    void shouldNotSendNotificationWhenManagerIsNotified() {
        String userEmail = "test@example.com";
        
        notificationManager.sendBookingConfirmation(userEmail, "Test message");
        
        verify(mockEmailService, times(1)).sendBookingConfirmation(userEmail, anyString());
    }
    
    @Test
    void shouldHandleMultipleReminderScheduling() {
        String userEmail = "test@example.com";
        long futureTime1 = System.currentTimeMillis() + 1000;
        long futureTime2 = System.currentTimeMillis() + 2000;
        
        notificationManager.scheduleReminder(userEmail, 1, "Reminder 1", futureTime1);
        notificationManager.scheduleReminder(userEmail, 2, "Reminder 2", futureTime2);
        notificationManager.cancelReminder(1);
        
        verifyNoInteractions(mockEmailService);
    }
}
