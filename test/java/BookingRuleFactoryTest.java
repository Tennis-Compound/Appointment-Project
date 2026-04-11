import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookingRuleFactoryTest {
    
    @Test
    void shouldReturnCorrectRuleForUrgent() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("URGENT");
        
        assertNotNull(rule);
        assertTrue(rule instanceof UrgentBookingRule);
    }
    
    @Test
    void shouldReturnCorrectRuleForFollowUp() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("FOLLOW_UP");
        
        assertNotNull(rule);
        assertTrue(rule instanceof FollowUpBookingRule);
    }
    
    @Test
    void shouldReturnCorrectRuleForAssessment() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("ASSESSMENT");
        
        assertNotNull(rule);
        assertTrue(rule instanceof AssessmentBookingRule);
    }
    
    @Test
    void shouldReturnNullForInvalidType() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("INVALID_TYPE");
        
        assertNull(rule);
    }
    
    @Test
    void shouldReturnNullForNullType() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule(null);
        
        assertNull(rule);
    }
}
