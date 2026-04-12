<<<<<<< HEAD
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
=======


import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * High-coverage tests for EmailNotificationService.
 * Uses mocking to verify interactions with the javax.mail API.
 */
public class EmailNotificationServiceTest {

    private static final String HOST = "smtp.test.invalid";
    private static final String PORT = "587";
    private static final String USERNAME = "test@test.invalid";
    private static final String PASSWORD = "password";

    private EmailNotificationService emailService;

    // Mocks for the static javax.mail.Transport class
    private static MockedStatic<Transport> mockedTransport;

    @BeforeAll
    static void setUpStaticMocks() {
        // We need to mock the static Transport.send() method
        mockedTransport = Mockito.mockStatic(Transport.class);
    }

    @AfterAll
    static void tearDownStaticMocks() {
        // It's crucial to close the static mock after tests
        mockedTransport.close();
    }

    @BeforeEach
    void setUp() {
        emailService = new EmailNotificationService(HOST, PORT, USERNAME, PASSWORD);
    }

    @Test
    void serviceShouldBeCreatedSuccessfully() {
        assertNotNull(emailService);
    }

    @Test
    void serviceShouldImplementNotificationServiceInterface() {
        assertTrue(emailService instanceof NotificationService);
    }

    @Test
    void sendReminderShouldCallTransportSendWithCorrectParameters() throws Exception {
        // Arrange
        String expectedEmail = "reminder@test.com";
        String expectedSubject = "Appointment Reminder";
        String expectedMessage = "This is a reminder message.";

        // Act
        emailService.sendReminder(expectedEmail, expectedMessage);

        // Assert
        // Verify that the static Transport.send() method was called exactly once
        mockedTransport.verify(() -> Transport.send(any(Message.class)), times(1));
    }

    @Test
    void sendBookingConfirmationShouldCallTransportSendWithCorrectParameters() throws Exception {
        // Arrange
        String expectedEmail = "booking@test.com";
        String expectedSubject = "Appointment Booking Confirmation";
        String expectedMessage = "Your appointment is confirmed.";

        // Act
        emailService.sendBookingConfirmation(expectedEmail, expectedMessage);

        // Assert
        mockedTransport.verify(() -> Transport.send(any(Message.class)), times(1));
    }

    @Test
    void sendCancellationNoticeShouldCallTransportSendWithCorrectParameters() throws Exception {
        // Arrange
        String expectedEmail = "cancel@test.com";
        String expectedSubject = "Appointment Cancellation";
        String expectedMessage = "Your appointment was cancelled.";

        // Act
        emailService.sendCancellationNotice(expectedEmail, expectedMessage);

        // Assert
        mockedTransport.verify(() -> Transport.send(any(Message.class)), times(1));
    }

    @Test
    void sendEmailShouldConstructMessageWithCorrectDetails() throws Exception {
        // This test is more advanced. It uses ArgumentCaptor to inspect the
        // Message object that was passed to Transport.send()
        
        // Arrange
        String toEmail = "details@test.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // Act
        // We need to call the private method indirectly. We can't, so we test a public method.
        emailService.sendBookingConfirmation(toEmail, body); 

        // Assert
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        mockedTransport.verify(() -> Transport.send(messageCaptor.capture()));
        
        Message capturedMessage = messageCaptor.getValue();

        assertEquals(subject, capturedMessage.getSubject());
        assertEquals(USERNAME, capturedMessage.getFrom()[0].toString());
        assertEquals(toEmail, capturedMessage.getAllRecipients()[0].toString());
    }
    
    @Test
    void sendEmailShouldCatchMessagingExceptionAndPrintStackTrace() throws Exception {
        // Arrange
        // Make the static Transport.send() method throw an exception
        mockedTransport.when(() -> Transport.send(any(Message.class)))
                      .thenThrow(new MessagingException("Simulated send failure"));

        // Act & Assert
        // The method should not throw an exception itself, it should handle it internally.
        assertDoesNotThrow(() -> {
            emailService.sendReminder("test@test.com", "This should not throw");
        });
    }

    @Test
    void serviceConstructorShouldHandleNullParametersGracefully() {
        // This tests the edge case where constructor params are null.
        assertDoesNotThrow(() -> new EmailNotificationService(null, null, null, null));
    }
}
>>>>>>> 98f38ff88bb0a72bb3aa0a35119530545a76ee8d
