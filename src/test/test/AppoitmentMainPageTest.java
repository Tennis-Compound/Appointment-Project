package appointmentsystem;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Tests for the Appoitment_Main_Page class by testing its public behavior.
 * We use dependency injection to provide mock services, making the test fast and reliable.
 */
class AppoitmentMainPageTest {

    private Appoitment_Main_Page app;
    private MockNotificationService mockNotificationService;
    private UserService mockUserService;

    @BeforeEach
    void setUp() {
        // Create mock dependencies
        mockNotificationService = new MockNotificationService();
        mockUserService = mock(UserService.class); // Using Mockito mock

        // We need to create the app with a constructor that accepts dependencies
        // For now, we will create an instance and manually set the dependencies if possible.
        // If your refactored class doesn't allow this, you need to add a constructor.
        
        // A better approach is to make NotificationManager injectable, but for now let's test what we have.
        // This test will focus on parts that don't require a full constructor setup.

        // For this test to be meaningful, Appoitment_Main_Page would ideally
        // have a constructor like: Appoitment_Main_Page(UserService, NotificationManager)
        // Since it doesn't yet, we will create an instance and test what we can.
        app = new Appoitment_Main_Page();
    }

    @Test
    void testFactoryReturnsRulesForAllTypes() {
        // This tests your business logic, which is good.
        assertNotNull(BookingRuleFactory.getRule("URGENT"));
        assertNotNull(BookingRuleFactory.getRule("FOLLOW_UP"));
        assertNotNull(BookingRuleFactory.getRule("ASSESSMENT"));
        assertNotNull(BookingRuleFactory.getRule("VIRTUAL"));
        assertNotNull(BookingRuleFactory.getRule("IN_PERSON"));
        assertNotNull(BookingRuleFactory.getRule("INDIVIDUAL"));
        assertNotNull(BookingRuleFactory.getRule("GROUP"));
    }
    
    @Test
    void factoryReturnsNullForInvalidType() {
        assertNull(BookingRuleFactory.getRule("INVALID_TYPE"));
    }

    // Tests for specific business logic rules
    @Test
    void urgentBookingRuleShouldRejectLongDuration() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("URGENT");
        AppointmentRequest bad = new AppointmentRequest("URGENT", 60, 1, "");
        assertFalse(rule.isValid(bad));
        assertEquals("Urgent appointment must be 30 minutes or less.", rule.getErrorMessage());
    }

    @Test
    void assessmentBookingRuleShouldRejectShortDuration() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("ASSESSMENT");
        AppointmentRequest bad = new AppointmentRequest("ASSESSMENT", 30, 1, "");
        assertFalse(rule.isValid(bad));
        assertEquals("Assessment appointment must be at least 60 minutes.", rule.getErrorMessage());
    }

    @Test
    void inPersonBookingRuleShouldRejectEmptyLocation() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("IN_PERSON");
        AppointmentRequest bad = new AppointmentRequest("IN_PERSON", 30, 1, "");
        assertFalse(rule.isValid(bad));
        assertEquals("In-person appointment requires a location.", rule.getErrorMessage());
    }

    @Test
    void virtualBookingRuleShouldRejectPhysicalLocation() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("VIRTUAL");
        AppointmentRequest bad = new AppointmentRequest("VIRTUAL", 30, 1, "Room 5");
        assertFalse(rule.isValid(bad));
        assertEquals("Virtual appointment should not have a physical location.", rule.getErrorMessage());
    }

    @Test
    void groupBookingRuleShouldRejectSingleParticipant() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("GROUP");
        AppointmentRequest bad = new AppointmentRequest("GROUP", 30, 1, "");
        assertFalse(rule.isValid(bad));
        assertEquals("Group appointment must have more than 1 participant.", rule.getErrorMessage());
    }

    @Test
    void individualBookingRuleShouldRejectMultipleParticipants() {
        BookingRuleStrategy rule = BookingRuleFactory.getRule("INDIVIDUAL");
        AppointmentRequest bad = new AppointmentRequest("INDIVIDUAL", 30, 3, "");
        assertFalse(rule.isValid(bad));
        assertEquals("Individual appointment must have exactly 1 participant.", rule.getErrorMessage());
    }
}
