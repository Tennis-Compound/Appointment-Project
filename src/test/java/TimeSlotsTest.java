import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class TimeSlotsTest {

    TimeSlots slot;

    @BeforeAll
    static void startAllTests() {
        System.out.println("Starting TimeSlot tests");
    }

    @AfterAll
    static void finishAllTests() {
        System.out.println("Finished TimeSlot tests");
    }

    @BeforeEach
    void setup() {
        slot = new TimeSlots("09:00");
        System.out.println("Creating new TimeSlot object");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test finished");
    }

    @Test
    void slotShouldNotBeBookedInitially() {

        assertFalse(slot.isBooked());
    }

    @Test
    void slotShouldBeBookedAfterBooking() {

        slot.bookSlot();

        assertTrue(slot.isBooked());
    }

    @Test
    void slotTimeShouldBeCorrect() {

        assertEquals("09:00", slot.getTime());
    }
}