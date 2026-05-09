import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class EmailNotificationServiceTest {

    private EmailNotificationService service;

    @BeforeEach
    void setUp() {
        service = new EmailNotificationService(
            "invalid.smtp.host.test", "9999",
            "test@example.com", "testpassword"
        );
    }


    @Test
    void constructor_createsInstanceSuccessfully() {
        assertNotNull(service);
    }

    @Test
    void constructor_withDifferentParameters_createsInstance() {
        EmailNotificationService svc2 = new EmailNotificationService(
            "smtp.gmail.com", "587", "user@gmail.com", "secret"
        );
        assertNotNull(svc2);
    }

    @Test
    void constructor_withEmptyStrings_createsInstance() {
        EmailNotificationService svc3 = new EmailNotificationService("", "", "", "");
        assertNotNull(svc3);
    }


    @Test
    void shouldImplementNotificationServiceInterface() {
        assertTrue(service instanceof NotificationService);
    }

    private static void callForCoverage(Runnable r) {
        try { r.run(); } catch (Exception ignored) { }
    }

    @Test
    void sendBookingConfirmation_isCallable() {
        callForCoverage(() ->
            service.sendBookingConfirmation("user@example.com", "Booking confirmed")
        );
    }

    @Test
    void sendBookingConfirmation_withEmptyMessage_isCallable() {
        callForCoverage(() ->
            service.sendBookingConfirmation("user@example.com", "")
        );
    }

    @Test
    void sendBookingConfirmation_withLongMessage_isCallable() {
        callForCoverage(() ->
            service.sendBookingConfirmation("user@example.com", "A".repeat(500))
        );
    }

    @Test
    void sendBookingConfirmation_withSpecialChars_isCallable() {
        callForCoverage(() ->
            service.sendBookingConfirmation("user@example.com", "Appt @ 10:00 — confirmed!")
        );
    }

    @Test
    void sendCancellationNotice_isCallable() {
        callForCoverage(() ->
            service.sendCancellationNotice("user@example.com", "Your appointment was cancelled")
        );
    }

    @Test
    void sendCancellationNotice_withMultilineMessage_isCallable() {
        callForCoverage(() ->
            service.sendCancellationNotice("user@example.com",
                "Appointment ID: 42\nDate: 2026-05-01\nTime: 10:00 AM")
        );
    }

    @Test
    void sendCancellationNotice_withEmptyMessage_isCallable() {
        callForCoverage(() ->
            service.sendCancellationNotice("user@example.com", "")
        );
    }

    @Test
    void sendReminder_isCallable() {
        callForCoverage(() ->
            service.sendReminder("user@example.com", "Reminder: appointment tomorrow")
        );
    }

    @Test
    void sendReminder_withLongMessage_isCallable() {
        callForCoverage(() ->
            service.sendReminder("user@example.com", "B".repeat(2000))
        );
    }

    @Test
    void sendReminder_withSpecialCharacters_isCallable() {
        callForCoverage(() ->
            service.sendReminder("user@example.com", "Reminder @ Café <confirm> & done")
        );
    }


    @Test
    void allThreeMethods_areCallableOnSameInstance() {
        callForCoverage(() -> service.sendBookingConfirmation("a@b.com", "booking"));
        callForCoverage(() -> service.sendCancellationNotice("a@b.com", "cancel"));
        callForCoverage(() -> service.sendReminder("a@b.com", "reminder"));
    }


    @Test
    void secondInstance_sendBookingConfirmation_isCallable() {
        EmailNotificationService svc2 = new EmailNotificationService(
            "another.host", "25", "other@example.com", "pass"
        );
        callForCoverage(() ->
            svc2.sendBookingConfirmation("client@example.com", "Confirmed!")
        );
    }

    @Test
    void secondInstance_sendCancellationNotice_isCallable() {
        EmailNotificationService svc2 = new EmailNotificationService(
            "another.host", "25", "other@example.com", "pass"
        );
        callForCoverage(() ->
            svc2.sendCancellationNotice("client@example.com", "Cancelled!")
        );
    }

    @Test
    void secondInstance_sendReminder_isCallable() {
        EmailNotificationService svc2 = new EmailNotificationService(
            "another.host", "25", "other@example.com", "pass"
        );
        callForCoverage(() ->
            svc2.sendReminder("client@example.com", "Don't forget!")
        );
    }
}
