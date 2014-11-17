package edu.uta.mysyllabi.core;

import java.util.Calendar;

public class SchoolSemester {
	public static int SPRING = 0;
	public static int SUMMER = 1;
	public static int FALL = 2;
	public static int WINTER = 3;
	private final int year;
	private final int season;
	private String[] seasonString = {"Spring", "Summer", "Fall", "Winter"};
	
	public SchoolSemester(String semester) {
		String[] segmented = semester.split(" ");
		if (segmented.length != 2) {
			throw new IllegalArgumentException("Semester string must have two words!");
		}
		this.year = Integer.parseInt(segmented[1]);
		
		for (int i = 0; i < seasonString.length; i++) {
			if (seasonString[i].equals(segmented[0])) {
				this.season = i;
				return;
			}
		}
		throw new IllegalArgumentException("Season string '" + segmented[0] + "' is invalid!");
	}
	
	public SchoolSemester(int year, int season) {
		this.year = year;
		this.season = season;
	}
	
	@Override
	public String toString() {
		return seasonString[season] + " " + year;
	}
	
	public static SchoolSemester getCurrent() {
		Calendar currentDate = Calendar.getInstance();
		int year = currentDate.get(Calendar.YEAR);
		int month = currentDate.get(Calendar.MONTH);
		int season;
		if (month >= Calendar.JANUARY && month < Calendar.MAY) {
			season = SPRING;
		} else if (month >= Calendar.MAY && month < Calendar.AUGUST) {
			season = SUMMER;
		} else if (month >= Calendar.AUGUST && month < Calendar.DECEMBER) {
			season = FALL;
		} else {
			season = WINTER;
		}
		return new SchoolSemester(year, season);
	}
	
	public SchoolSemester getNext() {
		if (season == WINTER) {
			return new SchoolSemester(year + 1, SPRING);
		} else {
			return new SchoolSemester(year, season + 1);
		}
	}
	
	public SchoolSemester getPrevious() {
		if (season == SPRING) {
			return new SchoolSemester(year - 1, WINTER);
		} else {
			return new SchoolSemester(year, season - 1);
		}
	}
}
