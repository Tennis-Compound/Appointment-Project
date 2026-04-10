public class VirtualBookingRule implements BookingRuleStrategy {

    @Override
    public boolean isValid(AppointmentRequest request) {
        return request.getLocation() == null || request.getLocation().trim().isEmpty();
    }

    @Override
    public String getErrorMessage() {
        return "Virtual appointment should not have a physical location.";
    }
}