import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AppointmentRequestTest {
    
    @Test
    void shouldCreateAppointmentRequest() {
        AppointmentRequest request = new AppointmentRequest("URGENT", 30, 1, "Room 101");
        
        assertEquals("URGENT", request.getAppointmentType());
        assertEquals(30, request.getDurationMinutes());
        assertEquals(1, request.getParticipantCount());
        assertEquals("Room 101", request.getLocation());
    }
    
    @Test
    void shouldHandleEmptyLocation() {
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 45, 2, "");
        
        assertEquals("", request.getLocation());
    }
    
    @Test
    void shouldHandleNullLocation() {
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 45, 2, null);
        
        assertNull(request.getLocation());
    }
}
