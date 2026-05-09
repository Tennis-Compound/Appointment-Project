import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Unit tests for {@link SimpleNotificationService}.
 *
 * Tests the REAL SimpleNotificationService class (not an inner stub).
 * Covers every method and branch to satisfy the >= 80% SonarCloud coverage gate.
 */
public class SimpleNotificationServiceTest {

    // The REAL production class — NOT an inner stub
    private SimpleNotificationService service;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setup() {
        service = new SimpleNotificationService();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void teardown() {
        System.setOut(originalOut);
    }

    // ── sendReminder ──────────────────────────────────────────────────────────

    @Test
    void sendReminderShouldNotThrow() {
        assertDoesNotThrow(() -> service.sendReminder("u@e.com", "Don't forget!"));
    }

    @Test
    void sendReminderShouldPrintReminderKeyword() {
        service.sendReminder("u@e.com", "Don't forget!");
        assertTrue(outContent.toString().contains("REMINDER"));
    }

    @Test
    void sendReminderShouldPrintEmail() {
        service.sendReminder("u@e.com", "msg");
        assertTrue(outContent.toString().contains("u@e.com"));
    }

    @Test
    void sendReminderNullEmail() {
        assertDoesNotThrow(() -> service.sendReminder(null, "msg"));
    }

    @Test
    void sendReminderNullMessage() {
        assertDoesNotThrow(() -> service.sendReminder("u@e.com", null));
    }

    @Test
    void sendReminderBothNull() {
        assertDoesNotThrow(() -> service.sendReminder(null, null));
    }

    // ── sendBookingConfirmation ───────────────────────────────────────────────

    @Test
    void sendBookingConfirmationShouldNotThrow() {
        assertDoesNotThrow(() -> service.sendBookingConfirmation("u@e.com", "Confirmed."));
    }

    @Test
    void sendBookingConfirmationShouldPrintKeyword() {
        service.sendBookingConfirmation("u@e.com", "Confirmed.");
        assertTrue(outContent.toString().contains("BOOKING CONFIRMATION"));
    }

    @Test
    void sendBookingConfirmationShouldPrintEmail() {
        service.sendBookingConfirmation("u@e.com", "Confirmed.");
        assertTrue(outContent.toString().contains("u@e.com"));
    }

    @Test
    void sendBookingConfirmationEmptyMessage() {
        assertDoesNotThrow(() -> service.sendBookingConfirmation("u@e.com", ""));
    }

    @Test
    void sendBookingConfirmationNullEmail() {
        assertDoesNotThrow(() -> service.sendBookingConfirmation(null, "msg"));
    }

    @Test
    void sendBookingConfirmationNullMessage() {
        assertDoesNotThrow(() -> service.sendBookingConfirmation("u@e.com", null));
    }

    @Test
    void sendBookingConfirmationBothNull() {
        assertDoesNotThrow(() -> service.sendBookingConfirmation(null, null));
    }

    // ── sendCancellationNotice ────────────────────────────────────────────────

    @Test
    void sendCancellationNoticeShouldNotThrow() {
        assertDoesNotThrow(() -> service.sendCancellationNotice("u@e.com", "Cancelled."));
    }

    @Test
    void sendCancellationNoticeShouldPrintKeyword() {
        service.sendCancellationNotice("u@e.com", "Cancelled.");
        assertTrue(outContent.toString().contains("CANCELLATION NOTICE"));
    }

    @Test
    void sendCancellationNoticeShouldPrintEmail() {
        service.sendCancellationNotice("u@e.com", "Cancelled.");
        assertTrue(outContent.toString().contains("u@e.com"));
    }

    @Test
    void sendCancellationNoticeEmptyMessage() {
        assertDoesNotThrow(() -> service.sendCancellationNotice("u@e.com", ""));
    }

    @Test
    void sendCancellationNoticeNullEmail() {
        assertDoesNotThrow(() -> service.sendCancellationNotice(null, "msg"));
    }

    @Test
    void sendCancellationNoticeNullMessage() {
        assertDoesNotThrow(() -> service.sendCancellationNotice("u@e.com", null));
    }

    @Test
    void sendCancellationNoticeBothNull() {
        assertDoesNotThrow(() -> service.sendCancellationNotice(null, null));
    }

    // ── sendModificationNotice ────────────────────────────────────────────────

    @Test
    void sendModificationNoticeShouldNotThrow() {
        assertDoesNotThrow(() ->
                service.sendModificationNotice("u@e.com", "Old: Mon 10am", "New: Wed 2pm"));
    }

    @Test
    void sendModificationNoticeShouldPrintKeyword() {
        service.sendModificationNotice("u@e.com", "Old: Mon 10am", "New: Wed 2pm");
        assertTrue(outContent.toString().contains("MODIFICATION NOTICE"));
    }

    @Test
    void sendModificationNoticeShouldPrintOldAndNewDetails() {
        service.sendModificationNotice("u@e.com", "Old: Mon 10am", "New: Wed 2pm");
        String output = outContent.toString();
        assertTrue(output.contains("Old: Mon 10am"));
        assertTrue(output.contains("New: Wed 2pm"));
    }

    @Test
    void sendModificationNoticeEmptyDetails() {
        assertDoesNotThrow(() -> service.sendModificationNotice("u@e.com", "", ""));
    }

    @Test
    void sendModificationNoticeNullEmail() {
        assertDoesNotThrow(() -> service.sendModificationNotice(null, "old", "new"));
    }

    @Test
    void sendModificationNoticeNullOld() {
        assertDoesNotThrow(() -> service.sendModificationNotice("u@e.com", null, "new"));
    }

    @Test
    void sendModificationNoticeNullNew() {
        assertDoesNotThrow(() -> service.sendModificationNotice("u@e.com", "old", null));
    }

    @Test
    void sendModificationNoticeAllNull() {
        assertDoesNotThrow(() -> service.sendModificationNotice(null, null, null));
    }
}
