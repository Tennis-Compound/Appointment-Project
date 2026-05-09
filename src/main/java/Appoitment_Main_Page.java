import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

/**
 * Main class for the Appointment Scheduling System
 * This class handles:
 * - Application entry point
 * - User and admin authentication
 * - Menu navigation
 * - Appointment management
 * - Notification service initialization
 * 
 * It interacts with the database and notification services
 * to manage appointments and user actions.
 */
public class Appoitment_Main_Page {
	private static final Logger LOGGER = Logger.getLogger(Appoitment_Main_Page.class.getName());
	/** Indicates whether an admin is currently logged in */
	static boolean isLoggedIn = false;
	/** Stores the logged-in user's ID */
	private static int loggedInUserId = -1;
	/** Stores the logged-in user's name */
	private static String loggedInUserName = "";
	/** Handles sending notifications (email/mock) */
	private static NotificationManager notificationManager;
	/** Flag to determine whether to use mock notifications */
	private static boolean useMockNotifications = false;
	private static final String ENTER_USERNAME = "Enter username: ";
	private static final String ENTER_PASSWORD = "Enter password: ";
	private static final String CANNOT_CONNECT = "Cannot connect to database.";
	private static final String APPOINTMENT_ID = "appointment_id";
	private static final String APPOINTMENT_ID2 = "Appointment ID: ";
	private static final String Appointment_Type = "appointment_type";
	private static final String EMAIL = "email";
	private static final String START_DATETIME = "start_datetime";
	private static final String START = " | Start: ";
	private static final String END_DATETIME = "end_datetime";
	private static final String END = " | End: ";
	private static final String SLOT_ID = "slot_id";
	private static final String ERROR_DB = "Error fetching appointment: ";
	private static final String UPDATE_STMNT = "UPDATE \"TimeSlots\" SET is_available = true WHERE slot_id = ?";
	private static final String DATE = "\nDate: ";
	private static final String TIME = "\nTime: ";
	private static final String ERROR_FETCHING = "Error checking slot: ";
	private static final String UPDATE_STMNT2 = "UPDATE \"TimeSlots\" SET is_available = false WHERE slot_id = ?";
	private static final String TYPE = "Type: ";
	/**
     * Entry point of the application.
     * 
     * Initializes services and displays menus in a loop.
     * 
     * @param args command-line arguments (not used)
     */
	public static void main(String[] args) {
        LOGGER.info("Welcome to Appointment Scheduling System");
        initializeNotificationService();
        Scanner input = new Scanner(System.in);
        while (true) {
            if (!isLoggedIn) {
                int chosenNum = showMenu(input);
                switch (chosenNum) {
                    case 1: adminLogin(input); break;
                    case 2: userLogin(input); break;
                    case 3: userSignUp(input); break;
                    case 4: viewAvailableSlots(); break;
                    case 0:
                        LOGGER.info("Exiting system. Goodbye!");
                        DatabaseConnection.closeConnection();
                        input.close();
                        return;
                    default: LOGGER.info("Invalid choice. Try again.");
                }
            } else {
                adminMenu(input);
            }
        }
    }

	/**
     * Initializes the notification service.
     * 
     * Loads SMTP configuration from environment variables using Dotenv.
     * If configuration is missing or fails, falls back to a mock service.
     */
	private static void initializeNotificationService() {
        if (useMockNotifications) {
            notificationManager = new NotificationManager(new MockNotificationService());
            LOGGER.info("Using mock notification service for testing");
        } else {
            try {
                Dotenv dotenv = Dotenv.configure()
                        .directory(".")
                        .ignoreIfMissing()
                        .load();
                String smtpHost = dotenv.get("SMTP_HOST");
                String smtpPort = dotenv.get("SMTP_PORT");
                String smtpUsername = dotenv.get("SMTP_USERNAME");
                String smtpPassword = dotenv.get("SMTP_PASSWORD");
                if (smtpHost != null && smtpPort != null && smtpUsername != null && smtpPassword != null) {
                    EmailNotificationService emailService = new EmailNotificationService(
                            smtpHost, smtpPort, smtpUsername, smtpPassword);
                    notificationManager = new NotificationManager(emailService);
                    LOGGER.info("Email notification service initialized for: " + smtpUsername);
                } else {
                    LOGGER.info("Email configuration incomplete, using mock service");
                    notificationManager = new NotificationManager(new MockNotificationService());
                }
            } catch (Exception e) {
                LOGGER.info("Failed to initialize email service, using mock: " + e.getMessage());
                notificationManager = new NotificationManager(new MockNotificationService());
            }
        }
    }

	/**
     * Displays the main menu and reads user input.
     * 
     * @param input Scanner object for user input
     * @return the selected menu option
     */
	private static int showMenu(Scanner input) {
        LOGGER.info("\nEnter the number following what you want to do");
        LOGGER.info("1- Administrator Login ");
        LOGGER.info("2- User Login");
        LOGGER.info("3- User Sign up");
        LOGGER.info("4- View Available Appointment Slots");
        LOGGER.info("0- Exit Program ");
        int choice = input.nextInt();
        input.nextLine();
        return choice;
    }
	
	/**
     * Handles administrator login.
     * 
     * Reads credentials from environment variables and compares them
     * with user input.
     * 
     * @param input Scanner object for user input
     */
	private static void adminLogin(Scanner input) {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMissing()
                .load();
        String adminUsername = dotenv.get("adminName");
        String adminPassword = dotenv.get("adminpassword");
 
        if (adminUsername == null || adminPassword == null) {
            LOGGER.info("Admin credentials not configured.");
            return;
        }
 
        LOGGER.info(ENTER_USERNAME);
        String username = input.nextLine();
        LOGGER.info(ENTER_PASSWORD);
        String password = input.nextLine();
 
        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            LOGGER.info("Login Successful");
            isLoggedIn = true;
        } else {
            LOGGER.info("Invalid Credentials");
        }
    }
	
	/**
     * Displays the administrator menu and handles admin actions.
     * 
     * @param input Scanner object for user input
     */
	private static void adminMenu(Scanner input) {
        LOGGER.info("\nAdministrator Menu");
        LOGGER.info("1- Logout");
        LOGGER.info("2- View All Reservations");
        LOGGER.info("3- Cancel a Reservation");
        LOGGER.info("4- Modify a Reservation");
        LOGGER.info("5- Test Notifications");
        int choice = input.nextInt();
        input.nextLine();
        switch (choice) {
            case 1:
                isLoggedIn = false;
                LOGGER.info("You have been logged out successfully");
                break;
            case 2: adminViewAllReservations(); break;
            case 3: adminCancelReservation(input); break;
            case 4: adminModifyReservation(input); break;
            case 5: testNotifications(input); break;
            default: LOGGER.info("Invalid option.");
        }
    }
	
	/**
     * Tests the notification system by sending a booking confirmation
     * and a cancellation notice to a specified email address.
     *
     * Prompts the admin to enter a target email, falling back to
     * "demo@example.com" if none is provided.
     *
     * @param input Scanner object for user input
     */
	private static void testNotifications(Scanner input) {
        LOGGER.info("Testing Notification System");
        LOGGER.info("Enter email to test (or press Enter for demo@example.com): ");
        String email = input.nextLine();
        if (email.trim().isEmpty()) {
            email = "demo@example.com";
        }
        String testMessage = "This is a test notification from the Appointment System.";
        LOGGER.info("Sending booking confirmation to: " + email);
        notificationManager.sendBookingConfirmation(email, testMessage);
        LOGGER.info("Sending cancellation notice to: " + email);
        notificationManager.sendCancellationNotice(email, testMessage);
        LOGGER.info("Test notifications sent! Check your email inbox.");
    }
	
	/**
     * Retrieves and displays all reservations from the database.
     * 
     * Shows appointment details including user info and time slots.
     */
	private static void adminViewAllReservations() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            LOGGER.info(CANNOT_CONNECT);
            return;
        }
        String query = "SELECT a.appointment_id, a.appointment_type, u.name AS user_name, u.email, " +
                       "t.start_datetime, t.end_datetime " +
                       "FROM \"Appointment\" a " +
                       "JOIN \"Users\" u ON a.user_id = u.user_id " +
                       "JOIN \"TimeSlots\" t ON a.slot_id = t.slot_id " +
                       "ORDER BY a.appointment_id";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            LOGGER.info("\n=== All Reservations ===");
            boolean found = false;
            while (rs.next()) {
                found = true;
                LOGGER.info(
                    APPOINTMENT_ID2 + rs.getInt(APPOINTMENT_ID) +
                    " | Type: " + rs.getString(Appointment_Type) +
                    " | User: " + rs.getString("user_name") +
                    " | Email: " + rs.getString(EMAIL) +
                    START + rs.getTimestamp(START_DATETIME) +
                    END + rs.getTimestamp(END_DATETIME)
                );
            }
            if (!found) {
                LOGGER.info("No reservations found.");
            }
        } catch (SQLException e) {
            LOGGER.info("Error fetching reservations: " + e.getMessage());
        }
    }
	
	/**
     * Cancels a reservation by appointment ID.
     * 
     * Also updates slot availability and sends a cancellation notification.
     * 
     * @param input Scanner object for user input
     */
	private static void adminCancelReservation(Scanner input) {
        adminViewAllReservations();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            LOGGER.info(CANNOT_CONNECT);
            return;
        }
        LOGGER.info("Enter the Appointment ID you want to cancel: ");
        int appointmentId = input.nextInt();
        input.nextLine();
 
        int slotId;
        String userEmail;
        Timestamp startTime;
        Timestamp endTime;
 
        try (PreparedStatement getDetailsStmt = conn.prepareStatement(
                "SELECT a.slot_id, u.email, t.start_datetime, t.end_datetime " +
                "FROM \"Appointment\" a " +
                "JOIN \"Users\" u ON a.user_id = u.user_id " +
                "JOIN \"TimeSlots\" t ON a.slot_id = t.slot_id " +
                "WHERE a.appointment_id = ?")) {
            getDetailsStmt.setInt(1, appointmentId);
            try (ResultSet detailsRs = getDetailsStmt.executeQuery()) {
                if (!detailsRs.next()) {
                    LOGGER.info("Appointment not found.");
                    return;
                }
                slotId = detailsRs.getInt(SLOT_ID);
                userEmail = detailsRs.getString(EMAIL);
                startTime = detailsRs.getTimestamp(START_DATETIME);
                endTime = detailsRs.getTimestamp(END_DATETIME);
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_DB + e.getMessage());
            return;
        }
 
        try (PreparedStatement deleteStmt = conn.prepareStatement(
                "DELETE FROM \"Appointment\" WHERE appointment_id = ?")) {
            deleteStmt.setInt(1, appointmentId);
            int rowsAffected = deleteStmt.executeUpdate();
            if (rowsAffected > 0) {
                try (PreparedStatement updateStmt = conn.prepareStatement(
                        UPDATE_STMNT)) {
                    updateStmt.setInt(1, slotId);
                    updateStmt.executeUpdate();
                }
                String appointmentDetails = APPOINTMENT_ID2 + appointmentId +
                                            DATE + startTime +
                                            TIME + startTime + " - " + endTime;
                notificationManager.sendCancellationNotice(userEmail, appointmentDetails);
                notificationManager.cancelReminder(appointmentId);
                LOGGER.info("Reservation cancelled successfully! Email sent to: " + userEmail);
            } else {
                LOGGER.info("Failed to cancel reservation.");
            }
        } catch (SQLException e) {
            LOGGER.info("Error cancelling reservation: " + e.getMessage());
        }
    }
	
	/**
     * Allows an administrator to reschedule an existing reservation to a
     * different available time slot.
     *
     * Displays all current reservations, then prompts for the appointment ID
     * to modify and the new slot ID. Validates that the chosen slot exists
     * and is available, updates the appointment and both affected time slots
     * in the database, cancels the old reminder, and sends a modification
     * notification to the user.
     *
     * @param input Scanner object for user input
     */
	private static void adminModifyReservation(Scanner input) {
        adminViewAllReservations();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            LOGGER.info(CANNOT_CONNECT);
            return;
        }
        LOGGER.info("Enter the Appointment ID you want to modify: ");
        int appointmentId = input.nextInt();
        input.nextLine();
 
        int oldSlotId;
        String userEmail;
        String userName;
        Timestamp oldStartTime;
        Timestamp oldEndTime;
 
        try (PreparedStatement getApptStmt = conn.prepareStatement(
                "SELECT a.slot_id, u.name AS user_name, u.email, " +
                "t.start_datetime, t.end_datetime " +
                "FROM \"Appointment\" a " +
                "JOIN \"Users\" u ON a.user_id = u.user_id " +
                "JOIN \"TimeSlots\" t ON a.slot_id = t.slot_id " +
                "WHERE a.appointment_id = ?")) {
            getApptStmt.setInt(1, appointmentId);
            try (ResultSet apptRs = getApptStmt.executeQuery()) {
                if (!apptRs.next()) {
                    LOGGER.info("Appointment not found.");
                    return;
                }
                oldSlotId = apptRs.getInt(SLOT_ID);
                userEmail = apptRs.getString(EMAIL);
                userName = apptRs.getString("user_name");
                oldStartTime = apptRs.getTimestamp(START_DATETIME);
                oldEndTime = apptRs.getTimestamp(END_DATETIME);
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_DB + e.getMessage());
            return;
        }
 
        LOGGER.info("\nCurrent Appointment Details:");
        LOGGER.info("User: " + userName);
        LOGGER.info("Email: " + userEmail);
        LOGGER.info("Current Slot: " + oldStartTime + " to " + oldEndTime);
        LOGGER.info("\nAvailable Slots for Modification:");
        viewAvailableSlots();
        LOGGER.info("Enter the new Slot ID (or 0 to cancel): ");
        int newSlotId = input.nextInt();
        input.nextLine();
        if (newSlotId == 0) {
            LOGGER.info("Modification cancelled.");
            return;
        }
 
        boolean isAvailable;
        Timestamp newStartTime;
        Timestamp newEndTime;
 
        try (PreparedStatement checkSlotStmt = conn.prepareStatement(
                "SELECT is_available, start_datetime, end_datetime FROM \"TimeSlots\" WHERE slot_id = ?")) {
            checkSlotStmt.setInt(1, newSlotId);
            try (ResultSet slotRs = checkSlotStmt.executeQuery()) {
                if (!slotRs.next()) {
                    LOGGER.info("Invalid slot ID.");
                    return;
                }
                isAvailable = slotRs.getBoolean("is_available");
                newStartTime = slotRs.getTimestamp(START_DATETIME);
                newEndTime = slotRs.getTimestamp(END_DATETIME);
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_FETCHING + e.getMessage());
            return;
        }
 
        if (!isAvailable) {
            LOGGER.info("Selected slot is not available.");
            return;
        }
 
        try (PreparedStatement updateApptStmt = conn.prepareStatement(
                "UPDATE \"Appointment\" SET slot_id = ? WHERE appointment_id = ?")) {
            updateApptStmt.setInt(1, newSlotId);
            updateApptStmt.setInt(2, appointmentId);
            updateApptStmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Error updating appointment: " + e.getMessage());
            return;
        }
 
        try (PreparedStatement freeOldSlotStmt = conn.prepareStatement(
                UPDATE_STMNT)) {
            freeOldSlotStmt.setInt(1, oldSlotId);
            freeOldSlotStmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Error freeing old slot: " + e.getMessage());
        }
 
        try (PreparedStatement bookNewSlotStmt = conn.prepareStatement(
                UPDATE_STMNT2)) {
            bookNewSlotStmt.setInt(1, newSlotId);
            bookNewSlotStmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Error booking new slot: " + e.getMessage());
        }
 
        String oldDetails = "Date: " + oldStartTime + TIME + oldStartTime + " - " + oldEndTime;
        String newDetails = "Date: " + newStartTime + TIME + newStartTime + " - " + newEndTime;
        notificationManager.sendModificationNotice(userEmail, oldDetails, newDetails);
        notificationManager.cancelReminder(appointmentId);
        LOGGER.info("Reservation modified successfully! Email sent to: " + userEmail);
    }
	
	/**
     * Handles user login by verifying credentials from the database.
     * 
     * @param input Scanner object for user input
     */
	 private static void userLogin(Scanner input) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            LOGGER.info(CANNOT_CONNECT);
            return;
        }
        LOGGER.info(ENTER_USERNAME);
        String username = input.nextLine();
        LOGGER.info(ENTER_PASSWORD);
        String password = input.nextLine();
 
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM \"Users\" WHERE name = ARRAY[?] AND password = ARRAY[?]")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    loggedInUserId = rs.getInt("user_id");
                    loggedInUserName = rs.getString("name");
                    LOGGER.info("Login Successful, Welcome " + loggedInUserName);
                    userMenu(input);
                } else {
                    LOGGER.info("Invalid username or password");
                }
            }
        } catch (SQLException e) {
            LOGGER.info("Error during login: " + e.getMessage());
        }
    }
	
	/**
     * Registers a new user in the system.
     * 
     * @param input Scanner object for user input
     */
	private static void userSignUp(Scanner input) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            LOGGER.info(CANNOT_CONNECT);
            return;
        }
        LOGGER.info(ENTER_USERNAME);
        String username = input.nextLine();
        LOGGER.info("Enter Email: ");
        String userEmail = input.nextLine();
        LOGGER.info(ENTER_PASSWORD);
        String password = input.nextLine();
 
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO \"Users\" (name, email, password) VALUES (ARRAY[?], ARRAY[?], ARRAY[?])")) {
            stmt.setString(1, username);
            stmt.setString(2, userEmail);
            stmt.setString(3, password);
            int rs = stmt.executeUpdate();
            if (rs > 0) {
                LOGGER.info("Sign up successful!");
            }
        } catch (SQLException e) {
            LOGGER.info("Error during Sign Up: " + e.getMessage());
        }
    }
	
	/**
     * Displays the user menu in a loop and dispatches actions based on
     * the logged-in user's selection.
     *
     * Available actions include viewing available slots, reading booking
     * rules, booking an appointment, viewing existing appointments,
     * modifying an appointment, cancelling an appointment, and logging out.
     * The loop exits when the user chooses to log out.
     *
     * @param input Scanner object for user input
     */
	 private static void userMenu(Scanner input) {
        while (true) {
            LOGGER.info("\nUser Menu - Welcome " + loggedInUserName);
            LOGGER.info("1- View Available Appointment Slots");
            LOGGER.info("2- Show booking rules");
            LOGGER.info("3- Book an Appointment");
            LOGGER.info("4- View My Appointment");
            LOGGER.info("5- Modify an appointment");
            LOGGER.info("6- Cancel an appointment");
            LOGGER.info("0- Logout");
            int choice = input.nextInt();
            input.nextLine();
            switch (choice) {
                case 1: viewAvailableSlots(); break;
                case 2: showBookingRules(); break;
                case 3: bookAppointment(input); break;
                case 4: viewMyAppointments(); break;
                case 5: modifyAppointment(input); break;
                case 6: cancelAppointment(input); break;
                case 0:
                    loggedInUserId = -1;
                    loggedInUserName = "";
                    LOGGER.info("Logged out successfully");
                    return;
                default: LOGGER.info("Invalid option.");
            }
        }
    }
	
	/**
     * Prints the booking constraints for each supported appointment type.
     *
     * Rules displayed:
     * <ul>
     *   <li>URGENT and FOLLOW_UP require a duration of 30 minutes or less.</li>
     *   <li>ASSESSMENT requires a duration of at least 60 minutes.</li>
     *   <li>VIRTUAL requires no physical location.</li>
     *   <li>IN_PERSON requires a physical location.</li>
     *   <li>INDIVIDUAL requires exactly one participant.</li>
     *   <li>GROUP requires more than one participant.</li>
     * </ul>
     */
	private static void showBookingRules() {
        LOGGER.info("\nBooking Rules by Appointment Type:");
        LOGGER.info("URGENT      -> duration must be 30 minutes or less");
        LOGGER.info("FOLLOW_UP   -> duration must be 30 minutes or less");
        LOGGER.info("ASSESSMENT  -> duration must be at least 60 minutes");
        LOGGER.info("VIRTUAL     -> no physical location is required");
        LOGGER.info("IN_PERSON   -> physical location is required");
        LOGGER.info("INDIVIDUAL  -> participant count must be exactly 1");
        LOGGER.info("GROUP       -> participant count must be more than 1");
    }
	
	/**
     * Displays available appointment slots.
     * 
     * Fetches slots marked as available from the database.
     */
	private static void viewAvailableSlots() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            LOGGER.info(CANNOT_CONNECT);
            return;
        }
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM \"TimeSlots\" WHERE is_available = 'true'")) {
            LOGGER.info("\n Available Appointment Slots:");
            boolean found = false;
            while (rs.next()) {
                found = true;
                LOGGER.info("ID: " + rs.getInt(SLOT_ID) +
                        START + rs.getTimestamp(START_DATETIME) +
                        END + rs.getTimestamp(END_DATETIME));
            }
            if (!found) {
                LOGGER.info("No available slots found.");
            }
        } catch (SQLException e) {
            LOGGER.info("Error fetching slots: " + e.getMessage());
        }
    }
	
	/**
     * Books a new appointment for the logged-in user.
     * 
     * Validates booking rules, checks slot availability,
     * updates database, and sends notifications.
     * 
     * @param input Scanner object for user input
     */
	private static void bookAppointment(Scanner input) {
        viewAvailableSlots();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            LOGGER.info(CANNOT_CONNECT);
            return;
        }
        LOGGER.info("Enter the slot ID you want to book: ");
        int slotID = input.nextInt();
        input.nextLine();
 
        LOGGER.info("Choose appointment type:");
        LOGGER.info("1- URGENT");
        LOGGER.info("2- FOLLOW_UP");
        LOGGER.info("3- ASSESSMENT");
        LOGGER.info("4- VIRTUAL");
        LOGGER.info("5- IN_PERSON");
        LOGGER.info("6- INDIVIDUAL");
        LOGGER.info("7- GROUP");
        int typeChoice = input.nextInt();
        input.nextLine();
 
        String appointmentType;
        switch (typeChoice) {
            case 1: appointmentType = "URGENT"; break;
            case 2: appointmentType = "FOLLOW_UP"; break;
            case 3: appointmentType = "ASSESSMENT"; break;
            case 4: appointmentType = "VIRTUAL"; break;
            case 5: appointmentType = "IN_PERSON"; break;
            case 6: appointmentType = "INDIVIDUAL"; break;
            case 7: appointmentType = "GROUP"; break;
            default:
                LOGGER.info("Invalid appointment type.");
                return;
        }
 
        LOGGER.info("Enter duration in minutes: ");
        int durationMinutes = input.nextInt();
        input.nextLine();
        LOGGER.info("Enter number of participants: ");
        int participantCount = input.nextInt();
        input.nextLine();
        LOGGER.info("Enter location (leave empty for none): ");
        String location = input.nextLine();
 
        AppointmentRequest request = new AppointmentRequest(appointmentType, durationMinutes, participantCount, location);
        BookingRuleStrategy rule = BookingRuleFactory.getRule(appointmentType);
        if (rule != null && !rule.isValid(request)) {
            LOGGER.info("Booking failed: " + rule.getErrorMessage());
            return;
        }
 
        Timestamp startTime;
        Timestamp endTime;
 
        try (PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT * FROM \"TimeSlots\" WHERE slot_id = ? AND is_available = true")) {
            checkStmt.setInt(1, slotID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    LOGGER.info("Slot not available, does not exist, or is in the past.");
                    return;
                }
                startTime = rs.getTimestamp(START_DATETIME);
                endTime = rs.getTimestamp(END_DATETIME);
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_FETCHING + e.getMessage());
            return;
        }
 
        try (PreparedStatement bookStmt = conn.prepareStatement(
                "INSERT INTO \"Appointment\" (user_id, slot_id, appointment_type) VALUES (?, ?, ARRAY[?]) RETURNING appointment_id")) {
            bookStmt.setInt(1, loggedInUserId);
            bookStmt.setInt(2, slotID);
            bookStmt.setString(3, appointmentType);
            try (ResultSet bookRs = bookStmt.executeQuery()) {
                if (bookRs.next()) {
                    int appointmentId = bookRs.getInt(APPOINTMENT_ID);
 
                    try (PreparedStatement updateStmt = conn.prepareStatement(
                            UPDATE_STMNT2)) {
                        updateStmt.setInt(1, slotID);
                        updateStmt.executeUpdate();
                    }
 
                    try (PreparedStatement userStmt = conn.prepareStatement(
                            "SELECT email FROM \"Users\" WHERE user_id = ?")) {
                        userStmt.setInt(1, loggedInUserId);
                        try (ResultSet userRs = userStmt.executeQuery()) {
                            if (userRs.next()) {
                                String userEmail = userRs.getString(EMAIL);
                                String appointmentDetails = APPOINTMENT_ID2 + appointmentId +
                                        "\nType: " + appointmentType +
                                        "\nDuration: " + durationMinutes + " minutes" +
                                        "\nParticipants: " + participantCount +
                                        "\nLocation: " + (location.isEmpty() ? "None" : location) +
                                        DATE + startTime +
                                        TIME + startTime + " - " + endTime;
                                notificationManager.sendBookingConfirmation(userEmail, appointmentDetails);
                                notificationManager.scheduleReminder(userEmail, appointmentId,
                                        appointmentDetails, startTime.getTime());
                                LOGGER.info("Appointment booked successfully! Email sent to: " + userEmail);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.info("Error booking appointment: " + e.getMessage());
        }
    }
	
	/**
     * Displays all appointments for the logged-in user.
     */
	private static void viewMyAppointments() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            LOGGER.info(CANNOT_CONNECT);
            return;
        }
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT a.appointment_id, a.appointment_type, t.start_datetime, t.end_datetime " +
                "FROM \"Appointment\" a " +
                "JOIN \"TimeSlots\" t ON a.slot_id = t.slot_id " +
                "WHERE a.user_id = ?")) {
            stmt.setInt(1, loggedInUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                LOGGER.info("\nYour Appointments:");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    Timestamp start = rs.getTimestamp(START_DATETIME);
                    String status = start.before(new Timestamp(System.currentTimeMillis())) ? "PAST" : "UPCOMING";
                    LOGGER.info(
                        APPOINTMENT_ID2 + rs.getInt(APPOINTMENT_ID) +
                        " | Type: " + rs.getString(Appointment_Type) +
                        START + start +
                        END + rs.getTimestamp(END_DATETIME) +
                        " | Status: " + status
                    );
                }
                if (!found) {
                    LOGGER.info("You have no appointments.");
                }
            }
        } catch (SQLException e) {
            LOGGER.info("Error fetching appointments: " + e.getMessage());
        }
    }

	/**
     * Allows the logged-in user to reschedule one of their upcoming
     * appointments to a different available time slot.
     *
     * Displays the user's current appointments, then prompts for the
     * appointment ID to change and the desired new slot ID. Only future
     * appointments belonging to the logged-in user may be modified.
     * On success, the old slot is freed, the new slot is marked
     * unavailable, a modification notification is sent to the user,
     * the old reminder is cancelled, and a new reminder is scheduled
     * for the updated time.
     *
     * @param input Scanner object for user input
     */
private static void modifyAppointment(Scanner input) {
        viewMyAppointments();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            LOGGER.info(CANNOT_CONNECT);
            return;
        }
        LOGGER.info("Enter the Appointment ID you want to modify: ");
        int appointmentId = input.nextInt();
        input.nextLine();
 
        int oldSlotId;
        String userEmail;
        String userName;
        Timestamp oldStartTime;
        Timestamp oldEndTime;
        String oldAppointmentType;
 
        try (PreparedStatement getApptStmt = conn.prepareStatement(
                "SELECT a.slot_id, u.email, u.name, t.start_datetime, t.end_datetime, a.appointment_type " +
                "FROM \"Appointment\" a " +
                "JOIN \"Users\" u ON a.user_id = u.user_id " +
                "JOIN \"TimeSlots\" t ON a.slot_id = t.slot_id " +
                "WHERE a.appointment_id = ? AND a.user_id = ? AND t.start_datetime > NOW()")) {
            getApptStmt.setInt(1, appointmentId);
            getApptStmt.setInt(2, loggedInUserId);
            try (ResultSet apptRs = getApptStmt.executeQuery()) {
                if (!apptRs.next()) {
                    LOGGER.info("Appointment not found, belongs to another user, or cannot be modified.");
                    return;
                }
                oldSlotId = apptRs.getInt(SLOT_ID);
                userEmail = apptRs.getString(EMAIL);
                userName = apptRs.getString("name");
                oldStartTime = apptRs.getTimestamp(START_DATETIME);
                oldEndTime = apptRs.getTimestamp(END_DATETIME);
                oldAppointmentType = apptRs.getString(Appointment_Type);
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_DB + e.getMessage());
            return;
        }
 
        LOGGER.info("\nCurrent Appointment Details:");
        LOGGER.info("User: " + userName);
        LOGGER.info("Email: " + userEmail);
        LOGGER.info(TYPE + oldAppointmentType);
        LOGGER.info("Current Slot: " + oldStartTime + " to " + oldEndTime);
        LOGGER.info("\nAvailable Slots for Modification:");
        viewAvailableSlots();
        LOGGER.info("Enter the new Slot ID (or 0 to cancel): ");
        int newSlotId = input.nextInt();
        input.nextLine();
        if (newSlotId == 0) {
            LOGGER.info("Modification cancelled.");
            return;
        }
 
        boolean isAvailable;
        Timestamp newStartTime;
        Timestamp newEndTime;
 
        try (PreparedStatement checkSlotStmt = conn.prepareStatement(
                "SELECT is_available, start_datetime, end_datetime FROM \"TimeSlots\" WHERE slot_id = ? AND start_datetime > NOW()")) {
            checkSlotStmt.setInt(1, newSlotId);
            try (ResultSet slotRs = checkSlotStmt.executeQuery()) {
                if (!slotRs.next()) {
                    LOGGER.info("Invalid slot ID, slot not available, or slot is in the past.");
                    return;
                }
                isAvailable = slotRs.getBoolean("is_available");
                newStartTime = slotRs.getTimestamp(START_DATETIME);
                newEndTime = slotRs.getTimestamp(END_DATETIME);
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_FETCHING + e.getMessage());
            return;
        }
 
        if (!isAvailable) {
            LOGGER.info("Selected slot is not available.");
            return;
        }
 
        try (PreparedStatement updateApptStmt = conn.prepareStatement(
                "UPDATE \"Appointment\" SET slot_id = ? WHERE appointment_id = ?")) {
            updateApptStmt.setInt(1, newSlotId);
            updateApptStmt.setInt(2, appointmentId);
            int rowsUpdated = updateApptStmt.executeUpdate();
            if (rowsUpdated <= 0) {
                LOGGER.info("Failed to modify appointment.");
                return;
            }
        } catch (SQLException e) {
            LOGGER.info("Error updating appointment: " + e.getMessage());
            return;
        }
 
        try (PreparedStatement freeOldSlotStmt = conn.prepareStatement(
                UPDATE_STMNT)) {
            freeOldSlotStmt.setInt(1, oldSlotId);
            freeOldSlotStmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Error freeing old slot: " + e.getMessage());
        }
 
        try (PreparedStatement bookNewSlotStmt = conn.prepareStatement(
                UPDATE_STMNT2)) {
            bookNewSlotStmt.setInt(1, newSlotId);
            bookNewSlotStmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Error booking new slot: " + e.getMessage());
        }
 
        String oldDetails = TYPE + oldAppointmentType + DATE + oldStartTime + TIME + oldStartTime + " - " + oldEndTime;
        String newDetails = TYPE + oldAppointmentType + DATE + newStartTime + TIME + newStartTime + " - " + newEndTime;
        notificationManager.sendModificationNotice(userEmail, oldDetails, newDetails);
        notificationManager.cancelReminder(appointmentId);
 
        String appointmentDetails = APPOINTMENT_ID2 + appointmentId +
                "\nType: " + oldAppointmentType +
                DATE + newStartTime +
                TIME + newStartTime + " - " + newEndTime;
        notificationManager.scheduleReminder(userEmail, appointmentId, appointmentDetails, newStartTime.getTime());
 
        LOGGER.info("Appointment modified successfully! Email sent to: " + userEmail);
        LOGGER.info("New appointment time: " + newStartTime + " to " + newEndTime);
    }
	
	/**
     * Cancels a user's appointment.
     * 
     * Updates database and sends cancellation notification.
     * 
     * @param input Scanner object for user input
     */
	private static void cancelAppointment(Scanner input) {
        viewMyAppointments();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            LOGGER.info(CANNOT_CONNECT);
            return;
        }
        LOGGER.info("Enter the Appointment ID you want to cancel: ");
        int appointmentId = input.nextInt();
        input.nextLine();
 
        int slotId;
        String userEmail;
        Timestamp startTime;
        Timestamp endTime;
 
        try (PreparedStatement getStmt = conn.prepareStatement(
                "SELECT a.slot_id, u.email, t.start_datetime, t.end_datetime " +
                "FROM \"Appointment\" a " +
                "JOIN \"Users\" u ON a.user_id = u.user_id " +
                "JOIN \"TimeSlots\" t ON a.slot_id = t.slot_id " +
                "WHERE a.appointment_id = ? AND a.user_id = ?")) {
            getStmt.setInt(1, appointmentId);
            getStmt.setInt(2, loggedInUserId);
            try (ResultSet rs = getStmt.executeQuery()) {
                if (!rs.next()) {
                    LOGGER.info("Appointment not found or does not belong to you.");
                    return;
                }
                slotId = rs.getInt(SLOT_ID);
                userEmail = rs.getString(EMAIL);
                startTime = rs.getTimestamp(START_DATETIME);
                endTime = rs.getTimestamp(END_DATETIME);
            }
        } catch (SQLException e) {
            LOGGER.info(ERROR_DB + e.getMessage());
            return;
        }
 
        try (PreparedStatement deleteStmt = conn.prepareStatement(
                "DELETE FROM \"Appointment\" WHERE appointment_id = ?")) {
            deleteStmt.setInt(1, appointmentId);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Error deleting appointment: " + e.getMessage());
            return;
        }
 
        try (PreparedStatement freeStmt = conn.prepareStatement(
                UPDATE_STMNT)) {
            freeStmt.setInt(1, slotId);
            freeStmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Error freeing slot: " + e.getMessage());
        }
 
        String appointmentDetails = APPOINTMENT_ID2 + appointmentId +
                DATE + startTime +
                TIME + startTime + " - " + endTime;
        notificationManager.sendCancellationNotice(userEmail, appointmentDetails);
        notificationManager.cancelReminder(appointmentId);
        LOGGER.info("Appointment cancelled successfully! Email sent to: " + userEmail);
    }
}
