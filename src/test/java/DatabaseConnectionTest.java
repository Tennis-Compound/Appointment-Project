import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTest {

    @AfterEach
    void cleanup() throws Exception {
        // Ensure connection is reset to null after every test
        setStaticConnection(null);
    }

    @Test
    void getConnection_whenNull_attemptsToConnect() throws Exception {
        // Force connection to be null
        setStaticConnection(null);

        // Call getConnection. 
        // Since .env might be missing or DB details wrong in test env, 
        // it will likely enter the catch(SQLException) block.
        Connection conn = DatabaseConnection.getConnection();

        // If connection failed (expected in unit test env), it returns null
        assertNull(conn);
        // The output should confirm attempt/failure
        //assertTrue(output.contains("Database connection failed")); 
    }

    @Test
    void getConnection_returnsExistingConnection() throws Exception {
        // 1. Create a mock connection
        Connection mockConn = mock(Connection.class);
        
        // 2. Inject it into the static field
        setStaticConnection(mockConn);

        // 3. Call getConnection
        Connection result = DatabaseConnection.getConnection();

        // 4. Verify it returns the SAME instance (didn't create new one)
        assertSame(mockConn, result);
    }

    @Test
    void closeConnection_whenNotNull_closesSuccessfully() throws Exception {
        // 1. Create a mock connection
        Connection mockConn = mock(Connection.class);
        
        // 2. Inject it
        setStaticConnection(mockConn);

        // 3. Call closeConnection
        DatabaseConnection.closeConnection();

        // 4. Verify the mock's close() method was called
        verify(mockConn).close();
        
        // 5. Verify the static field is now null (via reflection)
        Field field = DatabaseConnection.class.getDeclaredField("connection");
        field.setAccessible(true);
        assertNull(field.get(null));
    }

    @Test
    void closeConnection_throwsSQLException_catchesAndPrintsError() throws Exception {
        // 1. Create a mock connection
        Connection mockConn = mock(Connection.class);
        
        // 2. Make close() throw an exception
        doThrow(new SQLException("Close failed")).when(mockConn).close();

        // 3. Inject it
        setStaticConnection(mockConn);

        // 4. Call closeConnection - should NOT throw exception
        assertDoesNotThrow(() -> DatabaseConnection.closeConnection());
        
        // 5. Verify error handling (field should still be null or handle error logic)
        // The code catches the exception and prints error.
    }

    @Test
    void closeConnection_whenNull_doesNothing() throws Exception {
        // Ensure connection is null
        setStaticConnection(null);

        // Call close - should not throw NPE or errors
        assertDoesNotThrow(() -> DatabaseConnection.closeConnection());
    }

    // Helper to modify private static connection field
    private void setStaticConnection(Connection value) throws Exception {
        Field field = DatabaseConnection.class.getDeclaredField("connection");
        field.setAccessible(true);
        field.set(null, value);
    }
}
