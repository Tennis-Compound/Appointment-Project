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
    void shouldReturnCorrectRuleForVirtual() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("VIRTUAL");
        assertNotNull(rule);
        assertTrue(rule instanceof VirtualBookingRule);
    }

    @Test
    void shouldReturnCorrectRuleForInPerson() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("IN_PERSON");
        assertNotNull(rule);
        assertTrue(rule instanceof InPersonBookingRule);
    }

    @Test
    void shouldReturnCorrectRuleForIndividual() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("INDIVIDUAL");
        assertNotNull(rule);
        assertTrue(rule instanceof IndividualBookingRule);
    }

    @Test
    void shouldReturnCorrectRuleForGroup() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("GROUP");
        assertNotNull(rule);
        assertTrue(rule instanceof GroupBookingRule);
    }


    @Test
    void shouldReturnNullForInvalidType() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("INVALID_TYPE");
        assertNull(rule);
    }

    @Test
    void shouldReturnNullForEmptyString() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("");
        assertNull(rule);
    }

    @Test
    void shouldReturnNullForLowercaseType() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("urgent");
        assertNull(rule);
    }

   
    @Test
    void shouldThrowNullPointerExceptionForNullType() {
        assertThrows(NullPointerException.class,
                () -> BookingRuleFactory.getRule(null));
    }

    

    @Test
    void urgentRuleFromFactoryShouldValidateCorrectly() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("URGENT");
        assertTrue(rule.isValid(new AppointmentRequest("URGENT", 30, 1, "")));
        assertFalse(rule.isValid(new AppointmentRequest("URGENT", 60, 1, "")));
    }

    @Test
    void followUpRuleFromFactoryShouldValidateCorrectly() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("FOLLOW_UP");
        assertTrue(rule.isValid(new AppointmentRequest("FOLLOW_UP", 30, 1, "")));
        assertFalse(rule.isValid(new AppointmentRequest("FOLLOW_UP", 60, 1, "")));
    }

    @Test
    void assessmentRuleFromFactoryShouldValidateCorrectly() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("ASSESSMENT");
        assertTrue(rule.isValid(new AppointmentRequest("ASSESSMENT", 60, 1, "")));
        assertFalse(rule.isValid(new AppointmentRequest("ASSESSMENT", 30, 1, "")));
    }

    @Test
    void inPersonRuleFromFactoryShouldValidateCorrectly() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("IN_PERSON");
        assertTrue(rule.isValid(new AppointmentRequest("IN_PERSON", 30, 1, "Room 5")));
        assertFalse(rule.isValid(new AppointmentRequest("IN_PERSON", 30, 1, "")));
    }
}
