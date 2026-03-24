package sprent2;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ParticipantRule participantRule = new ParticipantRule(5);
        BookingService service = new BookingService(participantRule);
        while (true) {
            System.out.println("\n===== Booking System =====");
            System.out.println("1 - Show booking rules");
            System.out.println("2 - Show appointments");
            System.out.println("3 - Book new appointment");
            System.out.println("4 - Cancel an appointment");
            System.out.println("0 - Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1:
                    service.showBookingRules();
                    break;

                case 2:
                    service.showAppointments();
                    break;

                case 3:
                    System.out.print("Enter date (YYYY-MM-DD): ");
                    String date = sc.nextLine();
                    System.out.print("Enter time (HH:MM): ");
                    String time = sc.nextLine();
                    System.out.print("Enter duration (minutes): ");
                    int duration = sc.nextInt();
                    System.out.print("Enter number of participants: ");
                    int participants = sc.nextInt();
                    sc.nextLine();
                    Appointment newApp = new Appointment(date, time, duration, participants);
                    service.bookAppointment(newApp);
                    break;
                case 4:
                    if (service.getAppointments().isEmpty()) {
                        System.out.println("No appointments to cancel.");
                        break;
                    }
                    service.showAppointments();
                    System.out.print("Enter index of appointment to cancel (starting from 1): ");
                    int index = sc.nextInt() - 1;
                    sc.nextLine();
                    service.cancelAppointment(index);
                    break;

                case 0:
                    System.out.println("Exiting system. Goodbye!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}