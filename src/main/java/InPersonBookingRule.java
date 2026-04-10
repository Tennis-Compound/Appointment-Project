public class InPersonBookingRule implements BookingRuleStrategy {

    @Override
    public boolean isValid(AppointmentRequest request) {
        return request.getLocation() != null && !request.getLocation().trim().isEmpty();
    }

    @Override
    public String getErrorMessage() {
        return "In-person appointment requires a location.";
    }
}