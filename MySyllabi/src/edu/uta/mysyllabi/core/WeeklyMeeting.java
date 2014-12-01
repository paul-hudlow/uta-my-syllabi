package edu.uta.mysyllabi.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uta.mysyllabi.frontend.TimeHolder;

public class WeeklyMeeting implements Mappable.Child, TimeHolder {
	private TimeOfDay startTime; // in minutes from midnight
	private TimeOfDay endTime;
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
	private final String END_TIME = "end_time";
	private final String DAYS_OF_WEEK = "days_of_week";
	private final String LOCATION = "location";
	
	protected final String[] contentKeys = {
			LOCATION,
			START_TIME,
			END_TIME,
			DAYS_OF_WEEK
	};
	
	public WeeklyMeeting() {
		daysOfWeek = new String("nnnnnnn").toCharArray();
	}
	
	public boolean isValid() {
		if (startTime != null && 
			endTime != null &&
			startTime.isBefore(endTime) &&
			!new String(daysOfWeek).equals("nnnnnnn")) {
			return true;
		} else {
			return false;
		}
	}
	
	public List<String> getContentKeys(String keyPrefix) {
		LinkedList<String> keyList = new LinkedList<String>();
		for (int i = 0; i < contentKeys.length; i++) {
			keyList.add(keyPrefix + contentKeys[i]);
		}
		return keyList;
	}
	
	public void addContent(Map<String, String> map, String keyPrefix) {
		setStartTime(map.get(keyPrefix + START_TIME));
		setEndTime(map.get(keyPrefix + END_TIME));
		setDaysOfWeek(map.get(keyPrefix + DAYS_OF_WEEK));
		setLocation(map.get(keyPrefix + LOCATION));
	}
	
	public Map<String, String> getContentMap(String keyPrefix) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		if (this.startTime != null) {
			map.put(keyPrefix + START_TIME, Integer.toString(this.startTime.getTotalMinutes()));
		}
		if (this.endTime != null) {
			map.put(keyPrefix + END_TIME, Integer.toString(this.endTime.getTotalMinutes()));
		}
		map.put(keyPrefix + DAYS_OF_WEEK, new String(this.daysOfWeek));
		map.put(keyPrefix + LOCATION, this.location);
		
		return map;
	}
	
	public String getOccurrence(){
		String days = getDaysOfWeek();
		String occurrence;
		if (startTime != null && this.endTime != null) {
			occurrence = days + " " + startTime.toString(false) + "-" + endTime.toString(false);
		} else if (startTime != null) {
			occurrence = days + " " + startTime.toString(false);
		} else {
			occurrence = days;
		}
		return occurrence;
		
	}
	
	public String getOccurence(){
		String days = getDaysOfWeek();
		return days + " " + startTime.toString(false) + "-" + endTime.toString(false);
	}
	
	public boolean isMeetingDay(int day) {
		if (daysOfWeek[day] == 'y') {
			return true;
		}
		return false;
	}
	
	public void setDaysOfWeek(String days) {
		if (days != null) {
			this.daysOfWeek = days.toCharArray();
		}
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

	public void setStartTime(TimeOfDay time) {
		this.startTime = time;
	}
	
	public TimeOfDay getEndTime() {
		return endTime;
	}
	
	public void setEndTime(TimeOfDay time) {
		this.endTime = time;
	}
	
	public TimeOfDay getStartTime() {
		return startTime;
	}
	
	public void setStartTime(String startTime) {
		if (startTime != null) {
			try {
				this.startTime = new TimeOfDay(Integer.parseInt(startTime));
			} catch (NumberFormatException exception) {
				this.startTime = null;
			}
		}
	}
	
	public void setEndTime(String endTime) {
		if (endTime != null) {
			try {
				this.endTime = new TimeOfDay(Integer.parseInt(endTime));
			} catch (NumberFormatException exception) {
				this.endTime = null;
			}
		}
	}
	
	public void setStartTime(int minutes) {
		startTime = new TimeOfDay(minutes);
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		if (location != null) {
			this.location = location;
		}
	}
}