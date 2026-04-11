import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test file to verify testing setup
 */
public class AppoitmentMainPageTesting {
    
    @Test
    void sampleTest() {
        // Simple test that should pass
        assertTrue(2 + 2 == 4);
        System.out.println("sampleTest passed!");
    }
    
    @Test
    void anotherTest() {
        // Another simple test
        assertNotNull("Hello World");
        System.out.println("anotherTest passed!");
    }
    
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
        System.out.println("Setup before test");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test finished");
    }
}
