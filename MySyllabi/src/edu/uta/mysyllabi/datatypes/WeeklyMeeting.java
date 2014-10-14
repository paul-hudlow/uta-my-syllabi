package edu.uta.mysyllabi.datatypes;


public class WeeklyMeeting {
	public static final int MAX_TIME = 1439;
	private TimeOfDay startTime; // in minutes from midnight
	private int duration; // in minutes
	private char[] daysOfWeek; // Array has length 7 and indicates each meeting day with the character 'y'.
	private String location;
	
	private static final String[] dayAbbreviations = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
	
	public WeeklyMeeting(int startTime, int duration, String daysOfWeek, String location) throws IllegalArgumentException {
		if (daysOfWeek == null || location == null || startTime + duration > MAX_TIME) {
			throw new IllegalArgumentException();
		}
		this.startTime = new TimeOfDay(startTime);
		this.daysOfWeek = daysOfWeek.toCharArray();
		this.duration = duration;
		this.location = location;
	}
	
	public String getOccurence(){
		StringBuilder days = new StringBuilder();
		
		/* Loop through character array, looking for meeting days. */
		for (int i = 0; i < 7; i++) {
			if (daysOfWeek[i] == 'y') {
				/* Include the meeting day. */
				days.append(dayAbbreviations[i]);
			}
		}
		
		TimeOfDay endTime = new TimeOfDay(startTime.getTotalMinutes() + duration);
		return days.toString() + " " + startTime.getTime(false) + "-" + endTime.getTime(false);
	}
	
	public String getDaysOfWeek() {
		return new String(this.daysOfWeek);
	}
	
	public int getStartTime() {
		return this.startTime.getTotalMinutes();
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public String getLocation() {
		return location;
	}
}
