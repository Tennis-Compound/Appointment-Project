import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;

public class AppoitmentMainPageTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() throws Exception {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        setStaticField(Appoitment_Main_Page.class, "isLoggedIn", false);
        setStaticField(Appoitment_Main_Page.class, "loggedInUserId", -1);
        setStaticField(Appoitment_Main_Page.class, "loggedInUserName", "");

        NotificationManager mockNotificationManager = mock(NotificationManager.class);
        setStaticField(Appoitment_Main_Page.class, "notificationManager", mockNotificationManager);

        Field connectionField = DatabaseConnection.class.getDeclaredField("connection");
        connectionField.setAccessible(true);
        connectionField.set(null, null);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    private Object getStaticField(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    private Object invokePrivateStaticMethod(Class<?> clazz, String methodName,
                                              Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = clazz.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(null, args);
    }

    private Scanner scannerFrom(String input) {
        return new Scanner(new StringReader(input));
    }

    private Connection injectMockConnection() throws Exception {
        Connection mockConn = mock(Connection.class);
        Field connectionField = DatabaseConnection.class.getDeclaredField("connection");
        connectionField.setAccessible(true);
        connectionField.set(null, mockConn);
        return mockConn;
    }

    /**
     * Returns a Statement mock that produces an empty ResultSet.
     * Used to satisfy the viewAvailableSlots() call that many methods
     * trigger before doing their own work.
     */
    private Statement stubEmptySlots(Connection conn) throws Exception {
        Statement stmt = mock(Statement.class);
        ResultSet emptyRs = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(emptyRs);
        when(emptyRs.next()).thenReturn(false);
        return stmt;
    }

    // ── Menu / Rules ─────────────────────────────────────────────────────────

    @Test
    void testShowMenuReturnsChoice() throws Exception {
        Scanner scanner = scannerFrom("4\n");
        int choice = (int) invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "showMenu", new Class[]{Scanner.class}, scanner);
        assertEquals(4, choice);
        assertTrue(outContent.toString().contains("View Available Appointment Slots"));
    }

    @Test
    void testShowMenuAllOptionsPresent() throws Exception {
        Scanner scanner = scannerFrom("1\n");
        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "showMenu", new Class[]{Scanner.class}, scanner);
        String out = outContent.toString();
        assertTrue(out.contains("Administrator Login"));
        assertTrue(out.contains("User Login"));
        assertTrue(out.contains("User Sign up"));
        assertTrue(out.contains("Exit Program"));
    }

    @Test
    void testShowBookingRulesPrintsAllTypes() throws Exception {
        invokePrivateStaticMethod(Appoitment_Main_Page.class, "showBookingRules", new Class[]{});
        String output = outContent.toString();
        assertTrue(output.contains("URGENT"));
        assertTrue(output.contains("FOLLOW_UP"));
        assertTrue(output.contains("ASSESSMENT"));
        assertTrue(output.contains("VIRTUAL"));
        assertTrue(output.contains("IN_PERSON"));
        assertTrue(output.contains("INDIVIDUAL"));
        assertTrue(output.contains("GROUP"));
    }

    // ── Authentication ────────────────────────────────────────────────────────

    /**
     * FIX: After a successful login userLogin() immediately calls userMenu(),
     * which (a) calls viewMyAppointments() via prepareStatement and (b) reads a
     * menu choice from the scanner.  We must:
     *   1. Stub createStatement() so viewMyAppointments' PreparedStatement query
     *      returns an empty ResultSet (no infinite loop).
     *   2. Make rs.next() return true exactly once (for the login SELECT), then
     *      false for all subsequent calls (viewMyAppointments result set).
     *   3. Supply "0\n" at the end of the scanner input to choose "Logout" from
     *      the user menu, which exits the loop and returns control to the test.
     */
    @Test
    void testUserLoginSuccess() throws Exception {
        Connection conn = injectMockConnection();

        // First prepareStatement call: login SELECT
        // Second prepareStatement call: viewMyAppointments SELECT (entered from userMenu)
        PreparedStatement loginStmt = mock(PreparedStatement.class);
        PreparedStatement viewStmt  = mock(PreparedStatement.class);
        ResultSet loginRs = mock(ResultSet.class);
        ResultSet emptyRs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString()))
                .thenReturn(loginStmt)  // used by the login query
                .thenReturn(viewStmt);  // used by viewMyAppointments inside userMenu

        when(loginStmt.executeQuery()).thenReturn(loginRs);
        when(loginRs.next()).thenReturn(true);           // login row found
        when(loginRs.getInt("user_id")).thenReturn(123);
        when(loginRs.getString("name")).thenReturn("Ayham");

        when(viewStmt.executeQuery()).thenReturn(emptyRs);
        when(emptyRs.next()).thenReturn(false);          // no appointments → no loop

        // "4" → "View My Appointment" (calls viewMyAppointments, handled above)
        // "0" → Logout (resets loggedInUserId back to -1 — this is correct behaviour)
        // We assert on the printed welcome message, not on the field value after logout.
        Scanner scanner = scannerFrom("Ayham\npassword123\n4\n0\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "userLogin", new Class[]{Scanner.class}, scanner);

        // The welcome message was printed before userMenu ran, so it is always present.
        assertTrue(outContent.toString().contains("Welcome Ayham"));
        // After logout the field is correctly reset; verify the logout message too.
        assertTrue(outContent.toString().contains("Logged out successfully"));
    }

    @Test
    void testUserLoginFailure() throws Exception {
        Connection conn = injectMockConnection();
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false); // no matching user

        Scanner scanner = scannerFrom("unknown\nwrongpass\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "userLogin", new Class[]{Scanner.class}, scanner);

        assertEquals(-1, (int) getStaticField(Appoitment_Main_Page.class, "loggedInUserId"));
        assertTrue(outContent.toString().contains("Invalid username or password"));
    }

    @Test
    void testUserLoginNoDatabaseConnection() throws Exception {
        // connection left as null — injected nothing
        Scanner scanner = scannerFrom("user\npass\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "userLogin", new Class[]{Scanner.class}, scanner);

        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    @Test
    void testUserSignUpSuccess() throws Exception {
        Connection conn = injectMockConnection();
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(1);

        Scanner scanner = scannerFrom("NewUser\nuser@test.com\npass123\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "userSignUp", new Class[]{Scanner.class}, scanner);

        assertTrue(outContent.toString().contains("Sign up successful!"));
    }

    @Test
    void testUserSignUpNoDatabaseConnection() throws Exception {
        // connection left null
        Scanner scanner = scannerFrom("NewUser\nuser@test.com\npass123\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "userSignUp", new Class[]{Scanner.class}, scanner);

        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    @Test
    void testUserSignUpDatabaseError() throws Exception {
        Connection conn = injectMockConnection();
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenThrow(new SQLException("duplicate key"));

        Scanner scanner = scannerFrom("Dupe\ndup@test.com\npass\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "userSignUp", new Class[]{Scanner.class}, scanner);

        assertTrue(outContent.toString().contains("Error during Sign Up"));
    }

    // ── Booking ───────────────────────────────────────────────────────────────

    /**
     * FIX: bookAppointment() calls viewAvailableSlots() first, which uses
     * conn.createStatement() (not prepareStatement).  The original test only
     * mocked prepareStatement, so createStatement() returned null and threw an
     * NPE before the booking-rule logic was ever reached.
     *
     * Input "5\n1\n60\n1\nRoom1\n":
     *   5  → type choice IN_PERSON
     *   1  → slot ID
     *   60 → duration (IN_PERSON has no duration rule, but slot-ID is first)
     *
     * Wait — the method reads: slotID, typeChoice, duration, participants, location.
     * Input "1\n5\n60\n1\nRoom1\n":
     *   1   → slot ID
     *   5   → IN_PERSON (no rule violation by itself)
     *
     * To trigger a rule failure we use type 3 (ASSESSMENT, needs ≥60 min) with
     * only 30 minutes:  "1\n3\n30\n1\n\n"
     * ASSESSMENT rule: duration must be >= 60 → 30 fails → "Booking failed"
     */
    @Test
    void testBookAppointmentRuleFailure() throws Exception {
        Connection conn = injectMockConnection();
        stubEmptySlots(conn); // satisfies the viewAvailableSlots() call inside bookAppointment

        // slot=1, type=3 (ASSESSMENT), duration=30 (violates ≥60 rule), participants=1, location=""
        Scanner scanner = scannerFrom("1\n3\n30\n1\n\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "bookAppointment", new Class[]{Scanner.class}, scanner);

        assertTrue(outContent.toString().contains("Booking failed"));
    }

    @Test
    void testBookAppointmentUrgentRuleFailure() throws Exception {
        Connection conn = injectMockConnection();
        stubEmptySlots(conn);

        // slot=1, type=1 (URGENT), duration=60 (violates ≤30 rule), participants=1, location=""
        Scanner scanner = scannerFrom("1\n1\n60\n1\n\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "bookAppointment", new Class[]{Scanner.class}, scanner);

        assertTrue(outContent.toString().contains("Booking failed"));
    }

    @Test
    void testBookAppointmentSlotNotAvailable() throws Exception {
        Connection conn = injectMockConnection();
        stubEmptySlots(conn);

        // Slot availability check uses prepareStatement(SELECT)
        PreparedStatement checkStmt = mock(PreparedStatement.class);
        ResultSet checkRs = mock(ResultSet.class);
        when(conn.prepareStatement(anyString())).thenReturn(checkStmt);
        when(checkStmt.executeQuery()).thenReturn(checkRs);
        when(checkRs.next()).thenReturn(false); // slot not found / unavailable

        // type=4 VIRTUAL (no rule) so rule check passes, then DB check should fail
        Scanner scanner = scannerFrom("99\n4\n30\n1\n\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "bookAppointment", new Class[]{Scanner.class}, scanner);

        assertTrue(outContent.toString().contains("Slot not available"));
    }

    @Test
    void testBookAppointmentNoDatabaseConnection() throws Exception {
        // No connection injected — conn will be null
        // We still need viewAvailableSlots() to not NPE; but with null conn it
        // prints "Cannot connect to database." and returns from viewAvailableSlots,
        // then bookAppointment also gets null and returns early.
        Scanner scanner = scannerFrom("1\n4\n30\n1\n\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "bookAppointment", new Class[]{Scanner.class}, scanner);

        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    // ── Cancel Appointment ────────────────────────────────────────────────────

    @Test
    void testCancelAppointmentSuccess() throws Exception {
        setStaticField(Appoitment_Main_Page.class, "loggedInUserId", 1);
        Connection conn = injectMockConnection();

        // viewMyAppointments() is called first — use prepareStatement(anyString)
        // but we need to differentiate calls.  Use thenReturn chaining so the
        // first prepareStatement call (viewMyAppointments SELECT) returns a stmt
        // with empty RS, and subsequent ones are properly stubbed.
        PreparedStatement viewStmt = mock(PreparedStatement.class);
        ResultSet emptyRs = mock(ResultSet.class);
        when(emptyRs.next()).thenReturn(false);
        when(viewStmt.executeQuery()).thenReturn(emptyRs);

        PreparedStatement getStmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("slot_id")).thenReturn(10);
        when(rs.getString("email")).thenReturn("test@test.com");
        when(rs.getTimestamp(anyString())).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(getStmt.executeQuery()).thenReturn(rs);

        PreparedStatement deleteStmt = mock(PreparedStatement.class);
        PreparedStatement updateStmt = mock(PreparedStatement.class);

        // First prepareStatement → viewMyAppointments; subsequent ones → cancel logic
        when(conn.prepareStatement(anyString()))
                .thenReturn(viewStmt)   // viewMyAppointments
                .thenReturn(getStmt)    // SELECT for cancel details
                .thenReturn(deleteStmt) // DELETE appointment
                .thenReturn(updateStmt); // UPDATE slot

        Scanner scanner = scannerFrom("100\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "cancelAppointment", new Class[]{Scanner.class}, scanner);

        assertTrue(outContent.toString().contains("cancelled successfully"));
    }

    @Test
    void testCancelAppointmentNotFound() throws Exception {
        setStaticField(Appoitment_Main_Page.class, "loggedInUserId", 1);
        Connection conn = injectMockConnection();

        PreparedStatement viewStmt = mock(PreparedStatement.class);
        ResultSet emptyRs = mock(ResultSet.class);
        when(emptyRs.next()).thenReturn(false);
        when(viewStmt.executeQuery()).thenReturn(emptyRs);

        PreparedStatement getStmt = mock(PreparedStatement.class);
        ResultSet notFoundRs = mock(ResultSet.class);
        when(notFoundRs.next()).thenReturn(false); // appointment not found
        when(getStmt.executeQuery()).thenReturn(notFoundRs);

        when(conn.prepareStatement(anyString()))
                .thenReturn(viewStmt)
                .thenReturn(getStmt);

        Scanner scanner = scannerFrom("999\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "cancelAppointment", new Class[]{Scanner.class}, scanner);

        assertTrue(outContent.toString().contains("Appointment not found"));
    }

    @Test
    void testCancelAppointmentNoDatabaseConnection() throws Exception {
        setStaticField(Appoitment_Main_Page.class, "loggedInUserId", 1);
        // No connection injected

        Scanner scanner = scannerFrom("1\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "cancelAppointment", new Class[]{Scanner.class}, scanner);

        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    @Test
    void testAdminViewAllReservations() throws Exception {
        Connection conn = injectMockConnection();
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("appointment_id")).thenReturn(1);
        when(rs.getString("appointment_type")).thenReturn("URGENT");
        when(rs.getString("user_name")).thenReturn("AdminTest");
        when(rs.getString("email")).thenReturn("admin@test.com");
        when(rs.getTimestamp("start_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(rs.getTimestamp("end_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "adminViewAllReservations", new Class[]{});

        String out = outContent.toString();
        assertTrue(out.contains("=== All Reservations ==="));
        assertTrue(out.contains("AdminTest"));
    }

    @Test
    void testAdminViewAllReservationsEmpty() throws Exception {
        Connection conn = injectMockConnection();
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "adminViewAllReservations", new Class[]{});

        assertTrue(outContent.toString().contains("No reservations found."));
    }

    @Test
    void testAdminViewAllReservationsNoDatabaseConnection() throws Exception {
        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "adminViewAllReservations", new Class[]{});

        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    // ── Notifications ─────────────────────────────────────────────────────────

    @Test
    void testTestNotificationsWithCustomEmail() throws Exception {
        NotificationManager mockManager = mock(NotificationManager.class);
        setStaticField(Appoitment_Main_Page.class, "notificationManager", mockManager);

        Scanner scanner = scannerFrom("user@test.com\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "testNotifications", new Class[]{Scanner.class}, scanner);

        verify(mockManager).sendBookingConfirmation(eq("user@test.com"), contains("test"));
        verify(mockManager).sendCancellationNotice(eq("user@test.com"), anyString());
    }

    @Test
    void testTestNotificationsDefaultEmail() throws Exception {
        NotificationManager mockManager = mock(NotificationManager.class);
        setStaticField(Appoitment_Main_Page.class, "notificationManager", mockManager);

        // Empty line → should fall back to demo@example.com
        Scanner scanner = scannerFrom("\n");

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "testNotifications", new Class[]{Scanner.class}, scanner);

        verify(mockManager).sendBookingConfirmation(eq("demo@example.com"), anyString());
        verify(mockManager).sendCancellationNotice(eq("demo@example.com"), anyString());
    }

    // ── View Available Slots ──────────────────────────────────────────────────

    @Test
    void testViewAvailableSlotsWhenNoSlotsFound() throws Exception {
        Connection conn = injectMockConnection();
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "viewAvailableSlots", new Class[]{});

        assertTrue(outContent.toString().contains("No available slots found."));
    }

    @Test
    void testViewAvailableSlotsShowsResults() throws Exception {
        Connection conn = injectMockConnection();
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("slot_id")).thenReturn(7);
        when(rs.getTimestamp("start_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(rs.getTimestamp("end_datetime")).thenReturn(new Timestamp(System.currentTimeMillis() + 3600000));

        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "viewAvailableSlots", new Class[]{});

        assertTrue(outContent.toString().contains("ID: 7"));
    }

    @Test
    void testViewAvailableSlotsNoDatabaseConnection() throws Exception {
        // No connection injected
        invokePrivateStaticMethod(
                Appoitment_Main_Page.class, "viewAvailableSlots", new Class[]{});

        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    // ── Database ──────────────────────────────────────────────────────────────

    @Test
    void testDatabaseConnectionClose() throws Exception {
        Connection mockConn = mock(Connection.class);
        Field field = DatabaseConnection.class.getDeclaredField("connection");
        field.setAccessible(true);
        field.set(null, mockConn);

        DatabaseConnection.closeConnection();

        verify(mockConn).close();
        assertNull(field.get(null));
    }

    @Test
    void testDatabaseConnectionCloseWhenNull() throws Exception {
        // Should not throw even if connection is already null
        Field field = DatabaseConnection.class.getDeclaredField("connection");
        field.setAccessible(true);
        field.set(null, null);

        assertDoesNotThrow(() -> DatabaseConnection.closeConnection());
    }
}
