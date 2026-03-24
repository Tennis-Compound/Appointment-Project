package sprent2;

import java.util.ArrayList;
import java.util.List;

public class BookingService {
    private List<Appointment> appointments = new ArrayList<>();
    private final int maxDuration = 30; // الحد الأقصى للمدة بالدقائق
    private ParticipantRule participantRule;

    public BookingService(ParticipantRule participantRule) {
        this.participantRule = participantRule;
    }

    // حجز موعد
    public boolean bookAppointment(Appointment appointment) {
        if (appointment.getDuration() > maxDuration) {
            System.out.println("Error: Duration exceeds maximum allowed (" + maxDuration + " mins).");
            return false;
        }
        if (!participantRule.isValid(appointment.getParticipants())) {
            System.out.println("Error: Participants exceed maximum allowed (" +
                               participantRule.getMaxParticipants() + ").");
            return false;
        }
        appointments.add(appointment);
        System.out.println("Appointment booked successfully!");
        return true;
    }

    // إلغاء موعد حسب رقم index
    public boolean cancelAppointment(int index) {
        if (index >= 0 && index < appointments.size()) {
            Appointment removed = appointments.remove(index);
            System.out.println("Appointment canceled successfully!");
            return true;
        } else {
            System.out.println("Invalid index!");
            return false;
        }
    }

    // عرض كل المواعيد مع أرقام
    public void showAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("No appointments booked yet.");
            return;
        }
        System.out.println("\n--- Appointments ---");
        int i = 1;
        for (Appointment a : appointments) {
            System.out.print(i + ". "); // الرقم أمام الموعد
            a.displayInfo();
            i++;
        }
    }

    // عرض القواعد
    public void showBookingRules() {
        System.out.println("\nBooking Rules:");
        System.out.println("- Maximum duration: " + maxDuration + " minutes");
        System.out.println("- Maximum participants per appointment: " + participantRule.getMaxParticipants());
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}