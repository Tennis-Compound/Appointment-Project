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
	        
	    }

	    @AfterEach
	    void tearDown() {
	        System.out.println("Test finished");
	    }
}
