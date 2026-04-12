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
    void urgentRuleShouldAcceptMinimumDuration() {
        AppointmentRequest request = new AppointmentRequest("URGENT", 1, 1, "");
        assertTrue(new UrgentBookingRule().isValid(request));
    }

    @Test
    void urgentRuleShouldRejectLongDuration() {
        AppointmentRequest request = new AppointmentRequest("URGENT", 60, 1, "");
        BookingRuleStrategy rule = new UrgentBookingRule();
        assertFalse(rule.isValid(request));
        assertEquals("Urgent appointment must be 30 minutes or less.", rule.getErrorMessage());
    }

    @Test
    void urgentRuleShouldRejectJustOverLimit() {
        assertFalse(new UrgentBookingRule().isValid(
                new AppointmentRequest("URGENT", 31, 1, "")));
    }


    @Test
    void followUpRuleShouldAcceptShortDuration() {
        AppointmentRequest request = new AppointmentRequest("FOLLOW_UP", 30, 1, "");
        BookingRuleStrategy rule = new FollowUpBookingRule();
        assertTrue(rule.isValid(request));
    }

    @Test
    void followUpRuleShouldAcceptMinimumDuration() {
        assertTrue(new FollowUpBookingRule().isValid(
                new AppointmentRequest("FOLLOW_UP", 1, 1, "")));
    }

    @Test
    void followUpRuleShouldRejectLongDuration() {
        AppointmentRequest request = new AppointmentRequest("FOLLOW_UP", 60, 1, "");
        BookingRuleStrategy rule = new FollowUpBookingRule();
        assertFalse(rule.isValid(request));
        assertEquals("Follow-up appointment must be 30 minutes or less.", rule.getErrorMessage());
    }

    @Test
    void followUpRuleShouldRejectJustOverLimit() {
        assertFalse(new FollowUpBookingRule().isValid(
                new AppointmentRequest("FOLLOW_UP", 31, 1, "")));
    }


    @Test
    void assessmentRuleShouldAcceptLongDuration() {
        AppointmentRequest request = new AppointmentRequest("ASSESSMENT", 60, 1, "");
        BookingRuleStrategy rule = new AssessmentBookingRule();
        assertTrue(rule.isValid(request));
    }

    @Test
    void assessmentRuleShouldAcceptMoreThanMinimum() {
        assertTrue(new AssessmentBookingRule().isValid(
                new AppointmentRequest("ASSESSMENT", 90, 1, "")));
    }

    @Test
    void assessmentRuleShouldRejectShortDuration() {
        AppointmentRequest request = new AppointmentRequest("ASSESSMENT", 30, 1, "");
        BookingRuleStrategy rule = new AssessmentBookingRule();
        assertFalse(rule.isValid(request));
        assertEquals("Assessment appointment must be at least 60 minutes.", rule.getErrorMessage());
    }

    @Test
    void assessmentRuleShouldRejectJustUnderLimit() {
        assertFalse(new AssessmentBookingRule().isValid(
                new AppointmentRequest("ASSESSMENT", 59, 1, "")));
    }


    @Test
    void virtualRuleShouldAcceptEmptyLocation() {
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 30, 1, "");
        BookingRuleStrategy rule = new VirtualBookingRule();
        assertTrue(rule.isValid(request));
    }

    @Test
    void virtualRuleShouldAcceptNullLocation() {
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 30, 1, null);
        assertTrue(new VirtualBookingRule().isValid(request));
    }

    @Test
    void virtualRuleShouldAcceptWhitespaceOnlyLocation() {
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 30, 1, "   ");
        assertTrue(new VirtualBookingRule().isValid(request));
    }

    @Test
    void virtualRuleShouldRejectWithLocation() {
        AppointmentRequest request = new AppointmentRequest("VIRTUAL", 30, 1, "Room 101");
        BookingRuleStrategy rule = new VirtualBookingRule();
        assertFalse(rule.isValid(request));
        assertEquals("Virtual appointment should not have a physical location.", rule.getErrorMessage());
    }


    @Test
    void inPersonRuleShouldAcceptWithLocation() {
        AppointmentRequest request = new AppointmentRequest("IN_PERSON", 30, 1, "Room 101");
        BookingRuleStrategy rule = new InPersonBookingRule();
        assertTrue(rule.isValid(request));
    }

    @Test
    void inPersonRuleShouldAcceptAnyNonEmptyLocation() {
        assertTrue(new InPersonBookingRule().isValid(
                new AppointmentRequest("IN_PERSON", 60, 3, "Building A Floor 2")));
    }

    @Test
    void inPersonRuleShouldRejectEmptyLocation() {
        AppointmentRequest request = new AppointmentRequest("IN_PERSON", 30, 1, "");
        BookingRuleStrategy rule = new InPersonBookingRule();
        assertFalse(rule.isValid(request));
        assertEquals("In-person appointment requires a location.", rule.getErrorMessage());
    }

    @Test
    void inPersonRuleShouldRejectNullLocation() {
        AppointmentRequest request = new AppointmentRequest("IN_PERSON", 30, 1, null);
        assertFalse(new InPersonBookingRule().isValid(request));
    }

    @Test
    void inPersonRuleShouldRejectWhitespaceOnlyLocation() {
        AppointmentRequest request = new AppointmentRequest("IN_PERSON", 30, 1, "   ");
        assertFalse(new InPersonBookingRule().isValid(request));
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
    void individualRuleShouldRejectZeroParticipants() {
        assertFalse(new IndividualBookingRule().isValid(
                new AppointmentRequest("INDIVIDUAL", 30, 0, "")));
    }


    @Test
    void groupRuleShouldAcceptMultipleParticipants() {
        AppointmentRequest request = new AppointmentRequest("GROUP", 30, 5, "");
        BookingRuleStrategy rule = new GroupBookingRule();
        assertTrue(rule.isValid(request));
    }

    @Test
    void groupRuleShouldAcceptTwoParticipants() {
        assertTrue(new GroupBookingRule().isValid(
                new AppointmentRequest("GROUP", 30, 2, "")));
    }

    @Test
    void groupRuleShouldRejectOneParticipant() {
        AppointmentRequest request = new AppointmentRequest("GROUP", 30, 1, "");
        BookingRuleStrategy rule = new GroupBookingRule();
        assertFalse(rule.isValid(request));
        assertEquals("Group appointment must have more than 1 participant.", rule.getErrorMessage());
    }

    @Test
    void groupRuleShouldRejectZeroParticipants() {
        assertFalse(new GroupBookingRule().isValid(
                new AppointmentRequest("GROUP", 30, 0, "")));
    }
}
