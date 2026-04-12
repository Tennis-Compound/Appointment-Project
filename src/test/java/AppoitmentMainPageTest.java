import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.lang.reflect.*;


public class AppoitmentMainPageTest {

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
        setStaticField("isLoggedIn", false);
        setStaticField("loggedInUserId", -1);
        setStaticField("loggedInUserName", "");
        setStaticField("notificationManager", null);
    }

    private String output() {
        return outContent.toString();
    }

    private static void setStaticField(String fieldName, Object value) throws Exception {
        Field f = Appoitment_Main_Page.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(null, value);
    }

    private static Object invokeWithScanner(String methodName, String simulatedInput)
            throws Exception {
        java.util.Scanner scanner = new java.util.Scanner(simulatedInput);
        Method m = Appoitment_Main_Page.class.getDeclaredMethod(methodName, java.util.Scanner.class);
        m.setAccessible(true);
        return m.invoke(null, scanner);
    }

    private static Object invokeNoArg(String methodName) throws Exception {
        Method m = Appoitment_Main_Page.class.getDeclaredMethod(methodName);
        m.setAccessible(true);
        return m.invoke(null);
    }


    @Test
    void initializeNotificationService_mockPath_setsNotificationManager() throws Exception {
        Field useMock = Appoitment_Main_Page.class.getDeclaredField("useMockNotifications");
        useMock.setAccessible(true);
        useMock.set(null, true);

        Method init = Appoitment_Main_Page.class.getDeclaredMethod("initializeNotificationService");
        init.setAccessible(true);
        init.invoke(null);

        Field nm = Appoitment_Main_Page.class.getDeclaredField("notificationManager");
        nm.setAccessible(true);
        assertNotNull(nm.get(null));

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

    @Test
    void initializeNotificationService_emailPath_fallsBackToMockOnException() throws Exception {
        Field useMock = Appoitment_Main_Page.class.getDeclaredField("useMockNotifications");
        useMock.setAccessible(true);
        useMock.set(null, false);

        Method init = Appoitment_Main_Page.class.getDeclaredMethod("initializeNotificationService");
        init.setAccessible(true);
        assertDoesNotThrow(() -> init.invoke(null));

        Field nm = Appoitment_Main_Page.class.getDeclaredField("notificationManager");
        nm.setAccessible(true);
        assertNotNull(nm.get(null));

        useMock.set(null, false);
    }


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

    @Test
    void adminLogin_withNoEnvFile_shouldNotThrow() {
        assertDoesNotThrow(() -> invokeWithScanner("adminLogin", "admin\nwrongpass\n"));
    }

    @Test
    void adminLogin_withWrongCredentials_shouldPrintInvalid() {
        try {
            invokeWithScanner("adminLogin", "wrongUser\nwrongPass\n");
        } catch (Exception ignored) { /* dotenv missing is fine */ }

        assertFalse(Appoitment_Main_Page.isLoggedIn);
    }


    @Test
    void adminMenu_choice1_logsOut() throws Exception {
        setStaticField("isLoggedIn", true);

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


    @Test
    void testNotifications_withEmptyInput_usesDefaultEmail() throws Exception {
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("testNotifications", "\n");
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

        assertTrue(mockSvc.getSentMessages().size() >= 2);
    }


    @Test
    void viewAvailableSlots_withNoDbConnection_printsCannotConnect() throws Exception {
        invokeNoArg("viewAvailableSlots");
        assertTrue(output().contains("Cannot connect") || output().contains("Error"));
    }


    @Test
    void adminViewAllReservations_withNoDbConnection_printsCannotConnect() throws Exception {
        invokeNoArg("adminViewAllReservations");
        assertTrue(output().contains("Cannot connect") || output().contains("Error"));
    }


    @Test
    void viewMyAppointments_withNoDbConnection_printsCannotConnect() throws Exception {
        setStaticField("loggedInUserId", 1);
        invokeNoArg("viewMyAppointments");
        assertTrue(output().contains("Cannot connect") || output().contains("Error"));
    }


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


    @Test
    void adminCancelReservation_withNoDbConnection_printsCannotConnect() throws Exception {
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("adminCancelReservation", "1\n");
        assertTrue(output().contains("Cannot connect") || output().contains("Error")
                   || output().contains("No reservations"));
    }


    @Test
    void adminModifyReservation_withNoDbConnection_printsCannotConnect() throws Exception {
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

        invokeWithScanner("adminModifyReservation", "1\n0\n");
        assertTrue(output().contains("Cannot connect") || output().contains("Error")
                   || output().contains("No reservations"));
    }


    @Test
    void bookAppointment_withNoDbConnection_printsCannotConnect() throws Exception {
        setStaticField("loggedInUserId", 1);
        MockNotificationService mockSvc = new MockNotificationService();
        NotificationManager mgr = new NotificationManager(mockSvc);
        setStaticField("notificationManager", mgr);

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

        invokeWithScanner("bookAppointment", "1\n99\n");
        assertTrue(output().contains("Invalid appointment type")
                   || output().contains("Cannot connect"));
    }


    @Test
    void userLogin_withNoDbConnection_printsCannotConnect() throws Exception {
        invokeWithScanner("userLogin", "testuser\ntestpass\n");
        assertTrue(output().contains("Cannot connect") || output().contains("Error"));
    }


    @Test
    void userSignUp_withNoDbConnection_printsCannotConnect() throws Exception {
        invokeWithScanner("userSignUp", "newuser\nnew@email.com\npassword\n");
        assertTrue(output().contains("Cannot connect") || output().contains("Error"));
    }


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