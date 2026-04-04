import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.ArrayList;
import java.util.List;
public class Appoitment_Main_Page {
	
	static boolean isLoggedIn = false;
	static List<DaySchedule> weekSchedule = new ArrayList<>();
	
	public static void main(String[] args) {
		
		System.out.println("Welcome to Appointment Scheduling System");
		
		initializeSchedule();
		
		Scanner input = new Scanner(System.in);
		
		while(true) {
			
			if(!isLoggedIn) {
				int chosenNum = showMenu(input);
				
				if(chosenNum == 1) {
					adminLogin(input);
				}
				else if(chosenNum == 2) {
					viewAvailableSlots();
				}
				else if(chosenNum == 3) {
		            System.out.println("Program closed.");
		            input.close();
		            System.exit(0);
		        }
				else {
					System.out.println("Invalid option.");
				}
			}
			else {
				adminMenu(input);
			}
		}
	}
	
	//Initialize weekly slots
	public static void initializeSchedule() {

		String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
		
		for(String day : days) {
			DaySchedule schedule = new DaySchedule(day);
			
			schedule.addSlot(new TimeSlots("09:00"));
			schedule.addSlot(new TimeSlots("10:00"));
            schedule.addSlot(new TimeSlots("11:00"));
            schedule.addSlot(new TimeSlots("12:00"));
            
            weekSchedule.add(schedule);
		}
	}
	//Menu Function
	private static int showMenu(Scanner input) {
		System.out.println("Enter the number following what you want to do");
		System.out.println("1- Administrator Login ");
		System.out.println("2- View Available Appointment Slots");
		System.out.println("3- Exit Program ");
		
		int choice = input.nextInt();
		input.nextLine();
		return choice;
	}
	//Admin Login
	private static void adminLogin(Scanner input) {
		Dotenv dotenv = Dotenv.load();
		      
		      
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
        	adminLogout();
        }
        else {
        	System.out.println("Invalid option.");
        }
	}
	//Admin Logout
	private static void adminLogout() {
		isLoggedIn = false;
		System.out.println("You have been logged out successfully");
	}
	//View Available slots
	private static void viewAvailableSlots() {
		
		System.out.println("\nAvailable Appointment Slots:");
		
		for(DaySchedule schedule : weekSchedule) {
			System.out.println("\n" + schedule.getDay());
			
			for(TimeSlots slot : schedule.getSlots()) {
				if(!slot.isBooked()) {
					System.out.println(" - " + slot.getTime());
				}
			}
		}
	}
}