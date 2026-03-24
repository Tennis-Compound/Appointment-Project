public class TimeSlots {
	private String time;
	private boolean booked;
	
	public TimeSlots(String time) {
		this.time = time;
		this.booked = false;
	}
	
	public String getTime() {
		return time;
	}
	
	public boolean isBooked() {
		return booked;
	}
	
	public void bookSlot() {
		booked = true;
	}
}
