package edu.uta.mysyllabi.datatypes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class WeeklyMeeting {
	private TimeOfDay startTime; // in minutes from midnight
	private int duration = 0; // in minutes
	private char[] daysOfWeek = {'n','n','n','n','n','n','n'}; // Array has length 7 and indicates each meeting day with the character 'y'.
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
	
	private final String START_TIME = "start_time";
	private final String DURATION = "duration";
	private final String DAYS_OF_WEEK = "days_of_week";
	private final String LOCATION = "location";
	
	protected final String[] contentKeys = {
			LOCATION,
			START_TIME,
			DURATION,
			DAYS_OF_WEEK
	};
	
	public WeeklyMeeting() {
		daysOfWeek = new String("nnnnnnn").toCharArray();
	}
	
	public boolean isValid() {
		if (startTime != null) {
			return true;
		} else {
			return false;
		}
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
	
	public List<String> getContentKeys(String keyPrefix) {
		LinkedList<String> keyList = new LinkedList<String>();
		for (int i = 0; i < contentKeys.length; i++) {
			keyList.add(keyPrefix + contentKeys[i]);
		}
		return keyList;
	}
	
	public void addContentFromMap(Map<String, String> map, String keyPrefix) {
		try {
			this.startTime = new TimeOfDay(Integer.parseInt(map.get(keyPrefix + START_TIME)));
		} catch (NumberFormatException exception) {
			this.startTime = null;
		}
		try {
			this.duration = Integer.parseInt(map.get(keyPrefix + DURATION));
		} catch (NumberFormatException exception) {
			this.duration = 0;
		}
		if (map.get(keyPrefix + DAYS_OF_WEEK) != null) {
			this.daysOfWeek = map.get(keyPrefix + DAYS_OF_WEEK).toCharArray();
		}
		this.location = map.get(keyPrefix + LOCATION);
	}
	
	public Map<String, String> getContentMap(String keyPrefix) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		if (this.startTime != null) {
			map.put(keyPrefix + START_TIME, Integer.toString(this.startTime.getTotalMinutes()));
		}
		map.put(keyPrefix + DURATION, Integer.toString(this.duration));
		map.put(keyPrefix + DAYS_OF_WEEK, new String(this.daysOfWeek));
		map.put(keyPrefix + LOCATION, this.location);
		
		return map;
	}
	
	public String getOccurrence(){
		String days = getDaysOfWeek();
		String occurrence;
		if (startTime != null && duration > 0) {
			TimeOfDay endTime = new TimeOfDay(startTime.getTotalMinutes() + duration);
			occurrence = days + " " + startTime.toString(false) + "-" + endTime.toString(false);
		} else if (startTime != null) {
			occurrence = days + " " + startTime.toString(false);
		} else {
			occurrence = days;
		}
		return occurrence;
	}
	
	public boolean isMeetingDay(int day) {
		if (daysOfWeek[day] == 'y') {
			return true;
		}
		return false;
	}
	
	public void setDaysOfWeek(String days) {
		this.daysOfWeek = days.toCharArray();
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
	
	public void setStartTime(int minutes) {
		startTime = new TimeOfDay(minutes);
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