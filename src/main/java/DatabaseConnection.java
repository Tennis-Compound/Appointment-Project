import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static Connection connection = null;
	
	public static Connection getConnection() {

		if(connection == null) {
			try {
				Dotenv dotenv = Dotenv.configure()
						.directory(".")
						.load();
				String url = dotenv.get("DB_URL");
				String user = dotenv.get("DB_USER");
				String password = dotenv.get("DB_PASSWORD");
				
				connection = DriverManager.getConnection(url, user, password);
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