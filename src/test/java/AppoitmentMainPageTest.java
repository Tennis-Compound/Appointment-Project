import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
    private final InputStream originalIn  = System.in;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() throws Exception {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        setStaticField(Appoitment_Main_Page.class, "isLoggedIn", false);
        setStaticField(Appoitment_Main_Page.class, "loggedInUserId", -1);
        setStaticField(Appoitment_Main_Page.class, "loggedInUserName", "");

        NotificationManager mockNM = mock(NotificationManager.class);
        setStaticField(Appoitment_Main_Page.class, "notificationManager", mockNM);

        Field connField = DatabaseConnection.class.getDeclaredField("connection");
        connField.setAccessible(true);
        connField.set(null, null);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setStaticField(Class<?> clazz, String name, Object value) throws Exception {
        Field f = clazz.getDeclaredField(name);
        f.setAccessible(true);
        f.set(null, value);
    }

    private Object getStaticField(Class<?> clazz, String name) throws Exception {
        Field f = clazz.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(null);
    }

    private Object invoke(String methodName, Class<?>[] types, Object... args) throws Exception {
        Method m = Appoitment_Main_Page.class.getDeclaredMethod(methodName, types);
        m.setAccessible(true);
        return m.invoke(null, args);
    }

    private Scanner sc(String s) { return new Scanner(new StringReader(s)); }

    private Connection mockConn() throws Exception {
        Connection c = mock(Connection.class);
        Field f = DatabaseConnection.class.getDeclaredField("connection");
        f.setAccessible(true);
        f.set(null, c);
        return c;
    }

    /** Stubs conn.createStatement() to return an empty ResultSet (for viewAvailableSlots). */
    private void stubEmptySlots(Connection conn) throws Exception {
        Statement stmt = mock(Statement.class);
        ResultSet rs   = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(false);
    }

    /** Stubs the first prepareStatement call to return an empty ResultSet. */
    private PreparedStatement stubEmptyPrepared(Connection conn) throws Exception {
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false);
        when(ps.executeQuery()).thenReturn(rs);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        return ps;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // main() — covers the entire while-loop and initializeNotificationService()
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testMainExitsOnZero() throws Exception {
        System.setIn(new java.io.ByteArrayInputStream("0\n".getBytes()));
        assertDoesNotThrow(() -> Appoitment_Main_Page.main(new String[]{}));
        assertTrue(outContent.toString().contains("Exiting system. Goodbye!"));
    }

    @Test
    void testMainViewSlotsAndExit() throws Exception {
        // case 4 then exit
        System.setIn(new java.io.ByteArrayInputStream("4\n0\n".getBytes()));
        assertDoesNotThrow(() -> Appoitment_Main_Page.main(new String[]{}));
        assertTrue(outContent.toString().contains("Exiting system. Goodbye!"));
    }

    @Test
    void testMainInvalidChoiceThenExit() throws Exception {
        // default branch then exit
        System.setIn(new java.io.ByteArrayInputStream("99\n0\n".getBytes()));
        assertDoesNotThrow(() -> Appoitment_Main_Page.main(new String[]{}));
        assertTrue(outContent.toString().contains("Invalid choice. Try again."));
    }

    @Test
    void testMainAdminLoginFailureThenExit() throws Exception {
        // case 1: adminLogin — .env likely missing so it throws, falls to catch, then exit
        System.setIn(new java.io.ByteArrayInputStream("1\nwrong\nwrong\n0\n".getBytes()));
        assertDoesNotThrow(() -> Appoitment_Main_Page.main(new String[]{}));
    }

       @Test
    void testMainUserLoginNoDatabaseThenExit() throws Exception {
        // Case 2: userLogin with no DB -> returns immediately -> then 0 to exit
        // We do NOT provide user/pass because the code returns BEFORE reading them
        provideInput("2\n0\n");
        
        assertDoesNotThrow(() -> Appoitment_Main_Page.main(new String[]{}));
        assertTrue(outContent.toString().contains("Cannot connect"));
        assertTrue(outContent.toString().contains("Exiting system"));
    }

    @Test
    void testMainUserSignUpNoDatabaseThenExit() throws Exception {
        // Case 3: userSignUp with no DB -> returns immediately -> then 0 to exit
        // We do NOT provide user/email/pass because the code returns BEFORE reading them
        provideInput("3\n0\n");
                
                assertDoesNotThrow(() -> Appoitment_Main_Page.main(new String[]{}));
                assertTrue(outContent.toString().contains("Cannot connect"));
                assertTrue(outContent.toString().contains("Exiting system"));
            }
        
        
            // ══════════════════════════════════════════════════════════════════════════
            // initializeNotificationService() branches
            // ══════════════════════════════════════════════════════════════════════════
        
            private void provideInput(String string) {
                System.setIn(new java.io.ByteArrayInputStream(string.getBytes()));
            }
        
            @Test
    void testInitializeWithMockFlag() throws Exception {
        setStaticField(Appoitment_Main_Page.class, "useMockNotifications", true);
        invoke("initializeNotificationService", new Class[]{});
        assertTrue(outContent.toString().contains("mock notification service"));
        setStaticField(Appoitment_Main_Page.class, "useMockNotifications", false);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // showMenu / showBookingRules
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testShowMenuReturnsChoice() throws Exception {
        int choice = (int) invoke("showMenu", new Class[]{Scanner.class}, sc("4\n"));
        assertEquals(4, choice);
        assertTrue(outContent.toString().contains("View Available Appointment Slots"));
    }

    @Test
    void testShowMenuAllOptionsPresent() throws Exception {
        invoke("showMenu", new Class[]{Scanner.class}, sc("1\n"));
        String out = outContent.toString();
        assertTrue(out.contains("Administrator Login"));
        assertTrue(out.contains("User Login"));
        assertTrue(out.contains("User Sign up"));
        assertTrue(out.contains("Exit Program"));
    }

    @Test
    void testShowBookingRulesPrintsAllTypes() throws Exception {
        invoke("showBookingRules", new Class[]{});
        String out = outContent.toString();
        assertTrue(out.contains("URGENT"));
        assertTrue(out.contains("FOLLOW_UP"));
        assertTrue(out.contains("ASSESSMENT"));
        assertTrue(out.contains("VIRTUAL"));
        assertTrue(out.contains("IN_PERSON"));
        assertTrue(out.contains("INDIVIDUAL"));
        assertTrue(out.contains("GROUP"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // adminLogin
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testAdminLoginInvalidCredentials() throws Exception {
        // .env likely missing in test env → exception caught internally,
        // or credentials don't match → "Invalid Credentials"
        assertDoesNotThrow(() ->
            invoke("adminLogin", new Class[]{Scanner.class}, sc("wrong\nwrong\n")));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // adminMenu branches
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testAdminMenuLogout() throws Exception {
        setStaticField(Appoitment_Main_Page.class, "isLoggedIn", true);
        invoke("adminMenu", new Class[]{Scanner.class}, sc("1\n"));
        assertFalse((boolean) getStaticField(Appoitment_Main_Page.class, "isLoggedIn"));
        assertTrue(outContent.toString().contains("logged out successfully"));
    }

    @Test
    void testAdminMenuViewReservationsNoDB() throws Exception {
        // case 2 with no DB
        invoke("adminMenu", new Class[]{Scanner.class}, sc("2\n"));
        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    @Test
    void testAdminMenuInvalidOption() throws Exception {
        invoke("adminMenu", new Class[]{Scanner.class}, sc("99\n"));
        assertTrue(outContent.toString().contains("Invalid option."));
    }

    @Test
    void testAdminMenuTestNotifications() throws Exception {
        NotificationManager mockNM = mock(NotificationManager.class);
        setStaticField(Appoitment_Main_Page.class, "notificationManager", mockNM);
        // case 5 → testNotifications with empty email → demo@example.com
        invoke("adminMenu", new Class[]{Scanner.class}, sc("5\n\n"));
        verify(mockNM).sendBookingConfirmation(eq("demo@example.com"), anyString());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // userLogin
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testUserLoginSuccess() throws Exception {
        Connection conn = mockConn();
        PreparedStatement loginStmt = mock(PreparedStatement.class);
        PreparedStatement viewStmt  = mock(PreparedStatement.class);
        ResultSet loginRs = mock(ResultSet.class);
        ResultSet emptyRs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(loginStmt).thenReturn(viewStmt);
        when(loginStmt.executeQuery()).thenReturn(loginRs);
        when(loginRs.next()).thenReturn(true);
        when(loginRs.getInt("user_id")).thenReturn(1);
        when(loginRs.getString("name")).thenReturn("Ayham");
        when(viewStmt.executeQuery()).thenReturn(emptyRs);
        when(emptyRs.next()).thenReturn(false);

        invoke("userLogin", new Class[]{Scanner.class}, sc("Ayham\npass\n4\n0\n"));
        assertTrue(outContent.toString().contains("Welcome Ayham"));
        assertTrue(outContent.toString().contains("Logged out successfully"));
    }

    @Test
    void testUserLoginFailure() throws Exception {
        Connection conn = mockConn();
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        invoke("userLogin", new Class[]{Scanner.class}, sc("user\nwrongpass\n"));
        assertTrue(outContent.toString().contains("Invalid username or password"));
    }

    @Test
    void testUserLoginNoDatabaseConnection() throws Exception {
        invoke("userLogin", new Class[]{Scanner.class}, sc("user\npass\n"));
        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // userSignUp
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testUserSignUpSuccess() throws Exception {
        Connection conn = mockConn();
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(1);

        invoke("userSignUp", new Class[]{Scanner.class}, sc("NewUser\nuser@test.com\npass123\n"));
        assertTrue(outContent.toString().contains("Sign up successful!"));
    }

    @Test
    void testUserSignUpNoDatabaseConnection() throws Exception {
        invoke("userSignUp", new Class[]{Scanner.class}, sc("User\nemail\npass\n"));
        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    @Test
    void testUserSignUpDatabaseError() throws Exception {
        Connection conn = mockConn();
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenThrow(new SQLException("duplicate"));

        invoke("userSignUp", new Class[]{Scanner.class}, sc("Dupe\ndup@test.com\npass\n"));
        assertTrue(outContent.toString().contains("Error during Sign Up"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // viewAvailableSlots
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testViewAvailableSlotsNoDatabaseConnection() throws Exception {
        invoke("viewAvailableSlots", new Class[]{});
        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    @Test
    void testViewAvailableSlotsNoResults() throws Exception {
        Connection conn = mockConn();
        Statement stmt = mock(Statement.class);
        ResultSet rs   = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        invoke("viewAvailableSlots", new Class[]{});
        assertTrue(outContent.toString().contains("No available slots found."));
    }

    @Test
    void testViewAvailableSlotsShowsResults() throws Exception {
        Connection conn = mockConn();
        Statement stmt = mock(Statement.class);
        ResultSet rs   = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("slot_id")).thenReturn(7);
        when(rs.getTimestamp("start_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(rs.getTimestamp("end_datetime")).thenReturn(new Timestamp(System.currentTimeMillis() + 3600000));

        invoke("viewAvailableSlots", new Class[]{});
        assertTrue(outContent.toString().contains("ID: 7"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // bookAppointment — covers every typeChoice (1-7) + default + rule + slot
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testBookAppointmentNoDatabaseConnection() throws Exception {
        invoke("bookAppointment", new Class[]{Scanner.class}, sc("1\n4\n30\n1\n\n"));
        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    @Test
    void testBookAppointmentUrgentRuleFailure() throws Exception {
        Connection conn = mockConn();
        stubEmptySlots(conn);
        // type=1 URGENT, duration=60 violates <=30
        invoke("bookAppointment", new Class[]{Scanner.class}, sc("1\n1\n60\n1\n\n"));
        assertTrue(outContent.toString().contains("Booking failed"));
    }

    @Test
    void testBookAppointmentFollowUpRuleFailure() throws Exception {
        Connection conn = mockConn();
        stubEmptySlots(conn);
        // type=2 FOLLOW_UP, duration=60 violates <=30
        invoke("bookAppointment", new Class[]{Scanner.class}, sc("1\n2\n60\n1\n\n"));
        assertTrue(outContent.toString().contains("Booking failed"));
    }

    @Test
    void testBookAppointmentAssessmentRuleFailure() throws Exception {
        Connection conn = mockConn();
        stubEmptySlots(conn);
        // type=3 ASSESSMENT, duration=30 violates >=60
        invoke("bookAppointment", new Class[]{Scanner.class}, sc("1\n3\n30\n1\n\n"));
        assertTrue(outContent.toString().contains("Booking failed"));
    }

    @Test
    void testBookAppointmentVirtualSlotNotAvailable() throws Exception {
        Connection conn = mockConn();
        stubEmptySlots(conn);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        // type=4 VIRTUAL — no rule, slot not available
        invoke("bookAppointment", new Class[]{Scanner.class}, sc("99\n4\n30\n1\n\n"));
        assertTrue(outContent.toString().contains("Slot not available"));
    }

    @Test
    void testBookAppointmentInPersonSlotNotAvailable() throws Exception {
        Connection conn = mockConn();
        stubEmptySlots(conn);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        // type=5 IN_PERSON with location
        invoke("bookAppointment", new Class[]{Scanner.class}, sc("99\n5\n30\n1\nRoom1\n"));
        assertTrue(outContent.toString().contains("Slot not available"));
    }

    @Test
    void testBookAppointmentIndividualSlotNotAvailable() throws Exception {
        Connection conn = mockConn();
        stubEmptySlots(conn);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        // type=6 INDIVIDUAL, 1 participant
        invoke("bookAppointment", new Class[]{Scanner.class}, sc("99\n6\n30\n1\n\n"));
        assertTrue(outContent.toString().contains("Slot not available"));
    }

    @Test
    void testBookAppointmentGroupSlotNotAvailable() throws Exception {
        Connection conn = mockConn();
        stubEmptySlots(conn);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        // type=7 GROUP, 3 participants
        invoke("bookAppointment", new Class[]{Scanner.class}, sc("99\n7\n30\n3\n\n"));
        assertTrue(outContent.toString().contains("Slot not available"));
    }

    @Test
    void testBookAppointmentInvalidTypeDefault() throws Exception {
        Connection conn = mockConn();
        stubEmptySlots(conn);
        // type=99 → default → "Invalid appointment type."
        invoke("bookAppointment", new Class[]{Scanner.class}, sc("1\n99\n"));
        assertTrue(outContent.toString().contains("Invalid appointment type."));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // adminViewAllReservations
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testAdminViewAllReservationsNoDatabaseConnection() throws Exception {
        invoke("adminViewAllReservations", new Class[]{});
        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    @Test
    void testAdminViewAllReservationsEmpty() throws Exception {
        Connection conn = mockConn();
        stubEmptyPrepared(conn);
        invoke("adminViewAllReservations", new Class[]{});
        assertTrue(outContent.toString().contains("No reservations found."));
    }

    @Test
    void testAdminViewAllReservationsWithData() throws Exception {
        Connection conn = mockConn();
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

        invoke("adminViewAllReservations", new Class[]{});
        assertTrue(outContent.toString().contains("AdminTest"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // adminCancelReservation — covers all branches
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testAdminCancelReservationNoDatabaseConnection() throws Exception {
        // adminViewAllReservations also has no DB — that's fine
        invoke("adminCancelReservation", new Class[]{Scanner.class}, sc("1\n"));
        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    @Test
    void testAdminCancelReservationNotFound() throws Exception {
        Connection conn = mockConn();
        // First call: adminViewAllReservations (prepareStatement → empty RS)
        PreparedStatement viewStmt = mock(PreparedStatement.class);
        ResultSet emptyRs = mock(ResultSet.class);
        when(emptyRs.next()).thenReturn(false);
        when(viewStmt.executeQuery()).thenReturn(emptyRs);

        // Second call: getDetails (prepareStatement → not found)
        PreparedStatement getStmt = mock(PreparedStatement.class);
        ResultSet notFoundRs = mock(ResultSet.class);
        when(notFoundRs.next()).thenReturn(false);
        when(getStmt.executeQuery()).thenReturn(notFoundRs);

        when(conn.prepareStatement(anyString())).thenReturn(viewStmt).thenReturn(getStmt);

        invoke("adminCancelReservation", new Class[]{Scanner.class}, sc("999\n"));
        assertTrue(outContent.toString().contains("Appointment not found."));
    }

    @Test
    void testAdminCancelReservationSuccess() throws Exception {
        Connection conn = mockConn();

        PreparedStatement viewStmt   = mock(PreparedStatement.class);
        PreparedStatement getStmt    = mock(PreparedStatement.class);
        PreparedStatement deleteStmt = mock(PreparedStatement.class);
        PreparedStatement updateStmt = mock(PreparedStatement.class);

        ResultSet emptyRs  = mock(ResultSet.class);
        ResultSet detailsRs = mock(ResultSet.class);

        when(emptyRs.next()).thenReturn(false);
        when(viewStmt.executeQuery()).thenReturn(emptyRs);

        when(detailsRs.next()).thenReturn(true);
        when(detailsRs.getInt("slot_id")).thenReturn(5);
        when(detailsRs.getString("email")).thenReturn("u@test.com");
        when(detailsRs.getTimestamp("start_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(detailsRs.getTimestamp("end_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(getStmt.executeQuery()).thenReturn(detailsRs);

        when(deleteStmt.executeUpdate()).thenReturn(1); // rowsAffected > 0

        when(conn.prepareStatement(anyString()))
                .thenReturn(viewStmt)
                .thenReturn(getStmt)
                .thenReturn(deleteStmt)
                .thenReturn(updateStmt);

        invoke("adminCancelReservation", new Class[]{Scanner.class}, sc("1\n"));
        assertTrue(outContent.toString().contains("Reservation cancelled successfully!"));
    }

    @Test
    void testAdminCancelReservationFailedDelete() throws Exception {
        Connection conn = mockConn();

        PreparedStatement viewStmt   = mock(PreparedStatement.class);
        PreparedStatement getStmt    = mock(PreparedStatement.class);
        PreparedStatement deleteStmt = mock(PreparedStatement.class);

        ResultSet emptyRs   = mock(ResultSet.class);
        ResultSet detailsRs = mock(ResultSet.class);

        when(emptyRs.next()).thenReturn(false);
        when(viewStmt.executeQuery()).thenReturn(emptyRs);

        when(detailsRs.next()).thenReturn(true);
        when(detailsRs.getInt("slot_id")).thenReturn(5);
        when(detailsRs.getString("email")).thenReturn("u@test.com");
        when(detailsRs.getTimestamp(anyString())).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(getStmt.executeQuery()).thenReturn(detailsRs);

        when(deleteStmt.executeUpdate()).thenReturn(0); // rowsAffected == 0

        when(conn.prepareStatement(anyString()))
                .thenReturn(viewStmt)
                .thenReturn(getStmt)
                .thenReturn(deleteStmt);

        invoke("adminCancelReservation", new Class[]{Scanner.class}, sc("1\n"));
        assertTrue(outContent.toString().contains("Failed to cancel reservation."));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // adminModifyReservation — covers all branches
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testAdminModifyReservationNoDatabaseConnection() throws Exception {
        invoke("adminModifyReservation", new Class[]{Scanner.class}, sc("1\n"));
        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    @Test
    void testAdminModifyReservationNotFound() throws Exception {
        Connection conn = mockConn();
        PreparedStatement viewStmt = mock(PreparedStatement.class);
        PreparedStatement getStmt  = mock(PreparedStatement.class);
        ResultSet emptyRs    = mock(ResultSet.class);
        ResultSet notFoundRs = mock(ResultSet.class);

        when(emptyRs.next()).thenReturn(false);
        when(viewStmt.executeQuery()).thenReturn(emptyRs);
        when(notFoundRs.next()).thenReturn(false);
        when(getStmt.executeQuery()).thenReturn(notFoundRs);

        when(conn.prepareStatement(anyString())).thenReturn(viewStmt).thenReturn(getStmt);

        invoke("adminModifyReservation", new Class[]{Scanner.class}, sc("999\n"));
        assertTrue(outContent.toString().contains("Appointment not found."));
    }

    @Test
    void testAdminModifyReservationCancelledByUser() throws Exception {
        Connection conn = mockConn();
        PreparedStatement viewStmt = mock(PreparedStatement.class);
        PreparedStatement getStmt  = mock(PreparedStatement.class);
        ResultSet emptyRs  = mock(ResultSet.class);
        ResultSet apptRs   = mock(ResultSet.class);

        when(emptyRs.next()).thenReturn(false);
        when(viewStmt.executeQuery()).thenReturn(emptyRs);

        when(apptRs.next()).thenReturn(true);
        when(apptRs.getInt("slot_id")).thenReturn(1);
        when(apptRs.getString("email")).thenReturn("u@test.com");
        when(apptRs.getString("user_name")).thenReturn("User");
        when(apptRs.getTimestamp(anyString())).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(getStmt.executeQuery()).thenReturn(apptRs);

        // Also stub createStatement for viewAvailableSlots inside adminModifyReservation
        Statement slotsStmt = mock(Statement.class);
        ResultSet slotsRs   = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(slotsStmt);
        when(slotsStmt.executeQuery(anyString())).thenReturn(slotsRs);
        when(slotsRs.next()).thenReturn(false);

        when(conn.prepareStatement(anyString())).thenReturn(viewStmt).thenReturn(getStmt);

        // newSlotId = 0 → "Modification cancelled."
        invoke("adminModifyReservation", new Class[]{Scanner.class}, sc("1\n0\n"));
        assertTrue(outContent.toString().contains("Modification cancelled."));
    }

    @Test
    void testAdminModifyReservationInvalidSlot() throws Exception {
        Connection conn = mockConn();
        PreparedStatement viewStmt  = mock(PreparedStatement.class);
        PreparedStatement getStmt   = mock(PreparedStatement.class);
        PreparedStatement checkStmt = mock(PreparedStatement.class);
        ResultSet emptyRs  = mock(ResultSet.class);
        ResultSet apptRs   = mock(ResultSet.class);
        ResultSet noSlotRs = mock(ResultSet.class);

        when(emptyRs.next()).thenReturn(false);
        when(viewStmt.executeQuery()).thenReturn(emptyRs);

        when(apptRs.next()).thenReturn(true);
        when(apptRs.getInt("slot_id")).thenReturn(1);
        when(apptRs.getString("email")).thenReturn("u@test.com");
        when(apptRs.getString("user_name")).thenReturn("User");
        when(apptRs.getTimestamp(anyString())).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(getStmt.executeQuery()).thenReturn(apptRs);

        when(noSlotRs.next()).thenReturn(false); // slot not found
        when(checkStmt.executeQuery()).thenReturn(noSlotRs);

        Statement slotsStmt = mock(Statement.class);
        ResultSet slotsRs   = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(slotsStmt);
        when(slotsStmt.executeQuery(anyString())).thenReturn(slotsRs);
        when(slotsRs.next()).thenReturn(false);

        when(conn.prepareStatement(anyString()))
                .thenReturn(viewStmt)
                .thenReturn(getStmt)
                .thenReturn(checkStmt);

        invoke("adminModifyReservation", new Class[]{Scanner.class}, sc("1\n5\n"));
        assertTrue(outContent.toString().contains("Invalid slot ID."));
    }

    @Test
    void testAdminModifyReservationSlotNotAvailable() throws Exception {
        Connection conn = mockConn();
        PreparedStatement viewStmt  = mock(PreparedStatement.class);
        PreparedStatement getStmt   = mock(PreparedStatement.class);
        PreparedStatement checkStmt = mock(PreparedStatement.class);
        ResultSet emptyRs   = mock(ResultSet.class);
        ResultSet apptRs    = mock(ResultSet.class);
        ResultSet slotRs    = mock(ResultSet.class);

        when(emptyRs.next()).thenReturn(false);
        when(viewStmt.executeQuery()).thenReturn(emptyRs);

        when(apptRs.next()).thenReturn(true);
        when(apptRs.getInt("slot_id")).thenReturn(1);
        when(apptRs.getString("email")).thenReturn("u@test.com");
        when(apptRs.getString("user_name")).thenReturn("User");
        when(apptRs.getTimestamp(anyString())).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(getStmt.executeQuery()).thenReturn(apptRs);

        when(slotRs.next()).thenReturn(true);
        when(slotRs.getBoolean("is_available")).thenReturn(false); // not available
        when(slotRs.getTimestamp(anyString())).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(checkStmt.executeQuery()).thenReturn(slotRs);

        Statement slotsStmt = mock(Statement.class);
        ResultSet slotsRs   = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(slotsStmt);
        when(slotsStmt.executeQuery(anyString())).thenReturn(slotsRs);
        when(slotsRs.next()).thenReturn(false);

        when(conn.prepareStatement(anyString()))
                .thenReturn(viewStmt)
                .thenReturn(getStmt)
                .thenReturn(checkStmt);

        invoke("adminModifyReservation", new Class[]{Scanner.class}, sc("1\n5\n"));
        assertTrue(outContent.toString().contains("Selected slot is not available."));
    }

    @Test
    void testAdminModifyReservationSuccess() throws Exception {
        Connection conn = mockConn();
        PreparedStatement viewStmt   = mock(PreparedStatement.class);
        PreparedStatement getStmt    = mock(PreparedStatement.class);
        PreparedStatement checkStmt  = mock(PreparedStatement.class);
        PreparedStatement updateStmt = mock(PreparedStatement.class);
        PreparedStatement freeStmt   = mock(PreparedStatement.class);
        PreparedStatement bookStmt   = mock(PreparedStatement.class);

        ResultSet emptyRs  = mock(ResultSet.class);
        ResultSet apptRs   = mock(ResultSet.class);
        ResultSet slotRs   = mock(ResultSet.class);

        when(emptyRs.next()).thenReturn(false);
        when(viewStmt.executeQuery()).thenReturn(emptyRs);

        when(apptRs.next()).thenReturn(true);
        when(apptRs.getInt("slot_id")).thenReturn(1);
        when(apptRs.getString("email")).thenReturn("u@test.com");
        when(apptRs.getString("user_name")).thenReturn("User");
        when(apptRs.getTimestamp(anyString())).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(getStmt.executeQuery()).thenReturn(apptRs);

        when(slotRs.next()).thenReturn(true);
        when(slotRs.getBoolean("is_available")).thenReturn(true);
        when(slotRs.getTimestamp(anyString())).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(checkStmt.executeQuery()).thenReturn(slotRs);

        Statement slotsStmt = mock(Statement.class);
        ResultSet slotsRs   = mock(ResultSet.class);
        when(conn.createStatement()).thenReturn(slotsStmt);
        when(slotsStmt.executeQuery(anyString())).thenReturn(slotsRs);
        when(slotsRs.next()).thenReturn(false);

        when(conn.prepareStatement(anyString()))
                .thenReturn(viewStmt)
                .thenReturn(getStmt)
                .thenReturn(checkStmt)
                .thenReturn(updateStmt)
                .thenReturn(freeStmt)
                .thenReturn(bookStmt);

        invoke("adminModifyReservation", new Class[]{Scanner.class}, sc("1\n2\n"));
        assertTrue(outContent.toString().contains("Reservation modified successfully!"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // cancelAppointment (user)
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testCancelAppointmentNoDatabaseConnection() throws Exception {
        setStaticField(Appoitment_Main_Page.class, "loggedInUserId", 1);
        invoke("cancelAppointment", new Class[]{Scanner.class}, sc("1\n"));
        assertTrue(outContent.toString().contains("Cannot connect to database."));
    }

    @Test
    void testCancelAppointmentNotFound() throws Exception {
        setStaticField(Appoitment_Main_Page.class, "loggedInUserId", 1);
        Connection conn = mockConn();

        PreparedStatement viewStmt = mock(PreparedStatement.class);
        PreparedStatement getStmt  = mock(PreparedStatement.class);
        ResultSet emptyRs    = mock(ResultSet.class);
        ResultSet notFoundRs = mock(ResultSet.class);

        when(emptyRs.next()).thenReturn(false);
        when(viewStmt.executeQuery()).thenReturn(emptyRs);
        when(notFoundRs.next()).thenReturn(false);
        when(getStmt.executeQuery()).thenReturn(notFoundRs);

        when(conn.prepareStatement(anyString())).thenReturn(viewStmt).thenReturn(getStmt);

        invoke("cancelAppointment", new Class[]{Scanner.class}, sc("999\n"));
        assertTrue(outContent.toString().contains("Appointment not found"));
    }

    @Test
    void testCancelAppointmentSuccess() throws Exception {
        setStaticField(Appoitment_Main_Page.class, "loggedInUserId", 1);
        Connection conn = mockConn();

        PreparedStatement viewStmt   = mock(PreparedStatement.class);
        PreparedStatement getStmt    = mock(PreparedStatement.class);
        PreparedStatement deleteStmt = mock(PreparedStatement.class);
        PreparedStatement freeStmt   = mock(PreparedStatement.class);

        ResultSet emptyRs = mock(ResultSet.class);
        ResultSet apptRs  = mock(ResultSet.class);

        when(emptyRs.next()).thenReturn(false);
        when(viewStmt.executeQuery()).thenReturn(emptyRs);

        when(apptRs.next()).thenReturn(true);
        when(apptRs.getInt("slot_id")).thenReturn(10);
        when(apptRs.getString("email")).thenReturn("u@test.com");
        when(apptRs.getTimestamp(anyString())).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(getStmt.executeQuery()).thenReturn(apptRs);

        when(conn.prepareStatement(anyString()))
                .thenReturn(viewStmt)
                .thenReturn(getStmt)
                .thenReturn(deleteStmt)
                .thenReturn(freeStmt);

        invoke("cancelAppointment", new Class[]{Scanner.class}, sc("1\n"));
        assertTrue(outContent.toString().contains("cancelled successfully"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // testNotifications
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testTestNotificationsWithCustomEmail() throws Exception {
        NotificationManager mockNM = mock(NotificationManager.class);
        setStaticField(Appoitment_Main_Page.class, "notificationManager", mockNM);
        invoke("testNotifications", new Class[]{Scanner.class}, sc("user@test.com\n"));
        verify(mockNM).sendBookingConfirmation(eq("user@test.com"), contains("test"));
        verify(mockNM).sendCancellationNotice(eq("user@test.com"), anyString());
    }

    @Test
    void testTestNotificationsDefaultEmail() throws Exception {
        NotificationManager mockNM = mock(NotificationManager.class);
        setStaticField(Appoitment_Main_Page.class, "notificationManager", mockNM);
        invoke("testNotifications", new Class[]{Scanner.class}, sc("\n"));
        verify(mockNM).sendBookingConfirmation(eq("demo@example.com"), anyString());
        verify(mockNM).sendCancellationNotice(eq("demo@example.com"), anyString());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DatabaseConnection
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    void testDatabaseConnectionClose() throws Exception {
        Connection mockC = mock(Connection.class);
        Field f = DatabaseConnection.class.getDeclaredField("connection");
        f.setAccessible(true);
        f.set(null, mockC);
        DatabaseConnection.closeConnection();
        verify(mockC).close();
        assertNull(f.get(null));
    }

    @Test
    void testDatabaseConnectionCloseWhenNull() throws Exception {
        assertDoesNotThrow(() -> DatabaseConnection.closeConnection());
    }
}
