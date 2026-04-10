
public interface BookingRuleStrategy {
    boolean isValid(AppointmentRequest request);
    String getErrorMessage();
}