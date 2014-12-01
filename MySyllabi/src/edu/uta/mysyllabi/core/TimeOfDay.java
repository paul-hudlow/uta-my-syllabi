package edu.uta.mysyllabi.core;

import java.util.Calendar;
import java.util.Date;

public class TimeOfDay {
	public static final int MAX_HOUR = 23;
	public static final int MAX_MINUTE = 59;
	private final String AM = "am";
	private final String PM = "pm";
	private final int hour;
	private final int minute;
	
	public TimeOfDay(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}
	
	public TimeOfDay(int totalMinutes) {
		hour = totalMinutes/60;
		minute = totalMinutes%60;
	}
	
	public TimeOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
	}
	
	public String toString(boolean in24HourFormat) {
		String hourString;
		String postfix;
		if (!in24HourFormat) {
			if (hour > 12) {
				hourString = Integer.toString(hour - 12);
				postfix = PM;
			} else if (hour == 12) {
				hourString = "12";
				postfix = PM;
			} else if (hour == 0) {
				hourString = "12";
				postfix = AM;
			} else {
				hourString = Integer.toString(hour);
				postfix = AM;
			}
		} else {
			postfix = "";
			if (hour < 10) {
				hourString = "0" + Integer.toString(hour);
			} else {
				hourString = Integer.toString(hour);
			}
		}
		String minuteString;
		if (minute < 10) {
			minuteString = "0" + Integer.toString(minute);
		} else {
			minuteString = Integer.toString(minute);
		}
		
		return hourString + ":" + minuteString + postfix;
	}
	
	public int getHour() {
		return hour;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public int getTotalMinutes() {
		return hour*60 + minute;
	}

	public boolean isBefore(TimeOfDay otherTime) {
		if (this.getTotalMinutes() < otherTime.getTotalMinutes()) {
			return true;
		}
		return false;
	}
}
