import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailNotificationServiceTest {

    private static final String HOST = "smtp.test.invalid";
    private static final String PORT = "587";
    private static final String USERNAME = "test@test.invalid";
    private static final String PASSWORD = "password";

    private EmailNotificationService emailService;

    private static MockedStatic<Transport> mockedTransport;

    @BeforeAll
    static void setUpStaticMocks() {
        mockedTransport = Mockito.mockStatic(Transport.class);
    }

    @AfterAll
    static void tearDownStaticMocks() {
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
    void sendReminderShouldCallTransportSend() throws Exception {
        emailService.sendReminder("reminder@test.com", "This is a reminder message.");
        mockedTransport.verify(() -> Transport.send(any(Message.class)), times(1));
    }

    @Test
    void sendBookingConfirmationShouldCallTransportSend() throws Exception {
        emailService.sendBookingConfirmation("booking@test.com", "Your appointment is confirmed.");
        mockedTransport.verify(() -> Transport.send(any(Message.class)), times(1));
    }

    @Test
    void sendCancellationNoticeShouldCallTransportSend() throws Exception {
        emailService.sendCancellationNotice("cancel@test.com", "Your appointment was cancelled.");
        mockedTransport.verify(() -> Transport.send(any(Message.class)), times(1));
    }

    @Test
    void sendEmailShouldConstructMessageWithCorrectDetails() throws Exception {
        String toEmail = "details@test.com";
        String body = "Test Body";

        emailService.sendBookingConfirmation(toEmail, body);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        mockedTransport.verify(() -> Transport.send(messageCaptor.capture()));

        Message capturedMessage = messageCaptor.getValue();
        assertEquals(USERNAME, capturedMessage.getFrom()[0].toString());
        assertEquals(toEmail, capturedMessage.getAllRecipients()[0].toString());
    }

    @Test
    void sendEmailShouldCatchMessagingExceptionAndNotThrow() {
        mockedTransport.when(() -> Transport.send(any(Message.class)))
                       .thenThrow(new MessagingException("Simulated failure"));

        assertDoesNotThrow(() ->
            emailService.sendReminder("test@test.com", "This should not throw")
        );
    }

    @Test
    void constructorShouldHandleNullParametersGracefully() {
        assertDoesNotThrow(() ->
            new EmailNotificationService(null, null, null, null)
        );
    }
}
