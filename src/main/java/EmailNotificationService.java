import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailNotificationService implements NotificationService {
    
    private final String smtpHost;
    private final String smtpPort;
    private final String smtpUsername;
    private final String smtpPassword;
    private final String fromEmail;
    
    public EmailNotificationService(String smtpHost, String smtpPort, 
                                   String smtpUsername, String smtpPassword) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpUsername = smtpUsername;
        this.smtpPassword = smtpPassword;
        this.fromEmail = smtpUsername;
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
    
    private void sendEmail(String toEmail, String subject, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", smtpHost);
        
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });
        
        try {
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(fromEmail));
            emailMessage.setRecipients(Message.RecipientType.TO, 
                                      InternetAddress.parse(toEmail));
            emailMessage.setSubject(subject);
            emailMessage.setText(message);
            
            Transport.send(emailMessage);
            System.out.println("Email sent successfully to " + toEmail);
            
        } catch (MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
