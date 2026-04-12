
public class IndividualBookingRule implements BookingRuleStrategy {

    @Override
    public boolean isValid(AppointmentRequest request) {
        return request.getParticipantCount() == 1;
    }

    @Override
    public String getErrorMessage() {
        return "Individual appointment must have exactly 1 participant.";
    }
}