import java.util.Scanner;
public class Appoitment_Main_Page {
	public static void main(String[] args) {
		System.out.println("Welcome to Appointment Scheduling System");
		
		
		Scanner input = new Scanner(System.in);
		int chosenNum = showMenu(input);

		if(chosenNum == 1) {
			adminLogin(input);
			
		}
		input.close();
	}
	//Menu Function
	private static int showMenu(Scanner input) {
		System.out.println("Enter the number following what you want to do");
		System.out.println("1- Administrator Login ");
		
		int choice = input.nextInt();
		input.nextLine();
		return choice;
	}
	
	private static void adminLogin(Scanner input) {
		System.out.println("Enter username: ");
        String username = input.nextLine();
        
        System.out.println("Enter password: ");
        String password = input.nextLine();
        
        String adminUsername = "admin1";
        String adminPassword = "12345";
        if(username.equals(adminUsername) && password.equals(adminPassword)) {
        	System.out.println("Login Successful");
        }else {
        	System.out.println("Invalid Credentials");
        }
	}
}