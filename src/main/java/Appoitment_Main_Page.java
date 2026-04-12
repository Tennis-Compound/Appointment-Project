
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.*;

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
	
	static boolean isLoggedIn = false;
	private static int loggedInUserId = -1;
	private static String loggedInUserName = "";
	
	private static NotificationManager notificationManager;
	private static boolean useMockNotifications = false;
	
	public static void main(String[] args) {
		
		System.out.println("Welcome to Appointment Scheduling System");
		
		initializeNotificationService();
		
		Scanner input = new Scanner(System.in);
		
		while(true) {
			
			if(!isLoggedIn) {
				int chosenNum = showMenu(input);
				
				switch (chosenNum) {
					case 1:
						adminLogin(input);
						break;
					case 2:
						userLogin(input);
						break;
					case 3:
						userSignUp(input);
	                    break;
	                case 4:
	                	viewAvailableSlots();
	                    break;
	                case 0:
	                    System.out.println("Exiting system. Goodbye!");
	                    DatabaseConnection.closeConnection();
	                    input.close();
	                    return;
	                
	                default:
	                    System.out.println("Invalid choice. Try again.");
				}
			}
			else {
				adminMenu(input);
			}
		}
	}

	private static void initializeNotificationService() {
		if (useMockNotifications) {
			notificationManager = new NotificationManager(new MockNotificationService());
			System.out.println("Using mock notification service for testing");
		} else {
			try {
				Dotenv dotenv = Dotenv.configure()
						.directory(".")
						.load();
				
				String smtpHost = dotenv.get("SMTP_HOST");
				String smtpPort = dotenv.get("SMTP_PORT");
				String smtpUsername = dotenv.get("SMTP_USERNAME");
				String smtpPassword = dotenv.get("SMTP_PASSWORD");
				
				if (smtpHost != null && smtpPort != null && 
					smtpUsername != null && smtpPassword != null) {
					
					EmailNotificationService emailService = new EmailNotificationService(
						smtpHost, smtpPort, smtpUsername, smtpPassword);
					
					notificationManager = new NotificationManager(emailService);
					System.out.println("Email notification service initialized for: " + smtpUsername);
				} else {
					System.out.println("Email configuration incomplete, using mock service");
					notificationManager = new NotificationManager(new MockNotificationService());
				}
			} catch (Exception e) {
				System.out.println("Failed to initialize email service, using mock: " + e.getMessage());
				notificationManager = new NotificationManager(new MockNotificationService());
			}
		}
	}

	private static int showMenu(Scanner input) {
		System.out.println("\nEnter the number following what you want to do");
		System.out.println("1- Administrator Login ");
		System.out.println("2- User Login");
		System.out.println("3- User Sign up");
		System.out.println("4- View Available Appointment Slots");
        System.out.println("0- Exit Program ");
        
		int choice = input.nextInt();
		input.nextLine();
		return choice;
	}
	
	private static void adminLogin(Scanner input) {
		Dotenv dotenv = Dotenv.configure()
				.directory(".")
		        .load();
		
		String adminUsername = dotenv.get("adminName");
		String adminPassword = dotenv.get("adminpassword");
		
		System.out.println("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter Email: ");
        String userEmail = input.nextLine();
        System.out.print("Enter password: ");
        String password = input.nextLine();

        Map<String, Object> result = attemptSignUp(username, userEmail, password);
        System.out.println((String) result.get("message"));
    }

    private void userMenu() {
        while (true) {
            System.out.println("\nUser Menu - Welcome " + this.loggedInUserName);
            System.out.println("1- View Available Appointment Slots");
            System.out.println("2- Show booking rules");
            System.out.println("3- Book an Appointment");
            System.out.println("4- View My Appointment");
            System.out.println("5- Modify an appointment");
            System.out.println("6- Cancel an appointment");
            System.out.println("0- Logout");

            int choice = readIntInput();

            if (choice == 0) {
                logout();
                return;
            }
            handleUserMenuChoice(choice);
        }
    }

    private void handleUserMenuChoice(int choice) {
        switch (choice) {
            case 1:
                viewAvailableSlotsUI();
                break;
            case 2:
                showBookingRules();
                break;
            case 3:
                bookAppointment();
                break;
            case 4:
                viewMyAppointmentsUI();
                break;
            case 5:
                modifyAppointmentUI();
                break;
            case 6:
                cancelAppointmentUI();
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void viewMyAppointmentsUI() {
        List<String> appointments = getAppointmentsForUser(this.loggedInUserId);
        System.out.println("\nYour Appointments:");
        if (appointments.isEmpty()) {
            System.out.println("You have no appointments.");
        } else {
            appointments.forEach(System.out::println);
        }
    }

    // --- ADMIN METHODS ---
    private void adminLogin() {
        try {
            Dotenv dotenv = Dotenv.configure().directory(".").load();
            String adminUsername = dotenv.get("adminName");
            String adminPassword = dotenv.get("adminpassword");

            System.out.print("Enter username: ");
            String username = input.nextLine();
            System.out.print("Enter password: ");
            String password = input.nextLine();

            if (username.equals(adminUsername) && password.equals(adminPassword)) {
                System.out.println("Login Successful");
                this.isLoggedIn = true;
            } else {
                System.out.println("Invalid Credentials");
            }
        } catch (Exception e) {
            System.out.println("Error loading admin credentials from .env file.");
        }
	}
	
	private static void adminMenu(Scanner input) {
		System.out.println("\nAdministrator Menu");
		System.out.println("1- Logout");
		System.out.println("2- View All Reservations");
		System.out.println("3- Cancel a Reservation");
		System.out.println("4- Modify a Reservation");
		System.out.println("5- Test Notifications");
	
		int choice = input.nextInt();
		input.nextLine();
	
		switch (choice) {
			case 1:
				isLoggedIn = false;
				System.out.println("You have been logged out successfully");
				break;
			case 2: 
				adminViewAllReservations(); 
				break;
			case 3: 
				adminCancelReservation(input); 
				break;
			case 4: 
				adminModifyReservation(input); 
				break;
			case 5:
				testNotifications(input);
				break;
			default:
				System.out.println("Invalid option.");
		}
	}
	
	private static void testNotifications(Scanner input) {
		System.out.println("Testing Notification System");
		System.out.println("Enter email to test (or press Enter for demo@example.com): ");
		String email = input.nextLine();
		
		if (email.trim().isEmpty()) {
			email = "demo@example.com";
		}
		
		String testMessage = "This is a test notification from the Appointment System.";
		
		System.out.println("Sending booking confirmation to: " + email);
		notificationManager.sendBookingConfirmation(email, testMessage);
		
		System.out.println("Sending cancellation notice to: " + email);
		notificationManager.sendCancellationNotice(email, testMessage);
		
		System.out.println("Test notifications sent! Check your email inbox.");
	}
	
	private static void adminViewAllReservations() {
		Connection conn = DatabaseConnection.getConnection();
		if (conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		try {
			PreparedStatement stmt = conn.prepareStatement(
				"SELECT a.appointment_id, a.appointment_type, u.name AS user_name, u.email, " +
				"t.start_datetime, t.end_datetime " +
				"FROM \"Appointment\" a " +
				"JOIN \"Users\" u ON a.user_id = u.user_id " +
				"JOIN \"TimeSlots\" t ON a.slot_id = t.slot_id " +
				"ORDER BY a.appointment_id"
			);
			ResultSet rs = stmt.executeQuery();
			
			System.out.println("\n=== All Reservations ===");
			boolean found = false;
			while (rs.next()) {
				found = true;
				System.out.println(
					"Appointment ID: " + rs.getInt("appointment_id") +
					" | Type: " + rs.getString("appointment_type") +
					" | User: " + rs.getString("user_name") +
					" | Email: " + rs.getString("email") +
					" | Start: " + rs.getTimestamp("start_datetime") +
					" | End: " + rs.getTimestamp("end_datetime")
				);
			}
			if (!found) {
				System.out.println("No reservations found.");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error fetching reservations: " + e.getMessage());
		}
	}
	
	private static void adminCancelReservation(Scanner input) {
		adminViewAllReservations();
		
		Connection conn = DatabaseConnection.getConnection();
		if (conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		System.out.println("Enter the Appointment ID you want to cancel: ");
		int appointmentId = input.nextInt();
		input.nextLine();
		
		try {
			PreparedStatement getDetailsStmt = conn.prepareStatement(
				"SELECT a.slot_id, u.email, t.start_datetime, t.end_datetime " +
				"FROM \"Appointment\" a " +
				"JOIN \"Users\" u ON a.user_id = u.user_id " +
				"JOIN \"TimeSlots\" t ON a.slot_id = t.slot_id " +
				"WHERE a.appointment_id = ?"
			);
			getDetailsStmt.setInt(1, appointmentId);
			ResultSet detailsRs = getDetailsStmt.executeQuery();
			
			if (!detailsRs.next()) {
				System.out.println("Appointment not found.");
				detailsRs.close();
				getDetailsStmt.close();
				return;
			}
			
			int slotId = detailsRs.getInt("slot_id");
			String userEmail = detailsRs.getString("email");
			Timestamp startTime = detailsRs.getTimestamp("start_datetime");
			Timestamp endTime = detailsRs.getTimestamp("end_datetime");
			detailsRs.close();
			getDetailsStmt.close();
			
			PreparedStatement deleteStmt = conn.prepareStatement(
				"DELETE FROM \"Appointment\" WHERE appointment_id = ?"
			);
			deleteStmt.setInt(1, appointmentId);
			int rowsAffected = deleteStmt.executeUpdate();
			deleteStmt.close();
			
			if (rowsAffected > 0) {
				PreparedStatement updateStmt = conn.prepareStatement(
					"UPDATE \"TimeSlots\" SET is_available = true WHERE slot_id = ?"
				);
				updateStmt.setInt(1, slotId);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				String appointmentDetails = "Appointment ID: " + appointmentId + 
										   "\nDate: " + startTime +
										   "\nTime: " + startTime + " - " + endTime;
				
				notificationManager.sendCancellationNotice(userEmail, appointmentDetails);
				notificationManager.cancelReminder(appointmentId);
				
				System.out.println("Reservation cancelled successfully! Email sent to: " + userEmail);
			} else {
				System.out.println("Failed to cancel reservation.");
			}
		} catch (SQLException e) {
			System.out.println("Error cancelling reservation: " + e.getMessage());
		}
	}
	
	private static void adminModifyReservation(Scanner input) {
		adminViewAllReservations();
		
		Connection conn = DatabaseConnection.getConnection();
		if (conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		System.out.println("Enter the Appointment ID you want to modify: ");
		int appointmentId = input.nextInt();
		input.nextLine();
		
		try {
			PreparedStatement getApptStmt = conn.prepareStatement(
				"SELECT a.slot_id, u.name AS user_name, u.email, " +
				"t.start_datetime, t.end_datetime " +
				"FROM \"Appointment\" a " +
				"JOIN \"Users\" u ON a.user_id = u.user_id " +
				"JOIN \"TimeSlots\" t ON a.slot_id = t.slot_id " +
				"WHERE a.appointment_id = ?"
			);
			getApptStmt.setInt(1, appointmentId);
			ResultSet apptRs = getApptStmt.executeQuery();
			
			if (!apptRs.next()) {
				System.out.println("Appointment not found.");
				apptRs.close();
				getApptStmt.close();
				return;
			}
			
			int oldSlotId = apptRs.getInt("slot_id");
			String userEmail = apptRs.getString("email");
			String userName = apptRs.getString("user_name");
			Timestamp oldStartTime = apptRs.getTimestamp("start_datetime");
			Timestamp oldEndTime = apptRs.getTimestamp("end_datetime");
			
			System.out.println("\nCurrent Appointment Details:");
			System.out.println("User: " + userName);
			System.out.println("Email: " + userEmail);
			System.out.println("Current Slot: " + oldStartTime + " to " + oldEndTime);
			apptRs.close();
			getApptStmt.close();
			
			System.out.println("\nAvailable Slots for Modification:");
			viewAvailableSlots();
			
			System.out.println("Enter the new Slot ID (or 0 to cancel): ");
			int newSlotId = input.nextInt();
			input.nextLine();
			
			if (newSlotId == 0) {
				System.out.println("Modification cancelled.");
				return;
			}
			
			PreparedStatement checkSlotStmt = conn.prepareStatement(
				"SELECT is_available, start_datetime, end_datetime FROM \"TimeSlots\" WHERE slot_id = ?"
			);
			checkSlotStmt.setInt(1, newSlotId);
			ResultSet slotRs = checkSlotStmt.executeQuery();
			
			if (!slotRs.next()) {
				System.out.println("Invalid slot ID.");
				slotRs.close();
				checkSlotStmt.close();
				return;
			}
			
			boolean isAvailable = slotRs.getBoolean("is_available");
			Timestamp newStartTime = slotRs.getTimestamp("start_datetime");
			Timestamp newEndTime = slotRs.getTimestamp("end_datetime");
			slotRs.close();
			checkSlotStmt.close();
			
			if (!isAvailable) {
				System.out.println("Selected slot is not available.");
				return;
			}
			
			PreparedStatement updateApptStmt = conn.prepareStatement(
				"UPDATE \"Appointment\" SET slot_id = ? WHERE appointment_id = ?"
			);
			updateApptStmt.setInt(1, newSlotId);
			updateApptStmt.setInt(2, appointmentId);
			updateApptStmt.executeUpdate();
			updateApptStmt.close();
			
			PreparedStatement freeOldSlotStmt = conn.prepareStatement(
				"UPDATE \"TimeSlots\" SET is_available = true WHERE slot_id = ?"
			);
			freeOldSlotStmt.setInt(1, oldSlotId);
			freeOldSlotStmt.executeUpdate();
			freeOldSlotStmt.close();
			
			PreparedStatement bookNewSlotStmt = conn.prepareStatement(
				"UPDATE \"TimeSlots\" SET is_available = false WHERE slot_id = ?"
			);
			bookNewSlotStmt.setInt(1, newSlotId);
			bookNewSlotStmt.executeUpdate();
			bookNewSlotStmt.close();
			
			String oldDetails = "Date: " + oldStartTime + "\nTime: " + oldStartTime + " - " + oldEndTime;
			String newDetails = "Date: " + newStartTime + "\nTime: " + newStartTime + " - " + newEndTime;
			
			notificationManager.sendModificationNotice(userEmail, oldDetails, newDetails);
			notificationManager.cancelReminder(appointmentId);
			
			System.out.println("Reservation modified successfully! Email sent to: " + userEmail);
			
		} catch (SQLException e) {
			System.out.println("Error modifying reservation: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Invalid input: " + e.getMessage());
		}
	}
	
	private static void userLogin(Scanner input) {
		Connection conn = DatabaseConnection.getConnection();
		if(conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		System.out.println("Enter username: ");
		String username = input.nextLine();
		
		System.out.println("Enter password: ");
		String password = input.nextLine();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM \"Users\" WHERE name = ? AND password = ?"
			);
			stmt.setString(1, username);
			stmt.setString(2, password);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				loggedInUserId = rs.getInt("user_id");
				loggedInUserName = rs.getString("name");
				System.out.println("Login Successful, Welcome " + loggedInUserName);
				userMenu(input);
			} else {
				System.out.println("Invalid username or password");
			}
			rs.close();
			stmt.close();
		} catch(SQLException e) {
			System.out.println("Error during login: " + e.getMessage());
		}
	}
	
	private static void userSignUp(Scanner input) {
		Connection conn = DatabaseConnection.getConnection();
		if(conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		System.out.println("Enter username: ");
		String username = input.nextLine();
		
		System.out.println("Enter Email: ");
		String userEmail = input.nextLine();
		
		System.out.println("Enter password: ");
		String password = input.nextLine();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO \"Users\" (name, email, password) VALUES (?, ?, ?)"
			);
			stmt.setString(1, username);
			stmt.setString(2, userEmail);
			stmt.setString(3, password);
			
			int rs = stmt.executeUpdate();
			
			if(rs > 0) {
				System.out.println("Sign up successful!");
			}
			stmt.close();
			
		} catch(SQLException e) {
			System.out.println("Error during Sign Up: " + e.getMessage());
		}
	}
	
	private static void userMenu(Scanner input) {
		while(true) {
			System.out.println("\nUser Menu - Welcome " + loggedInUserName);
			System.out.println("1- View Available Appointment Slots");
			System.out.println("2- Show booking rules");
			System.out.println("3- Book an Appointment");
			System.out.println("4- View My Appointment");
			System.out.println("5- Modify an appointment");
			System.out.println("6- Cancel an appointment");
			System.out.println("0- Logout");
			
			int choice = input.nextInt();
			input.nextLine();
			
			switch(choice) {
				case 1:
					viewAvailableSlots();
					break;
				case 2:
					showBookingRules();
					break;
				case 3:
					bookAppointment(input);
					break;
				case 4:
					viewMyAppointments();
					break;
				case 5:
					modifyAppointment(input);
					break;
				case 6:
					cancelAppointment(input);
					break;
				case 0:
					loggedInUserId = -1;
					loggedInUserName = "";
					System.out.println("Logged out successfully");
					return;
				default:
					System.out.println("Invalid option.");
			}
		}
	}
	
	private static void showBookingRules() {
		System.out.println("\nBooking Rules by Appointment Type:");
		System.out.println("URGENT      -> duration must be 30 minutes or less");
		System.out.println("FOLLOW_UP   -> duration must be 30 minutes or less");
		System.out.println("ASSESSMENT  -> duration must be at least 60 minutes");
		System.out.println("VIRTUAL     -> no physical location is required");
		System.out.println("IN_PERSON   -> physical location is required");
		System.out.println("INDIVIDUAL  -> participant count must be exactly 1");
		System.out.println("GROUP       -> participant count must be more than 1");
	}
	
	private static void viewAvailableSlots() {
		Connection conn = DatabaseConnection.getConnection();
		if(conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"TimeSlots\" WHERE is_available = 'true' AND start_datetime > NOW()");
			System.out.println("\n Available Appointment Slots:");
			boolean found = false;
			while (rs.next()) {
				found = true;
				System.out.println("ID: " + rs.getInt("slot_id") +
						" | Start: " + rs.getTimestamp("start_datetime") +
						" | End: " + rs.getTimestamp("end_datetime"));
			}
			if(!found) {
				System.out.println("No available slots found.");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error fetching slots: " + e.getMessage());
		}
	}
	
	private static void bookAppointment(Scanner input) {
		viewAvailableSlots();
		
		Connection conn = DatabaseConnection.getConnection();
		if(conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		System.out.println("Enter the slot ID you want to book: ");
		int slotID = input.nextInt();
		input.nextLine();
		
		System.out.println("Choose appointment type:");
		System.out.println("1- URGENT");
		System.out.println("2- FOLLOW_UP");
		System.out.println("3- ASSESSMENT");
		System.out.println("4- VIRTUAL");
		System.out.println("5- IN_PERSON");
		System.out.println("6- INDIVIDUAL");
		System.out.println("7- GROUP");
		
		int typeChoice = input.nextInt();
		input.nextLine();
		
		String appointmentType;
		
		switch (typeChoice) {
			case 1:
				appointmentType = "URGENT";
				break;
			case 2:
				appointmentType = "FOLLOW_UP";
				break;
			case 3:
				appointmentType = "ASSESSMENT";
				break;
			case 4:
				appointmentType = "VIRTUAL";
				break;
			case 5:
				appointmentType = "IN_PERSON";
				break;
			case 6:
				appointmentType = "INDIVIDUAL";
				break;
			case 7:
				appointmentType = "GROUP";
				break;
			default:
				System.out.println("Invalid appointment type.");
				return;
		}
		
		System.out.println("Enter duration in minutes: ");
		int durationMinutes = input.nextInt();
		input.nextLine();
		
		System.out.println("Enter number of participants: ");
		int participantCount = input.nextInt();
		input.nextLine();
		
		System.out.println("Enter location (leave empty for none): ");
		String location = input.nextLine();
		
		AppointmentRequest request = new AppointmentRequest(
				appointmentType,
				durationMinutes,
				participantCount,
				location
		);
		
		BookingRuleStrategy rule = BookingRuleFactory.getRule(appointmentType);
		if (rule != null && !rule.isValid(request)) {
			System.out.println("Booking failed: " + rule.getErrorMessage());
			return;
		}
		
		try {
			PreparedStatement checkStmt = conn.prepareStatement(
					"SELECT * FROM \"TimeSlots\" WHERE slot_id = ? AND is_available = true AND start_datetime > NOW()"
			);
			checkStmt.setInt(1, slotID);
			ResultSet rs = checkStmt.executeQuery();
			
			if(!rs.next()) {
				System.out.println("Slot not available, does not exist, or is in the past.");
				rs.close();
				checkStmt.close();
				return;
			}
			
			Timestamp startTime = rs.getTimestamp("start_datetime");
			Timestamp endTime = rs.getTimestamp("end_datetime");
			rs.close();
			checkStmt.close();
			
			PreparedStatement bookStmt = conn.prepareStatement(
					"INSERT INTO \"Appointment\" (user_id, slot_id, appointment_type) VALUES (?, ?, ?) RETURNING appointment_id"
			);
			bookStmt.setInt(1, loggedInUserId);
			bookStmt.setInt(2, slotID);
			bookStmt.setString(3, appointmentType);
			ResultSet bookRs = bookStmt.executeQuery();
			
			if (bookRs.next()) {
				int appointmentId = bookRs.getInt("appointment_id");
				bookRs.close();
				bookStmt.close();
				
				PreparedStatement updateStmt = conn.prepareStatement(
						"UPDATE \"TimeSlots\" SET is_available = false WHERE slot_id = ?"
				);
				updateStmt.setInt(1, slotID);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				PreparedStatement userStmt = conn.prepareStatement(
					"SELECT email FROM \"Users\" WHERE user_id = ?"
				);
				userStmt.setInt(1, loggedInUserId);
				ResultSet userRs = userStmt.executeQuery();
				
				if (userRs.next()) {
					String userEmail = userRs.getString("email");
					String appointmentDetails = "Appointment ID: " + appointmentId +
											   "\nType: " + appointmentType +
											   "\nDuration: " + durationMinutes + " minutes" +
											   "\nParticipants: " + participantCount +
											   "\nLocation: " + (location.isEmpty() ? "None" : location) +
											   "\nDate: " + startTime +
											   "\nTime: " + startTime + " - " + endTime;
					
					notificationManager.sendBookingConfirmation(userEmail, appointmentDetails);
					notificationManager.scheduleReminder(userEmail, appointmentId,
													   appointmentDetails, startTime.getTime());
					
					System.out.println("Appointment booked successfully! Email sent to: " + userEmail);
				}
				userRs.close();
				userStmt.close();
			}
			
		} catch(SQLException e) {
			System.out.println("Error booking appointment: " + e.getMessage());
		}
	}
	
	private static void viewMyAppointments() {
		Connection conn = DatabaseConnection.getConnection();
		if(conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		try {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT a.appointment_id, a.appointment_type, t.start_datetime, t.end_datetime " +
					"FROM \"Appointment\" a " +
					"JOIN \"TimeSlots\" t ON a.slot_id = t.slot_id " +
					"WHERE a.user_id = ?"
			);
			stmt.setInt(1, loggedInUserId);
			ResultSet rs = stmt.executeQuery();
			
			System.out.println("\nYour Appointments:");
			boolean found = false;
			while(rs.next()) {
				found = true;
				Timestamp start = rs.getTimestamp("start_datetime");
				String status = start.before(new Timestamp(System.currentTimeMillis())) ? "PAST" : "UPCOMING";
				
				System.out.println(
					"Appointment ID: " + rs.getInt("appointment_id") +
					" | Type: " + rs.getString("appointment_type") +
					" | Start: " + start +
					" | End: " + rs.getTimestamp("end_datetime") +
					" | Status: " + status
				);
			}
			if(!found) { 
				System.out.println("You have no appointments."); 
			}
			rs.close();
			stmt.close();
			
		} catch(SQLException e) {
			System.out.println("Error fetching appointments: " + e.getMessage());
		}
	}
	
	/**
	 * Allows a user to modify their upcoming appointment
	 * Only future appointments can be modified for security
	 */
	private static void modifyAppointment(Scanner input) {
		viewMyAppointments();
		
		Connection conn = DatabaseConnection.getConnection();
		if(conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		System.out.println("Enter the Appointment ID you want to modify: ");
		int appointmentId = input.nextInt();
		input.nextLine();
		
		try {
			// Get current appointment details and validate it's a future appointment
			PreparedStatement getApptStmt = conn.prepareStatement(
				"SELECT a.slot_id, u.email, u.name, t.start_datetime, t.end_datetime, a.appointment_type " +
				"FROM \"Appointment\" a " +
				"JOIN \"Users\" u ON a.user_id = u.user_id " +
				"JOIN \"TimeSlots\" t ON a.slot_id = t.slot_id " +
				"WHERE a.appointment_id = ? AND a.user_id = ? AND t.start_datetime > NOW()"
			);
			getApptStmt.setInt(1, appointmentId);
			getApptStmt.setInt(2, loggedInUserId);
			ResultSet apptRs = getApptStmt.executeQuery();
			
			if (!apptRs.next()) {
				System.out.println("Appointment not found, belongs to another user, or cannot be modified (past appointments cannot be changed).");
				apptRs.close();
				getApptStmt.close();
				return;
			}
			
			int oldSlotId = apptRs.getInt("slot_id");
			String userEmail = apptRs.getString("email");
			String userName = apptRs.getString("name");
			Timestamp oldStartTime = apptRs.getTimestamp("start_datetime");
			Timestamp oldEndTime = apptRs.getTimestamp("end_datetime");
			String oldAppointmentType = apptRs.getString("appointment_type");
			
			System.out.println("\nCurrent Appointment Details:");
			System.out.println("User: " + userName);
			System.out.println("Email: " + userEmail);
			System.out.println("Type: " + oldAppointmentType);
			System.out.println("Current Slot: " + oldStartTime + " to " + oldEndTime);
			apptRs.close();
			getApptStmt.close();
			
			System.out.println("\nAvailable Slots for Modification:");
			viewAvailableSlots();
			
			System.out.println("Enter the new Slot ID (or 0 to cancel): ");
			int newSlotId = input.nextInt();
			input.nextLine();
			
			if (newSlotId == 0) {
				System.out.println("Modification cancelled.");
				return;
			}
			
			// Validate the new slot
			PreparedStatement checkSlotStmt = conn.prepareStatement(
				"SELECT is_available, start_datetime, end_datetime FROM \"TimeSlots\" WHERE slot_id = ? AND start_datetime > NOW()"
			);
			checkSlotStmt.setInt(1, newSlotId);
			ResultSet slotRs = checkSlotStmt.executeQuery();
			
			if (!slotRs.next()) {
				System.out.println("Invalid slot ID, slot not available, or slot is in the past.");
				slotRs.close();
				checkSlotStmt.close();
				return;
			}
			
			boolean isAvailable = slotRs.getBoolean("is_available");
			Timestamp newStartTime = slotRs.getTimestamp("start_datetime");
			Timestamp newEndTime = slotRs.getTimestamp("end_datetime");
			slotRs.close();
			checkSlotStmt.close();
			
			if (!isAvailable) {
				System.out.println("Selected slot is not available.");
				return;
			}
			
			// Update the appointment
			PreparedStatement updateApptStmt = conn.prepareStatement(
				"UPDATE \"Appointment\" SET slot_id = ? WHERE appointment_id = ?"
			);
			updateApptStmt.setInt(1, newSlotId);
			updateApptStmt.setInt(2, appointmentId);
			int rowsUpdated = updateApptStmt.executeUpdate();
			updateApptStmt.close();
			
			if (rowsUpdated > 0) {
				// Make old slot available again
				PreparedStatement freeOldSlotStmt = conn.prepareStatement(
					"UPDATE \"TimeSlots\" SET is_available = true WHERE slot_id = ?"
				);
				freeOldSlotStmt.setInt(1, oldSlotId);
				freeOldSlotStmt.executeUpdate();
				freeOldSlotStmt.close();
				
				// Make new slot unavailable
				PreparedStatement bookNewSlotStmt = conn.prepareStatement(
					"UPDATE \"TimeSlots\" SET is_available = false WHERE slot_id = ?"
				);
				bookNewSlotStmt.setInt(1, newSlotId);
				bookNewSlotStmt.executeUpdate();
				bookNewSlotStmt.close();
				
				// Send notification and manage reminders
				String oldDetails = "Type: " + oldAppointmentType + 
								 "\nDate: " + oldStartTime + 
								 "\nTime: " + oldStartTime + " - " + oldEndTime;
				String newDetails = "Type: " + oldAppointmentType + 
								 "\nDate: " + newStartTime + 
								 "\nTime: " + newStartTime + " - " + newEndTime;
				
				notificationManager.sendModificationNotice(userEmail, oldDetails, newDetails);
				notificationManager.cancelReminder(appointmentId);
				
				// Schedule new reminder for the modified appointment
				String appointmentDetails = "Appointment ID: " + appointmentId + 
										   "\nType: " + oldAppointmentType +
										   "\nDate: " + newStartTime + 
										   "\nTime: " + newStartTime + " - " + newEndTime;
				notificationManager.scheduleReminder(userEmail, appointmentId, 
												   appointmentDetails, newStartTime.getTime());
				
				System.out.println("Appointment modified successfully! Email sent to: " + userEmail);
				System.out.println("New appointment time: " + newStartTime + " to " + newEndTime);
			} else {
				System.out.println("Failed to modify appointment.");
			}
			
		} catch (SQLException e) {
			System.out.println("Error modifying appointment: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Invalid input: " + e.getMessage());
		}
	}
	
	private static void cancelAppointment(Scanner input) {
		viewMyAppointments();
		
		Connection conn = DatabaseConnection.getConnection();
		if(conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		System.out.println("Enter the Appointment ID you want to cancel: ");
		int appointmentId = input.nextInt();
		input.nextLine();
		
		try {
			PreparedStatement getStmt = conn.prepareStatement(
					"SELECT a.slot_id, u.email, t.start_datetime, t.end_datetime " +
					"FROM \"Appointment\" a " +
					"JOIN \"Users\" u ON a.user_id = u.user_id " +
					"JOIN \"TimeSlots\" t ON a.slot_id = t.slot_id " +
					"WHERE a.appointment_id = ? AND a.user_id = ?"
			);
			getStmt.setInt(1, appointmentId);
			getStmt.setInt(2, loggedInUserId);
			ResultSet rs = getStmt.executeQuery();
			
			if (!rs.next()) {
	            System.out.println("Appointment not found or does not belong to you.");
	            rs.close();
	            getStmt.close();
	            return;
	        }
			
			int slotId = rs.getInt("slot_id");
			String userEmail = rs.getString("email");
			Timestamp startTime = rs.getTimestamp("start_datetime");
			Timestamp endTime = rs.getTimestamp("end_datetime");
			rs.close();
			getStmt.close();
			
			PreparedStatement deleteStmt = conn.prepareStatement(
		            "DELETE FROM \"Appointment\" WHERE appointment_id = ?"
		    );
			deleteStmt.setInt(1, appointmentId);
			deleteStmt.executeUpdate();
	        deleteStmt.close();
	        
	        PreparedStatement freeStmt = conn.prepareStatement(
	                "UPDATE \"TimeSlots\" SET is_available = true WHERE slot_id = ?"
	        );
	        freeStmt.setInt(1, slotId);
	        freeStmt.executeUpdate();
	        freeStmt.close();
	        
	        String appointmentDetails = "Appointment ID: " + appointmentId + 
	        						   "\nDate: " + startTime +
	        						   "\nTime: " + startTime + " - " + endTime;
	        
	        notificationManager.sendCancellationNotice(userEmail, appointmentDetails);
	        notificationManager.cancelReminder(appointmentId);
	        
	        System.out.println("Appointment cancelled successfully! Email sent to: " + userEmail);
	        
		} catch (SQLException e) {
	        System.out.println("Error cancelling appointment: " + e.getMessage());
	    }
	}
}
