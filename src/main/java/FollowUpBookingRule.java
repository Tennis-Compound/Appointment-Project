public class FollowUpBookingRule implements BookingRuleStrategy {

    @Override
    public boolean isValid(AppointmentRequest request) {
        return request.getDurationMinutes() <= 30;
    }

    @Override
    public String getErrorMessage() {
        return "Follow-up appointment must be 30 minutes or less.";
    }
}