import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
public class AppoitmentMainPageTesting {
		@BeforeAll
	    static void startAllTests() {
	        System.out.println("Starting Main Page tests");
	    }

	    @AfterAll
	    static void finishAllTests() {
	        System.out.println("Finished Main Page tests");
	    }

	    @BeforeEach
	    void setup() {
	        Appoitment_Main_Page.weekSchedule.clear();
	        System.out.println("Reset schedule before test");
	    }

	    @AfterEach
	    void tearDown() {
	        System.out.println("Test finished");
	    }
	    @Test
	    void scheduleShouldContainFiveDays() {
	    	Appoitment_Main_Page.initializeSchedule();
	    	assertEquals(5, Appoitment_Main_Page.weekSchedule.size());
	    }
	    @Test
	    void firstDayShouldBeSunday() {
	    	Appoitment_Main_Page.initializeSchedule();
	    	assertEquals("Sunday", Appoitment_Main_Page.weekSchedule.get(0).getDay());
	    }
}
