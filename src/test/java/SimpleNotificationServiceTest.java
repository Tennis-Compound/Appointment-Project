import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class SimpleNotificationServiceTest {

    private SimpleNotificationService service;

    @BeforeEach
    void setup() {
        service = new SimpleNotificationService();
    }

    @Test
    void serviceShouldBeCreatedSuccessfully() {
        assertNotNull(service);
    }

    @Test
    void sendBookingConfirmationValidInputs() {
        assertDoesNotThrow(() -> service.sendBookingConfirmation("u@e.com", "Confirmed."));
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

    @Test
    void sendCancellationNoticeValidInputs() {
        assertDoesNotThrow(() -> service.sendCancellationNotice("u@e.com", "Cancelled."));
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

    @Test
    void sendModificationNoticeValidInputs() {
        assertDoesNotThrow(() ->
            service.sendModificationNotice("u@e.com", "Old: Mon 10am", "New: Wed 2pm"));
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
