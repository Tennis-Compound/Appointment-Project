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
        
        int choice = input.nextInt();
        input.nextLine();
        
        if(choice == 1) {
        	isLoggedIn = false;
    		System.out.println("You have been logged out successfully");
        }
        else {
        	System.out.println("Invalid option.");
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