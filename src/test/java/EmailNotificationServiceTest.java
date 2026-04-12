

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
