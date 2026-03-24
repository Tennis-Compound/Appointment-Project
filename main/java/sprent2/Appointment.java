package sprent2;

public class Appointment {
    private String date;
    private String time;
    private int duration;      // بالدقائق
    private int participants;
    private String status;

    // Constructor
    public Appointment(String date, String time, int duration, int participants) {
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.participants = participants;
        this.status = "Confirmed";
    }

    // Getters
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getDuration() { return duration; }
    public int getParticipants() { return participants; }
    public String getStatus() { return status; }

    // Setters
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setParticipants(int participants) { this.participants = participants; }
    public void setStatus(String status) { this.status = status; }

    // Display appointment info
    public void displayInfo() {
        System.out.println("Date: " + date + ", Time: " + time +
                           ", Duration: " + duration + " mins" +
                           ", Participants: " + participants +
                           ", Status: " + status);
    }

	
}