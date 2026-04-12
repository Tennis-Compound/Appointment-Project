import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class AppointmentTypeTest {

    @Test
    void urgentConstantShouldExist() { assertNotNull(AppointmentType.URGENT); }

    @Test
    void followUpConstantShouldExist() { assertNotNull(AppointmentType.FOLLOW_UP); }

    @Test
    void assessmentConstantShouldExist() { assertNotNull(AppointmentType.ASSESSMENT); }

    @Test
    void virtualConstantShouldExist() { assertNotNull(AppointmentType.VIRTUAL); }

    @Test
    void inPersonConstantShouldExist() { assertNotNull(AppointmentType.IN_PERSON); }

    @Test
    void individualConstantShouldExist() { assertNotNull(AppointmentType.INDIVIDUAL); }

    @Test
    void groupConstantShouldExist() { assertNotNull(AppointmentType.GROUP); }

    @Test
    void valueOfUrgentShouldWork() {
        assertEquals(AppointmentType.URGENT, AppointmentType.valueOf("URGENT"));
    }

    @Test
    void valueOfFollowUpShouldWork() {
        assertEquals(AppointmentType.FOLLOW_UP, AppointmentType.valueOf("FOLLOW_UP"));
    }

    @Test
    void valueOfAssessmentShouldWork() {
        assertEquals(AppointmentType.ASSESSMENT, AppointmentType.valueOf("ASSESSMENT"));
    }

    @Test
    void valueOfVirtualShouldWork() {
        assertEquals(AppointmentType.VIRTUAL, AppointmentType.valueOf("VIRTUAL"));
    }

    @Test
    void valueOfInPersonShouldWork() {
        assertEquals(AppointmentType.IN_PERSON, AppointmentType.valueOf("IN_PERSON"));
    }

    @Test
    void valueOfInvalidNameShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> AppointmentType.valueOf("INVALID"));
    }

    @Test
    void namesShouldMatchConstantNames() {
        assertEquals("URGENT",    AppointmentType.URGENT.name());
        assertEquals("FOLLOW_UP", AppointmentType.FOLLOW_UP.name());
        assertEquals("GROUP",     AppointmentType.GROUP.name());
    }

    @Test
    void valuesShouldContainAllSevenConstants() {
        assertEquals(7, AppointmentType.values().length);
    }

    @Test
    void enumConstantsShouldBeUnique() {
        AppointmentType[] all = AppointmentType.values();
        for (int i = 0; i < all.length; i++) {
            for (int j = i + 1; j < all.length; j++) {
                assertNotEquals(all[i], all[j]);
            }
        }
    }
}
