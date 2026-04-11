import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        String message = "Appointment booked successfully";
        
        notificationManager.sendBookingConfirmation(userEmail, message);
        
        verify(mockEmailService).sendBookingConfirmation(userEmail, message);
    }
    
    @Test
    void shouldSendCancellationNotice() {
        String userEmail = "test@example.com";
        String message = "Appointment cancelled";
        
        notificationManager.sendCancellationNotice(userEmail, message);
        
        verify(mockEmailService).sendCancellationNotice(userEmail, message);
    }
    
    @Test
    void shouldSendModificationNotice() {
        String userEmail = "test@example.com";
        String oldDetails = "Old appointment: 2025-01-15 10:00";
        String newDetails = "New appointment: 2025-01-16 14:00";
        
        notificationManager.sendModificationNotice(userEmail, oldDetails, newDetails);
        
        verify(mockEmailService).sendReminder(eq(userEmail), contains("modified"));
    }
}
