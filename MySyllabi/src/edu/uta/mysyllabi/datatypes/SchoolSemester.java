package edu.uta.mysyllabi.datatypes;

import java.util.Calendar;

public class SchoolSemester {
	public static int SPRING = 0;
	public static int SUMMER = 1;
	public static int FALL = 2;
	public static int WINTER = 3;
	private final int year;
	private final int season;
	private String[] season_string = {"Spring", "Summer", "Fall", "Winter"};

	public SchoolSemester(int year, int season) {
		this.year = year;
		this.season = season;
	}
	
	@Override
	public String toString() {
		return season_string[season] + " " + year;
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
