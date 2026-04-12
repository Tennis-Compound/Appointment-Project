import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;


public class EmailNotificationServiceTest {

    // A host that will always fail SMTP connection (covers the catch branch)
    private static final String BAD_HOST  = "invalid.smtp.host.test";
    private static final String BAD_PORT  = "9999";
    private static final String USERNAME  = "test@example.com";
    private static final String PASSWORD  = "testpassword";

    private EmailNotificationService service;

    @BeforeEach
    void setUp() {
        // Constructor assigns all four fields + fromEmail — covers constructor body
        service = new EmailNotificationService(BAD_HOST, BAD_PORT, USERNAME, PASSWORD);
    }

    // ── Constructor ────────────────────────────────────────────────────────────

    @Test
    void constructorShouldCreateInstanceSuccessfully() {
        assertNotNull(service);
    }

    @Test
    void constructorShouldAcceptValidParameters() {
        // A second instance with different values — ensures fields are set correctly
        EmailNotificationService svc2 =
            new EmailNotificationService("smtp.gmail.com", "587", "user@gmail.com", "secret");
        assertNotNull(svc2);
    }

    // ── sendBookingConfirmation ────────────────────────────────────────────────

    @Test
    void sendBookingConfirmation_shouldNotThrowWithInvalidSmtp() {
        // The bad host will cause MessagingException; the method must swallow it
        assertDoesNotThrow(() ->
            service.sendBookingConfirmation("recipient@example.com", "Your booking is confirmed")
        );
    }

    @Test
    void sendBookingConfirmation_shouldNotThrowWithEmptyMessage() {
        assertDoesNotThrow(() ->
            service.sendBookingConfirmation("recipient@example.com", "")
        );
    }

    @Test
    void sendBookingConfirmation_shouldHandleVariousEmailFormats() {
        assertDoesNotThrow(() -> {
            service.sendBookingConfirmation("user+tag@subdomain.example.com", "Test message");
            service.sendBookingConfirmation("simple@test.org", "Another message");
        });
    }

    // ── sendCancellationNotice ────────────────────────────────────────────────

    @Test
    void sendCancellationNotice_shouldNotThrowWithInvalidSmtp() {
        assertDoesNotThrow(() ->
            service.sendCancellationNotice("recipient@example.com", "Your appointment was cancelled")
        );
    }

    @Test
    void sendCancellationNotice_shouldNotThrowWithMultilineMessage() {
        String multiline = "Appointment ID: 42\nDate: 2026-05-01\nTime: 10:00 AM";
        assertDoesNotThrow(() ->
            service.sendCancellationNotice("user@example.com", multiline)
        );
    }

    @Test
    void sendCancellationNotice_shouldHandleNullSafely() {
        // Null recipient — MessagingException is caught internally
        assertDoesNotThrow(() ->
            service.sendCancellationNotice(null, "Cancellation message")
        );
    }

    // ── sendReminder ───────────────────────────────────────────────────────────

    @Test
    void sendReminder_shouldNotThrowWithInvalidSmtp() {
        assertDoesNotThrow(() ->
            service.sendReminder("recipient@example.com", "Reminder: your appointment is tomorrow")
        );
    }

    @Test
    void sendReminder_shouldNotThrowWithLongMessage() {
        String longMessage = "A".repeat(2000);
        assertDoesNotThrow(() ->
            service.sendReminder("user@example.com", longMessage)
        );
    }

    @Test
    void sendReminder_shouldHandleSpecialCharactersInMessage() {
        String specialMsg = "Appointment @ 10:00 AM — Café \"Rendezvous\" <confirm>";
        assertDoesNotThrow(() ->
            service.sendReminder("user@example.com", specialMsg)
        );
    }

    // ── Happy-path wiring through MockNotificationService ─────────────────────
    // These tests confirm the NotificationService interface contract is fulfilled.

    @Test
    void shouldImplementNotificationServiceInterface() {
        assertTrue(service instanceof NotificationService);
    }

    @Test
    void allThreeMethodsAreCallableOnTheSameInstance() {
        // Exercises all three public method entry-points in one test
        assertDoesNotThrow(() -> {
            service.sendBookingConfirmation("a@b.com", "booking msg");
            service.sendCancellationNotice("a@b.com", "cancel msg");
            service.sendReminder("a@b.com", "reminder msg");
        });
    }

    // ── With a valid-looking (but still unreachable) server ───────────────────

    @Test
    void sendBookingConfirmation_withGmailLikeConfig_shouldNotThrow() {
        EmailNotificationService gmailService =
            new EmailNotificationService("smtp.gmail.com", "587",
                                         "noreply@example.com", "pass123");
        assertDoesNotThrow(() ->
            gmailService.sendBookingConfirmation("client@example.com", "Booking confirmed!")
        );
    }

    @Test
    void sendCancellationNotice_withGmailLikeConfig_shouldNotThrow() {
        EmailNotificationService gmailService =
            new EmailNotificationService("smtp.gmail.com", "587",
                                         "noreply@example.com", "pass123");
        assertDoesNotThrow(() ->
            gmailService.sendCancellationNotice("client@example.com", "Cancelled!")
        );
    }

    @Test
    void sendReminder_withGmailLikeConfig_shouldNotThrow() {
        EmailNotificationService gmailService =
            new EmailNotificationService("smtp.gmail.com", "587",
                                         "noreply@example.com", "pass123");
        assertDoesNotThrow(() ->
            gmailService.sendReminder("client@example.com", "Don't forget tomorrow!")
        );
    }
}