import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
public class DayScheduleTest {
	 DaySchedule schedule;
	 	@BeforeAll
	    static void startAllTests() {
	        System.out.println("Starting DaySchedule tests");
	    }

	    @AfterAll
	    static void finishAllTests() {
	        System.out.println("Finished DaySchedule tests");
	    }

	    @BeforeEach
	    void setup() {
	        schedule = new DaySchedule("Sunday");
	        System.out.println("New DaySchedule created");
	    }

	    @AfterEach
	    void tearDown() {
	        System.out.println("Test completed");
	    }
	    @Test
	    void dayNameShouldBeCorrect() {
	    	assertEquals("Sunday", schedule.getDay());
	    }
	    @Test
	    void slotShouldBeAddedCorrectly() {

	        TimeSlots slot = new TimeSlots("10:00");

	        schedule.addSlot(slot);

	        assertEquals(1, schedule.getSlots().size());
	    }
	    @Test
	    void addedSlotShouldExistInSchedule() {

	        TimeSlots slot = new TimeSlots("11:00");

	        schedule.addSlot(slot);

	        assertEquals("11:00", schedule.getSlots().get(0).getTime());
	    }
}
