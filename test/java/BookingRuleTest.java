import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookingRuleTest {
    
    @Test
    void urgentRuleShouldAcceptShortDuration() {
        AppointmentRequest request = new AppointmentRequest("URGENT", 30, 1, "");
        BookingRuleStrategy rule = new UrgentBookingRule();
        
        assertTrue(rule.isValid(request));
    }
    
    @Test
    void urgentRuleShouldRejectLongDuration() {
        AppointmentRequest request = new AppointmentRequest("URGENT", 60, 1, "");
        BookingRuleStrategy rule = new UrgentBookingRule();
        
        assertFalse(rule.isValid(request));
        assertEquals("Urgent appointment must be 30 minutes or less.", rule.getErrorMessage());
    }
    
    @Test
    void assessmentRuleShouldAcceptLongDuration() {
        AppointmentRequest request = new AppointmentRequest("ASSESSMENT", 60, 1, "");
        BookingRuleStrategy rule = new AssessmentBookingRule();
        
        assertTrue(rule.isValid(request));
    }
    
    @Test
    void assessmentRuleShouldRejectShortDuration() {
        AppointmentRequest request = new AppointmentRequest("ASSESSMENT", 30, 1, "");
        BookingRuleStrategy rule = new AssessmentBookingRule();
        
        assertFalse(rule.isValid(request));
        assertEquals("Assessment appointment must be at least 60 minutes.", rule.getErrorMessage());
    }
    
    @Test
    void virtualRuleShouldAcceptNoLocation() {
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 30, 1, "");
        BookingRuleStrategy rule = new VirtualBookingRule();
        
        assertTrue(rule.isValid(request));
    }
    
    @Test
    void virtualRuleShouldRejectWithLocation() {
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 30, 1, "Room 101");
        BookingRuleStrategy rule = new VirtualBookingRule();
        
        assertFalse(rule.isValid(request));
        assertEquals("Virtual appointment should not have a physical location.", rule.getErrorMessage());
    }
    
    @Test
    void individualRuleShouldAcceptOneParticipant() {
        AppointmentRequest request = new AppointmentRequest("INDIVIDUAL", 30, 1, "");
        BookingRuleStrategy rule = new IndividualBookingRule();
        
        assertTrue(rule.isValid(request));
    }
    
    @Test
    void individualRuleShouldRejectMultipleParticipants() {
        AppointmentRequest request = new AppointmentRequest("INDIVIDUAL", 30, 5, "");
        BookingRuleStrategy rule = new IndividualBookingRule();
        
        assertFalse(rule.isValid(request));
        assertEquals("Individual appointment must have exactly 1 participant.", rule.getErrorMessage());
    }
    
    @Test
    void groupRuleShouldAcceptMultipleParticipants() {
        AppointmentRequest request = new AppointmentRequest("GROUP", 30, 5, "");
        BookingRuleStrategy rule = new GroupBookingRule();
        
        assertTrue(rule.isValid(request));
    }
    
    @Test
    void groupRuleShouldRejectOneParticipant() {
        AppointmentRequest request = new AppointmentRequest("GROUP", 30, 1, "");
        BookingRuleStrategy rule = new GroupBookingRule();
        
        assertFalse(rule.isValid(request));
        assertEquals("Group appointment must have more than 1 participant.", rule.getErrorMessage());
    }
}
