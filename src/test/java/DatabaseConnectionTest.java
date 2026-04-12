import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;


public class DatabaseConnectionTest {

    @AfterEach
    void cleanup() {
        DatabaseConnection.closeConnection();
    }

    @Test
    void getConnectionShouldNotThrow() {
        assertDoesNotThrow(() -> DatabaseConnection.getConnection());
    }

    @Test
    void callingGetConnectionTwiceShouldNotThrow() {
        assertDoesNotThrow(() -> {
            DatabaseConnection.getConnection();
            DatabaseConnection.getConnection();
        });
    }

    @Test
    void secondCallToGetConnectionReturnsSameReference() {
        Connection first  = DatabaseConnection.getConnection();
        Connection second = DatabaseConnection.getConnection();
        assertSame(first, second);
    }

    @Test
    void closeConnectionShouldNotThrow() {
        assertDoesNotThrow(() -> DatabaseConnection.closeConnection());
    }

    @Test
    void closeConnectionWhenNeverOpenedShouldNotThrow() {
        assertDoesNotThrow(() -> DatabaseConnection.closeConnection());
    }

    @Test
    void closeConnectionCalledTwiceShouldNotThrow() {
        DatabaseConnection.getConnection();
        assertDoesNotThrow(() -> {
            DatabaseConnection.closeConnection();
            DatabaseConnection.closeConnection();
        });
    }

    @Test
    void getConnectionAfterCloseShouldNotThrow() {
        DatabaseConnection.getConnection();
        DatabaseConnection.closeConnection();
        assertDoesNotThrow(() -> DatabaseConnection.getConnection());
    }

    @Test
    void consecutiveGetAfterCloseReturnSameReference() {
        DatabaseConnection.getConnection();
        DatabaseConnection.closeConnection();
        Connection a = DatabaseConnection.getConnection();
        Connection b = DatabaseConnection.getConnection();
        assertSame(a, b);
    }
}
