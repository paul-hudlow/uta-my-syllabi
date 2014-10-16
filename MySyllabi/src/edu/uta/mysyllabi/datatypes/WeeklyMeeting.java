package edu.uta.mysyllabi.datatypes;

import java.util.List;


public class WeeklyMeeting {
	private TimeOfDay startTime; // in minutes from midnight
	private int duration; // in minutes
	private char[] daysOfWeek; // Array has length 7 and indicates each meeting day with the character 'y'.
	private String location;
	
	private static final String[] dayAbbreviations = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
	
	public static final int MAX_TIME = 1439;
	public static final int MONDAY = 0;
	public static final int TUESDAY = 1;
	public static final int WEDNESDAY = 2;
	public static final int THURSDAY = 3;
	public static final int FRIDAY = 4;
	public static final int SATURDAY = 5;
	public static final int SUNDAY = 6;
	
	public WeeklyMeeting() {
		daysOfWeek = new String("nnnnnnn").toCharArray();
	}
	
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
		String days = getDaysOfWeek();
		TimeOfDay endTime = new TimeOfDay(startTime.getTotalMinutes() + duration);
		return days + " " + startTime.toString(false) + "-" + endTime.toString(false);
	}
	
	public boolean isMeetingDay(int day) {
		if (daysOfWeek[day] == 'y') {
			return true;
		}
		return false;
	}
	
	public void setDaysOfWeek(List<Integer> days) {
		daysOfWeek = new String("nnnnnnn").toCharArray();
		for (int nextDay : days) {
			this.daysOfWeek[nextDay] = 'y';
		}
	}
	
	public String getDaysOfWeek() {
		StringBuilder days = new StringBuilder();
		/* Loop through character array, looking for meeting days. */
		for (int i = 0; i < 7; i++) {
			if (daysOfWeek[i] == 'y') {
				/* Include the meeting day. */
				days.append(dayAbbreviations[i]);
			}
		}
		return days.toString();
	}
	
	public TimeOfDay getStartTime() {
		return startTime;
	}
	
	public void setStartTime(TimeOfDay time) {
		startTime = time;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public void setDuration(int minutes) {
		this.duration = minutes;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
}
