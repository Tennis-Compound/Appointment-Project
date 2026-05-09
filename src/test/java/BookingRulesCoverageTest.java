import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Booking Rules Strategy Coverage")
class BookingRulesCoverageTest {

    @Test
    @DisplayName("FollowUp: Covers 30 min limit and error message")
    void testFollowUpRule() {
        FollowUpBookingRule rule = new FollowUpBookingRule();
        assertTrue(rule.isValid(new AppointmentRequest("FOLLOW_UP", 30, 1, "Room 1")));
        assertFalse(rule.isValid(new AppointmentRequest("FOLLOW_UP", 31, 1, "Room 1")));
        assertEquals("Follow-up appointment must be 30 minutes or less.", rule.getErrorMessage());
    }

    @Test
    @DisplayName("Urgent: Covers 30 min limit and error message")
    void testUrgentRule() {
        UrgentBookingRule rule = new UrgentBookingRule();
        assertTrue(rule.isValid(new AppointmentRequest("URGENT", 30, 1, "Room 1")));
        assertFalse(rule.isValid(new AppointmentRequest("URGENT", 31, 1, "Room 1")));
        assertEquals("Urgent appointment must be 30 minutes or less.", rule.getErrorMessage());
    }

    @Test
    @DisplayName("Group: Covers participant count > 1")
    void testGroupRule() {
        GroupBookingRule rule = new GroupBookingRule();
        assertTrue(rule.isValid(new AppointmentRequest("GROUP", 60, 2, "Room 1")));
        assertFalse(rule.isValid(new AppointmentRequest("GROUP", 60, 1, "Room 1")));
        assertEquals("Group appointment must have more than 1 participant.", rule.getErrorMessage());
    }

    @Test
    @DisplayName("Individual: Covers participant count == 1")
    void testIndividualRule() {
        IndividualBookingRule rule = new IndividualBookingRule();
        assertTrue(rule.isValid(new AppointmentRequest("INDIVIDUAL", 30, 1, "Room 1")));
        assertFalse(rule.isValid(new AppointmentRequest("INDIVIDUAL", 30, 2, "Room 1")));
        assertEquals("Individual appointment must have exactly 1 participant.", rule.getErrorMessage());
    }

    @Nested
    @DisplayName("InPerson Strategy Branches")
    class InPersonTests {
        private final InPersonBookingRule rule = new InPersonBookingRule();

        @Test
        void shouldFailForNullLocation() {
            assertFalse(rule.isValid(new AppointmentRequest("IN_PERSON", 30, 1, null)));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        void shouldFailForEmptyOrBlankLocation(String loc) {
            assertFalse(rule.isValid(new AppointmentRequest("IN_PERSON", 30, 1, loc)));
        }

        @Test
        void shouldPassForValidLocation() {
            assertTrue(rule.isValid(new AppointmentRequest("IN_PERSON", 30, 1, "Main Clinic")));
        }
    }

    @Nested
    @DisplayName("Virtual Strategy Branches")
    class VirtualTests {
        private final VirtualBookingRule rule = new VirtualBookingRule();

        @Test
        void shouldPassForNullLocation() {
            assertTrue(rule.isValid(new AppointmentRequest("VIRTUAL", 30, 1, null)));
        }

        @Test
        void shouldPassForEmptyLocation() {
            assertTrue(rule.isValid(new AppointmentRequest("VIRTUAL", 30, 1, "")));
        }

        @Test
        void shouldFailIfPhysicalLocationIsProvided() {
            assertFalse(rule.isValid(new AppointmentRequest("VIRTUAL", 30, 1, "Office 101")));
        }
    }
}
