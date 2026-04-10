
public class AppointmentRequest {
    private String appointmentType;
    private int durationMinutes;
    private int participantCount;
    private String location;

    public AppointmentRequest(String appointmentType, int durationMinutes, int participantCount, String location) {
        this.appointmentType = appointmentType;
        this.durationMinutes = durationMinutes;
        this.participantCount = participantCount;
        this.location = location;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public String getLocation() {
        return location;
    }
}