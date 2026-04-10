public class GroupBookingRule implements BookingRuleStrategy {

    @Override
    public boolean isValid(AppointmentRequest request) {
        return request.getParticipantCount() > 1;
    }

    @Override
    public String getErrorMessage() {
        return "Group appointment must have more than 1 participant.";
    }
}