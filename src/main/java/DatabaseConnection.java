import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static Connection connection = null;
	
	private static final String DB_URL = "jdbc:postgresql://localhost:5432/appointment_db";
	private static final String DB_USER = "your_db_username";
	private static final String DB_PASSWORD = "your_db_password";
	
	public static Connection getConnection() {
		if(connection == null) {
			try {
				connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				System.out.println("Connected to database successfully");
			} catch (SQLException e) {
				System.out.println("Database connection failed: " + e.getMessage());
			}
		}
		return connection;
	}
	
	public static void closeConnection() {
		if(connection != null) {
			try {
				connection.close();
				connection = null;
				System.out.println("Database connection closed.");
			} catch (SQLException e) {
				System.out.println("Error closing connection: " + e.getMessage());
			}
		}
	}
}
