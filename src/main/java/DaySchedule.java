import java.util.ArrayList;
import java.util.List;
public class DaySchedule {
	
	private String day;
	private List<TimeSlots> slots;
	
	public DaySchedule(String day) {
		this.day = day;
		this.slots = new ArrayList<>();
	}
	
	public String getDay() {
		return day;
	}
	
	public List<TimeSlots> getSlots(){
		return slots;
	}
	
	public void addSlot(TimeSlots slot) {
		slots.add(slot);
	}
}