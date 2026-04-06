
import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public class Appoitment_Main_Page {
	
	static boolean isLoggedIn = false;
	private static int loggedInUserId = -1;
	private static String loggedInUserName = "";
	
	public static void main(String[] args) {
		
		System.out.println("Welcome to Appointment Scheduling System");
		
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

	//Menu Function
	private static int showMenu(Scanner input) {
		System.out.println("Enter the number following what you want to do");
		System.out.println("1- Administrator Login ");
		System.out.println("2- User Login");
		System.out.println("3- User Sign up");
		System.out.println("4- View Available Appointment Slots");
        System.out.println("0- Exit Program ");
        
		int choice = input.nextInt();
		input.nextLine();
		return choice;
	}
	
	//Admin Login
	private static void adminLogin(Scanner input) {
		Dotenv dotenv = Dotenv.configure()
		        .directory("C:\\Programming\\JAVA\\maven")
		        .load();
		
		String adminUsername = dotenv.get("adminName");
		String adminPassword = dotenv.get("adminpassword");
		
		System.out.println("Enter username: ");
        String username = input.nextLine();
        
        System.out.println("Enter password: ");
        String password = input.nextLine();
        
        if(username.equals(adminUsername) && password.equals(adminPassword)) {
        	System.out.println("Login Successful");
        	isLoggedIn = true;
        }else {
        	System.out.println("Invalid Credentials");
        }
	}
	
	//Admin Menu
	private static void adminMenu(Scanner input) {
		System.out.println("\nAdministrator Menu");
		System.out.println("1- Logout");
		System.out.println("2- View All Reservations");
		System.out.println("3- Cancel a Reservation");
		System.out.println("4- Modify a Reservation");
	
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
			default:
				System.out.println("Invalid option.");
		}
	}
	
	//View All Reservations (Administrator)
	private static void adminViewAllReservations() {
		Connection conn = DatabaseConnection.getConnection();
		if (conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		try {
			PreparedStatement stmt = conn.prepareStatement(
				"SELECT a.appointment_id, u.name AS user_name, t.start_datetime, t.end_datetime " +
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
					" | User: " + rs.getString("user_name") +
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
	
	//Cancel Reservation (Administrator)
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
			// First get the slot ID before deleting
			PreparedStatement getStmt = conn.prepareStatement(
				"SELECT slot_id FROM \"Appointment\" WHERE appointment_id = ?"
			);
			getStmt.setInt(1, appointmentId);
			ResultSet rs = getStmt.executeQuery();
			
			if (!rs.next()) {
				System.out.println("Appointment not found.");
				rs.close();
				getStmt.close();
				return;
			}
			int slotId = rs.getInt("slot_id");
			rs.close();
			getStmt.close();
			
			// Delete the appointment
			PreparedStatement deleteStmt = conn.prepareStatement(
				"DELETE FROM \"Appointment\" WHERE appointment_id = ?"
			);
			deleteStmt.setInt(1, appointmentId);
			int rowsAffected = deleteStmt.executeUpdate();
			deleteStmt.close();
			
			if (rowsAffected > 0) {
				// Make the time slot available again
				PreparedStatement updateStmt = conn.prepareStatement(
					"UPDATE \"TimeSlots\" SET is_available = true WHERE slot_id = ?"
				);
				updateStmt.setInt(1, slotId);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				System.out.println("Reservation cancelled successfully!");
			} else {
				System.out.println("Failed to cancel reservation.");
			}
		} catch (SQLException e) {
			System.out.println("Error cancelling reservation: " + e.getMessage());
		}
	}
	
	//Modify Reservation (Administrator)
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
			// Get current appointment details
			PreparedStatement getApptStmt = conn.prepareStatement(
				"SELECT a.slot_id, u.name AS user_name, t.start_datetime, t.end_datetime " +
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
			System.out.println("\nCurrent Appointment Details:");
			System.out.println("User: " + apptRs.getString("user_name"));
			System.out.println("Current Slot: " + apptRs.getTimestamp("start_datetime") + 
							  " to " + apptRs.getTimestamp("end_datetime"));
			apptRs.close();
			getApptStmt.close();
			
			// Show available slots for modification
			System.out.println("\nAvailable Slots for Modification:");
			viewAvailableSlots();
			
			System.out.println("Enter the new Slot ID (or 0 to cancel): ");
			int newSlotId = input.nextInt();
			input.nextLine();
			
			if (newSlotId == 0) {
				System.out.println("Modification cancelled.");
				return;
			}
			
			// Check if new slot is available
			PreparedStatement checkSlotStmt = conn.prepareStatement(
				"SELECT is_available FROM \"TimeSlots\" WHERE slot_id = ?"
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
			slotRs.close();
			checkSlotStmt.close();
			
			if (!isAvailable) {
				System.out.println("Selected slot is not available.");
				return;
			}
			
			// Update the appointment with new slot
			PreparedStatement updateApptStmt = conn.prepareStatement(
				"UPDATE \"Appointment\" SET slot_id = ? WHERE appointment_id = ?"
			);
			updateApptStmt.setInt(1, newSlotId);
			updateApptStmt.setInt(2, appointmentId);
			updateApptStmt.executeUpdate();
			updateApptStmt.close();
			
			// Update slot availability
			// Make old slot available
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
			
			System.out.println("Reservation modified successfully!");
			
		} catch (SQLException e) {
			System.out.println("Error modifying reservation: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Invalid input: " + e.getMessage());
		}
	}
	
	//User Login
	private static void userLogin(Scanner input) {
		Connection conn = DatabaseConnection.getConnection();
		if(conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		System.out.println("Enter username: ");
		String username = input.nextLine();
		
		System.out.println("Enter password");
		String password = input.nextLine();
		
		try {
			java.sql.PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM \"Users\" WHERE name = ARRAY[?] AND password = ARRAY[?]"
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
	
	//User Sign Up
	private static void userSignUp(Scanner input) {
		Connection conn = DatabaseConnection.getConnection();
		if(conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		System.out.println("Enter username: ");
		String username = input.nextLine();
		
		System.out.println("Enter Email");
		String userEmail = input.nextLine();
		
		System.out.println("Enter password");
		String password = input.nextLine();
		
		try {
			java.sql.PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO \"Users\" (name, email, password) VALUES (ARRAY[?], ARRAY[?], ARRAY[?]);"
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
	
	//User Menu
	private static void userMenu(Scanner input) {
		while(true) {
			System.out.println("\nUser Menu - Welcome " + loggedInUserName);
			System.out.println("1- View Available Appointment Slots");
			System.out.println("2- Show booking rules");
			System.out.println("3- Book an Appointment");
			System.out.println("4- View My Appointment");
	        System.out.println("5- Cancel an appointment");
			System.out.println("0- Logout");
			
			int choice = input.nextInt();
			input.nextLine();
			
			switch(choice) {
				case 1:
					viewAvailableSlots();
					break;
				case 2:
					System.out.println("add");
					break;
				case 3:
					bookAppointment(input);
					break;
				case 4:
					viewMyAppointments();
					break;
				case 5:
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
	
	//Viewing Available Slots
	private static void viewAvailableSlots() {
		Connection conn = DatabaseConnection.getConnection();
		if(conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"TimeSlots\" WHERE is_available = 'true'");
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
	
	//booking an Appointment
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
		
		try {
			java.sql.PreparedStatement checkStmt = conn.prepareStatement(
					"SELECT * FROM \"TimeSlots\" WHERE slot_id = ? AND is_available = true"
			);
			checkStmt.setInt(1, slotID);
			ResultSet rs = checkStmt.executeQuery();
			
			if(!rs.next()) {
				System.out.println("Slot not available or does not exist.");
				rs.close();
				checkStmt.close();
				return;
			}
			rs.close();
			checkStmt.close();
			
			java.sql.PreparedStatement bookStmt = conn.prepareStatement(
					"INSERT INTO \"Appointment\" (user_id, slot_id)\r\n VALUES (?, ?);"
			);
			bookStmt.setInt(1, loggedInUserId);
			bookStmt.setInt(2, slotID);
			bookStmt.executeUpdate();
			bookStmt.close();
			
			java.sql.PreparedStatement updateStmt = conn.prepareStatement(
					"UPDATE \"TimeSlots\" SET is_available = false WHERE slot_id = ?"
			);
			updateStmt.setInt(1, slotID);
			updateStmt.executeUpdate();
			updateStmt.close();
			System.out.println("Appointment booked successfully");
			
		} catch(SQLException e) {
			System.out.println("Error booking appointment: " + e.getMessage());
		}
	}
	
	//View My Appointments
	private static void viewMyAppointments() {
		Connection conn = DatabaseConnection.getConnection();
		if(conn == null) {
			System.out.println("Cannot connect to database.");
			return;
		}
		
		try {
			java.sql.PreparedStatement stmt = conn.prepareStatement(
					"SELECT a.appointment_id, t.start_datetime, t.end_datetime " +
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
				System.out.println(
					"Appointment ID: " + rs.getInt("appointment_id") +
					" | Start: " + rs.getTimestamp("start_datetime") +
					" | End: " + rs.getTimestamp("end_datetime")
				);
				
			}
			if(!found) { System.out.println("You have no appointments."); }
			rs.close();
			stmt.close();
			
		} catch(SQLException e) {
			System.out.println("Error fetching appointments: " + e.getMessage());
		}
	}
	
	//Cancel Appointment
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
			java.sql.PreparedStatement getStmt = conn.prepareStatement(
					"SELECT slot_id FROM \"Appointment\" WHERE appointment_id = ? AND user_id = ?"
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
			rs.close();
			getStmt.close();
			java.sql.PreparedStatement deleteStmt = conn.prepareStatement(
		            "DELETE FROM \"Appointment\" WHERE appointment_id = ?"
		    );
			
			deleteStmt.setInt(1, appointmentId);
			deleteStmt.executeUpdate();
	        deleteStmt.close();
	        java.sql.PreparedStatement freeStmt = conn.prepareStatement(
	                "UPDATE \"TimeSlots\" SET is_available = true WHERE slot_id = ?"
	        );
	        freeStmt.setInt(1, slotId);
	        freeStmt.executeUpdate();
	        freeStmt.close();
	        System.out.println("Appointment cancelled successfully!");
	        
		} catch (SQLException e) {
	        System.out.println("Error cancelling appointment: " + e.getMessage());
	    }
	}
}
