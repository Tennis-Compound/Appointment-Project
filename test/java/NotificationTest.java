import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class NotificationTest {
    private MockNotificationService mockService;
    private NotificationManager notificationManager;
    
    @BeforeEach
    void setup() {
        mockService = new MockNotificationService();
        notificationManager = new NotificationManager(mockService);
    }
    
    @Test
    void shouldSendBookingConfirmation() {
        String email = "test@example.com";
        String details = "Appointment on 2025-01-15 at 10:00";
        
        notificationManager.sendBookingConfirmation(email, details);
        
        List<String> messages = mockService.getSentMessages();
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).contains("CONFIRMATION"));
        assertTrue(messages.get(0).contains(email));
        assertTrue(messages.get(0).contains(details));
    }
    
    @Test
    void shouldSendCancellationNotice() {
        String email = "test@example.com";
        String details = "Appointment on 2025-01-15 at 10:00";
        
        notificationManager.sendCancellationNotice(email, details);
        
        List<String> messages = mockService.getSentMessages();
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).contains("CANCELLATION"));
        assertTrue(messages.get(0).contains(email));
    }
    
    @Test
    void shouldSendModificationNotice() {
        String email = "test@example.com";
        String oldDetails = "Old appointment: 2025-01-15 10:00";
        String newDetails = "New appointment: 2025-01-16 14:00";
        
        notificationManager.sendModificationNotice(email, oldDetails, newDetails);
        
        List<String> messages = mockService.getSentMessages();
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).contains("modified"));
        assertTrue(messages.get(0).contains(oldDetails));
        assertTrue(messages.get(0).contains(newDetails));
    }
    
    @Test
    void shouldClearMessages() {
        notificationManager.sendBookingConfirmation("test@test.com", "test");
        assertEquals(1, mockService.getSentMessages().size());
        
        mockService.clearMessages();
        
        assertEquals(0, mockService.getSentMessages().size());
    }
    
    @AfterEach
    void cleanup() {
        mockService.clearMessages();
    }
}
