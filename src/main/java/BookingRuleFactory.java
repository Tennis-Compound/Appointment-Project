
public class BookingRuleFactory {

    public static BookingRuleStrategy getRule(String appointmentType) {
        switch (appointmentType) {
            case "URGENT":
                return new UrgentBookingRule();
            case "FOLLOW_UP":
                return new FollowUpBookingRule();
            case "ASSESSMENT":
                return new AssessmentBookingRule();
            case "VIRTUAL":
                return new VirtualBookingRule();
            case "IN_PERSON":
                return new InPersonBookingRule();
            case "INDIVIDUAL":
                return new IndividualBookingRule();
            case "GROUP":
                return new GroupBookingRule();
            default:
                return null;
        }
    }
}