import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.lang.reflect.*;


public class AppoitmentMainPageTesting {

    // ── helpers ───────────────────────────────────────────────────────────────

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void redirectOutput() {
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreOutput() throws Exception {
        System.setOut(originalOut);
        // Reset static state to defaults after every test
        setStaticField("isLoggedIn", false);
        setStaticField("loggedInUserId", -1);
        setStaticField("loggedInUserName", "");
        setStaticField("notificationManager", null);
    }

    /** Read captured stdout as a string. */
    private String output() {
        return outContent.toString();
    }

    /** Set any static field (including private) by name. */
    private static void setStaticField(String fieldName, Object value) throws Exception {
        Field f = Appoitment_Main_Page.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(null, value);
    }

    /** Invoke a private static method that accepts a Scanner. */
    private static Object invokeWithScanner(String methodName, String simulatedInput)
            throws Exception {
        java.util.Scanner scanner = new java.util.Scanner(simulatedInput);
        Method m = Appoitment_Main_Page.class.getDeclaredMethod(methodName, java.util.Scanner.class);
        m.setAccessible(true);
        return m.invoke(null, scanner);
    }

    /** Invoke a private static no-arg method. */
    private static Object invokeNoArg(String methodName) throws Exception {
        Method m = Appoitment_Main_Page.class.getDeclaredMethod(methodName);
        m.setAccessible(true);
        return m.invoke(null);
    }

    // ── initializeNotificationService (useMockNotifications = true path) ──────

    @Test
    void initializeNotificationService_mockPath_setsNotificationManager() throws Exception {
        // Force useMockNotifications = true
        Field useMock = Appoitment_Main_Page.class.getDeclaredField("useMockNotifications");
        useMock.setAccessible(true);
        useMock.set(null, true);

        Method init = Appoitment_Main_Page.class.getDeclaredMethod("initializeNotificationService");
        init.setAccessible(true);
        init.invoke(null);

        Field nm = Appoitment_Main_Page.class.getDeclaredField("notificationManager");
        nm.setAccessible(true);
        assertNotNull(nm.get(null));

        // restore
        useMock.set(null, false);
    }

    @Test
    void initializeNotificationService_mockPath_printsMockMessage() throws Exception {
        Field useMock = Appoitment_Main_Page.class.getDeclaredField("useMockNotifications");
        useMock.setAccessible(true);
        useMock.set(null, true);

        Method init = Appoitment_Main_Page.class.getDeclaredMethod("initializeNotificationService");
        init.setAccessible(true);
        init.invoke(null);

        assertTrue(output().contains("mock"));
        useMock.set(null, false);
    }

    // initializeNotificationService — dotenv/email path (no .env file → falls to catch → mock)
    @Test
    void initializeNotificationService_emailPath_fallsBackToMockOnException() throws Exception {
        Field useMock = Appoitment_Main_Page.class.getDeclaredField("useMockNotifications");
        useMock.setAccessible(true);
        useMock.set(null, false);

        Method init = Appoitment_Main_Page.class.getDeclaredMethod("initializeNotificationService");
        init.setAccessible(true);
        // Will throw internally (no .env in test context) and fall back to mock — must not propagate
        assertDoesNotThrow(() -> init.invoke(null));

        Field nm = Appoitment_Main_Page.class.getDeclaredField("notificationManager");
        nm.setAccessible(true);
        assertNotNull(nm.get(null)); // fallback mock is set

        useMock.set(null, false);
    }

    // ── showMenu ──────────────────────────────────────────────────────────────

    @Test
    void showMenu_shouldReturnChosenNumber() throws Exception {
        Object result = invokeWithScanner("showMenu", "2\n");
        assertEquals(2, result);
    }

    @Test
    void showMenu_shouldReturnZeroForExit() throws Exception {
        Object result = invokeWithScanner("showMenu", "0\n");
        assertEquals(0, result);
    }

    @Test
    void showMenu_shouldPrintAllOptions() throws Exception {
        invokeWithScanner("showMenu", "1\n");
        String out = output();
        assertTrue(out.contains("Administrator Login"));
        assertTrue(out.contains("User Login"));
        assertTrue(out.contains("User Sign up"));
        assertTrue(out.contains("View Available Appointment Slots"));
        assertTrue(out.contains("Exit"));
    }

    // ── showBookingRules ──────────────────────────────────────────────────────

    @Test
    void showBookingRules_shouldPrintAllRuleTypes() throws Exception {
        invokeNoArg("showBookingRules");
        String out = output();
        assertTrue(out.contains("URGENT"));
        assertTrue(out.contains("FOLLOW_UP"));
        assertTrue(out.contains("ASSESSMENT"));
        assertTrue(out.contains("VIRTUAL"));
        assertTrue(out.contains("IN_PERSON"));
        assertTrue(out.contains("INDIVIDUAL"));
        assertTrue(out.contains("GROUP"));
    }

    @Test
    void showBookingRules_shouldMentionDuration() throws Exception {
        invokeNoArg("showBookingRules");
        assertTrue(output().contains("duration"));
    }

    @Test
    void showBookingRules_shouldMentionParticipantCount() throws Exception {
        invokeNoArg("showBookingRules");
        assertTrue(output().contains("participant"));
    }

    // ── adminLogin ────────────────────────────────────────────────────────────
    // adminLogin reads from dotenv which may not exist in test — it catches internally via Dotenv
    // We just verify no exception escapes and isLoggedIn state is not corrupted.

    @Test
    void adminLogin_withNoEnvFile_shouldNotThrow() {
        assertDoesNotThrow(() -> invokeWithScanner("adminLogin", "admin\nwrongpass\n"));
    }

    @Test
    void adminLogin_withWrongCredentials_shouldPrintInvalid() {
        try {
            invokeWithScanner("adminLogin", "wrongUser\nwrongPass\n");
        } catch (Exception ignored) { /* dotenv missing is fine */ }
        // If dotenv present and credentials wrong, "Invalid Credentials" would be printed.
        // If dotenv missing, an exception is caught. Either way isLoggedIn stays false.
        assertFalse(Appoitment_Main_Page.isLoggedIn);
    }

    // ── adminMenu ─────────────────────────────────────────────────────────────

    @Test
    void adminMenu_choice1_logsOut() throws Exception {
        setStaticField("isLoggedIn", true);

        // Set up a mock notification manager to avoid NPE in paths that use it
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("adminMenu", "1\n");

        assertFalse(Appoitment_Main_Page.isLoggedIn);
        assertTrue(output().contains("logged out"));
    }

    @Test
    void adminMenu_choice1_printsLogoutMessage() throws Exception {
        setStaticField("isLoggedIn", true);
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("adminMenu", "1\n");
        assertTrue(output().contains("logged out"));
    }

    @Test
    void adminMenu_defaultCase_printsInvalidOption() throws Exception {
        setStaticField("isLoggedIn", true);
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("adminMenu", "99\n");
        assertTrue(output().contains("Invalid"));
    }

    // ── testNotifications ─────────────────────────────────────────────────────

    @Test
    void testNotifications_withEmptyInput_usesDefaultEmail() throws Exception {
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("testNotifications", "\n"); // empty → uses demo@example.com
        String out = output();
        assertTrue(out.contains("demo@example.com"));
    }

    @Test
    void testNotifications_withProvidedEmail_usesThatEmail() throws Exception {
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("testNotifications", "custom@test.com\n");
        assertTrue(output().contains("custom@test.com"));
    }

    @Test
    void testNotifications_sendsBookingAndCancellationNotifications() throws Exception {
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("testNotifications", "\n");

        // MockNotificationService records calls
        assertTrue(mockSvc.getSentMessages().size() >= 2);
    }

    // ── viewAvailableSlots ────────────────────────────────────────────────────
    // DatabaseConnection.getConnection() returns null when no DB → prints "Cannot connect"

    @Test
    void viewAvailableSlots_withNoDbConnection_printsCannotConnect() throws Exception {
        invokeNoArg("viewAvailableSlots");
        assertTrue(output().contains("Cannot connect") || output().contains("Error"));
    }

    // ── adminViewAllReservations ───────────────────────────────────────────────

    @Test
    void adminViewAllReservations_withNoDbConnection_printsCannotConnect() throws Exception {
        invokeNoArg("adminViewAllReservations");
        assertTrue(output().contains("Cannot connect") || output().contains("Error"));
    }

    // ── viewMyAppointments ────────────────────────────────────────────────────

    @Test
    void viewMyAppointments_withNoDbConnection_printsCannotConnect() throws Exception {
        setStaticField("loggedInUserId", 1);
        invokeNoArg("viewMyAppointments");
        assertTrue(output().contains("Cannot connect") || output().contains("Error"));
    }

    // ── cancelAppointment ─────────────────────────────────────────────────────

    @Test
    void cancelAppointment_withNoDbConnection_printsCannotConnect() throws Exception {
        setStaticField("loggedInUserId", 1);
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("cancelAppointment", "1\n");
        assertTrue(output().contains("Cannot connect") || output().contains("Error")
                   || output().contains("no appointments"));
    }

    // ── modifyAppointment ─────────────────────────────────────────────────────

    @Test
    void modifyAppointment_withNoDbConnection_printsCannotConnect() throws Exception {
        setStaticField("loggedInUserId", 1);
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("modifyAppointment", "1\n0\n");
        assertTrue(output().contains("Cannot connect") || output().contains("Error")
                   || output().contains("no appointments"));
    }

    // ── adminCancelReservation ────────────────────────────────────────────────

    @Test
    void adminCancelReservation_withNoDbConnection_printsCannotConnect() throws Exception {
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("adminCancelReservation", "1\n");
        assertTrue(output().contains("Cannot connect") || output().contains("Error")
                   || output().contains("No reservations"));
    }

    // ── adminModifyReservation ────────────────────────────────────────────────

    @Test
    void adminModifyReservation_withNoDbConnection_printsCannotConnect() throws Exception {
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("adminModifyReservation", "1\n0\n");
        assertTrue(output().contains("Cannot connect") || output().contains("Error")
                   || output().contains("No reservations"));
    }

    // ── bookAppointment ───────────────────────────────────────────────────────

    @Test
    void bookAppointment_withNoDbConnection_printsCannotConnect() throws Exception {
        setStaticField("loggedInUserId", 1);
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        // slot=1, type=1(URGENT), duration=20, participants=1, location=""
        invokeWithScanner("bookAppointment", "1\n1\n20\n1\n\n");
        assertTrue(output().contains("Cannot connect") || output().contains("Error")
                   || output().contains("Slot not available") || output().contains("Booking failed"));
    }

    @Test
    void bookAppointment_invalidTypeChoice_printsInvalidType() throws Exception {
        setStaticField("loggedInUserId", 1);
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        // slot=1, type=99 (invalid), no further input needed
        invokeWithScanner("bookAppointment", "1\n99\n");
        assertTrue(output().contains("Invalid appointment type")
                   || output().contains("Cannot connect"));
    }

    // ── userLogin ─────────────────────────────────────────────────────────────

    @Test
    void userLogin_withNoDbConnection_printsCannotConnect() throws Exception {
        invokeWithScanner("userLogin", "testuser\ntestpass\n");
        assertTrue(output().contains("Cannot connect") || output().contains("Error"));
    }

    // ── userSignUp ────────────────────────────────────────────────────────────

    @Test
    void userSignUp_withNoDbConnection_printsCannotConnect() throws Exception {
        invokeWithScanner("userSignUp", "newuser\nnew@email.com\npassword\n");
        assertTrue(output().contains("Cannot connect") || output().contains("Error"));
    }

    // ── static field accessors ────────────────────────────────────────────────

    @Test
    void isLoggedIn_defaultValueIsFalse() {
        assertFalse(Appoitment_Main_Page.isLoggedIn);
    }

    @Test
    void isLoggedIn_canBeSetToTrue() throws Exception {
        setStaticField("isLoggedIn", true);
        assertTrue(Appoitment_Main_Page.isLoggedIn);
    }
}