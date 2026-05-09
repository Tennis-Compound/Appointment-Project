import org.junit.jupiter.api.Test;
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
        assertThrows(NullPointerException.class, () -> BookingRuleFactory.getRule(null));
    }

    @Test
    void shouldReturnDifferentObjectsOnDifferentCalls() {
        BookingRuleStrategy rule1 = BookingRuleFactory.getRule("URGENT");
        BookingRuleStrategy rule2 = BookingRuleFactory.getRule("URGENT");

        assertNotNull(rule1);
        assertNotNull(rule2);
        assertNotSame(rule1, rule2);
    }

    @Test
    void urgentRuleShouldAccept30Minutes() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("URGENT");
        AppointmentRequest request = new AppointmentRequest("URGENT", 30, 1, "");
        assertTrue(rule.isValid(request));
    }

    @Test
    void urgentRuleShouldAcceptLessThan30Minutes() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("URGENT");
        AppointmentRequest request = new AppointmentRequest("URGENT", 15, 1, "");
        assertTrue(rule.isValid(request));
    }

    @Test
    void urgentRuleShouldRejectMoreThan30Minutes() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("URGENT");
        AppointmentRequest request = new AppointmentRequest("URGENT", 31, 1, "");
        assertFalse(rule.isValid(request));
    }

    @Test
    void urgentRuleShouldHaveErrorMessage() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("URGENT");
        assertNotNull(rule.getErrorMessage());
        assertFalse(rule.getErrorMessage().isEmpty());
    }

    @Test
    void followUpRuleShouldAccept30Minutes() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("FOLLOW_UP");
        AppointmentRequest request = new AppointmentRequest("FOLLOW_UP", 30, 1, "");
        assertTrue(rule.isValid(request));
    }

    @Test
    void followUpRuleShouldAccept10Minutes() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("FOLLOW_UP");
        AppointmentRequest request = new AppointmentRequest("FOLLOW_UP", 10, 1, "");
        assertTrue(rule.isValid(request));
    }

    @Test
    void followUpRuleShouldRejectMoreThan30Minutes() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("FOLLOW_UP");
        AppointmentRequest request = new AppointmentRequest("FOLLOW_UP", 45, 1, "");
        assertFalse(rule.isValid(request));
    }

    @Test
    void followUpRuleShouldHaveErrorMessage() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("FOLLOW_UP");
        assertNotNull(rule.getErrorMessage());
        assertFalse(rule.getErrorMessage().isEmpty());
    }

    @Test
    void assessmentRuleShouldAccept60Minutes() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("ASSESSMENT");
        AppointmentRequest request = new AppointmentRequest("ASSESSMENT", 60, 1, "");
        assertTrue(rule.isValid(request));
    }

    @Test
    void assessmentRuleShouldAcceptMoreThan60Minutes() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("ASSESSMENT");
        AppointmentRequest request = new AppointmentRequest("ASSESSMENT", 90, 1, "");
        assertTrue(rule.isValid(request));
    }

    @Test
    void assessmentRuleShouldRejectLessThan60Minutes() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("ASSESSMENT");
        AppointmentRequest request = new AppointmentRequest("ASSESSMENT", 59, 1, "");
        assertFalse(rule.isValid(request));
    }

    @Test
    void assessmentRuleShouldHaveErrorMessage() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("ASSESSMENT");
        assertNotNull(rule.getErrorMessage());
        assertFalse(rule.getErrorMessage().isEmpty());
    }

    @Test
    void virtualRuleShouldAcceptEmptyLocation() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("VIRTUAL");
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 30, 1, "");
        assertTrue(rule.isValid(request));
    }

    @Test
    void virtualRuleShouldAcceptNullLocationIfSupported() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("VIRTUAL");
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 30, 1, null);
        assertTrue(rule.isValid(request));
    }

    @Test
    void virtualRuleShouldRejectNonEmptyLocation() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("VIRTUAL");
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 30, 1, "Room 7");
        assertFalse(rule.isValid(request));
    }

    @Test
    void virtualRuleShouldHaveErrorMessage() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("VIRTUAL");
        assertNotNull(rule.getErrorMessage());
        assertFalse(rule.getErrorMessage().isEmpty());
    }

    @Test
    void inPersonRuleShouldAcceptValidLocation() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("IN_PERSON");
        AppointmentRequest request = new AppointmentRequest("IN_PERSON", 30, 1, "Room 5");
        assertTrue(rule.isValid(request));
    }

    @Test
    void inPersonRuleShouldRejectEmptyLocation() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("IN_PERSON");
        AppointmentRequest request = new AppointmentRequest("IN_PERSON", 30, 1, "");
        assertFalse(rule.isValid(request));
    }

    @Test
    void inPersonRuleShouldRejectNullLocationIfRuleRequiresLocation() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("IN_PERSON");
        AppointmentRequest request = new AppointmentRequest("IN_PERSON", 30, 1, null);
        assertFalse(rule.isValid(request));
    }

    @Test
    void inPersonRuleShouldHaveErrorMessage() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("IN_PERSON");
        assertNotNull(rule.getErrorMessage());
        assertFalse(rule.getErrorMessage().isEmpty());
    }

    @Test
    void individualRuleShouldAcceptOneParticipant() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("INDIVIDUAL");
        AppointmentRequest request = new AppointmentRequest("INDIVIDUAL", 30, 1, "");
        assertTrue(rule.isValid(request));
    }

    @Test
    void individualRuleShouldRejectZeroParticipants() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("INDIVIDUAL");
        AppointmentRequest request = new AppointmentRequest("INDIVIDUAL", 30, 0, "");
        assertFalse(rule.isValid(request));
    }

    @Test
    void individualRuleShouldRejectMoreThanOneParticipant() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("INDIVIDUAL");
        AppointmentRequest request = new AppointmentRequest("INDIVIDUAL", 30, 2, "");
        assertFalse(rule.isValid(request));
    }

    @Test
    void individualRuleShouldHaveErrorMessage() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("INDIVIDUAL");
        assertNotNull(rule.getErrorMessage());
        assertFalse(rule.getErrorMessage().isEmpty());
    }

    @Test
    void groupRuleShouldAcceptMoreThanOneParticipant() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("GROUP");
        AppointmentRequest request = new AppointmentRequest("GROUP", 30, 2, "");
        assertTrue(rule.isValid(request));
    }

    @Test
    void groupRuleShouldAcceptManyParticipants() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("GROUP");
        AppointmentRequest request = new AppointmentRequest("GROUP", 30, 10, "");
        assertTrue(rule.isValid(request));
    }

    @Test
    void groupRuleShouldRejectOneParticipant() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("GROUP");
        AppointmentRequest request = new AppointmentRequest("GROUP", 30, 1, "");
        assertFalse(rule.isValid(request));
    }

    @Test
    void groupRuleShouldRejectZeroParticipants() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("GROUP");
        AppointmentRequest request = new AppointmentRequest("GROUP", 30, 0, "");
        assertFalse(rule.isValid(request));
    }

    @Test
    void groupRuleShouldHaveErrorMessage() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("GROUP");
        assertNotNull(rule.getErrorMessage());
        assertFalse(rule.getErrorMessage().isEmpty());
    }

    @Test
    void appointmentRequestShouldStoreValuesCorrectly() {
        AppointmentRequest request = new AppointmentRequest("GROUP", 45, 3, "Lab 1");

        assertEquals("GROUP", request.getAppointmentType());
        assertEquals(45, request.getDurationMinutes());
        assertEquals(3, request.getParticipantCount());
        assertEquals("Lab 1", request.getLocation());
    }

    @Test
    void appointmentRequestShouldAllowEmptyLocation() {
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 20, 1, "");

        assertEquals("VIRTUAL", request.getAppointmentType());
        assertEquals(20, request.getDurationMinutes());
        assertEquals(1, request.getParticipantCount());
        assertEquals("", request.getLocation());
    }

    @Test
    void appointmentRequestShouldAllowNullLocation() {
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 20, 1, null);

        assertEquals("VIRTUAL", request.getAppointmentType());
        assertEquals(20, request.getDurationMinutes());
        assertEquals(1, request.getParticipantCount());
        assertNull(request.getLocation());
    }
}
