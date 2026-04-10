public class AssessmentBookingRule implements BookingRuleStrategy {

    @Override
    public boolean isValid(AppointmentRequest request) {
        return request.getDurationMinutes() >= 60;
    }

    @Override
    public String getErrorMessage() {
        return "Assessment appointment must be at least 60 minutes.";
    }
}