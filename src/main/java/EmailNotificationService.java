

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * A concrete implementation of the NotificationService that sends emails
 * via SMTP using the JavaMail API.
 */
public class EmailNotificationService implements NotificationService {
    
    private final String smtpHost;
    private final String smtpPort;
    private final String smtpUsername;
    private final String smtpPassword;
    private final String fromEmail;

    /**
     * Constructor to configure the email service with SMTP credentials.
     *
     * @param smtpHost     The address of the SMTP server (e.g., smtp.gmail.com).
     * @param smtpPort     The port of the SMTP server (e.g., 587 for TLS).
     * @param smtpUsername The username for authentication (usually your email).
     * @param smtpPassword The password or app-password for authentication.
     */
    public EmailNotificationService(String smtpHost, String smtpPort, 
                                   String smtpUsername, String smtpPassword) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpUsername = smtpUsername;
        this.smtpPassword = smtpPassword;
        this.fromEmail = smtpUsername; // The 'from' address is typically the username
    }
    
    @Override
    public void sendReminder(String userEmail, String message) {
        sendEmail(userEmail, "Appointment Reminder", message);
    }
    
    @Override
    public void sendBookingConfirmation(String userEmail, String message) {
        sendEmail(userEmail, "Appointment Booking Confirmation", message);
    }
    
    @Override
    public void sendCancellationNotice(String userEmail, String message) {
        sendEmail(userEmail, "Appointment Cancellation", message);
    }
    
    /**
     * The core method that configures and sends an email.
     *
     * @param toEmail The recipient's email address.
     * @param subject The subject line for the email.
     * @param message The body of the email.
     */
    private void sendEmail(String toEmail, String subject, String message) {
        // 1. Set up mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.auth", "true");         // Enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // Enable TLS
        props.put("mail.smtp.ssl.trust", smtpHost); // Trust the host

        // 2. Create a mail session with an authenticator
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });

        try {
            // 3. Create the email message
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(fromEmail));
            emailMessage.setRecipients(Message.RecipientType.TO, 
                                      InternetAddress.parse(toEmail));
            emailMessage.setSubject(subject);
            emailMessage.setText(message);

            // 4. Send the message
            Transport.send(emailMessage);
            
            // Optional: Print a confirmation to the console for debugging
            System.out.println("Email sent successfully to " + toEmail);

        } catch (MessagingException e) {
            // It's important to handle this exception so a failed email
            // doesn't crash the whole application.
            System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
            // e.printStackTrace(); // Uncomment for more detailed debugging
        }
    }
}
