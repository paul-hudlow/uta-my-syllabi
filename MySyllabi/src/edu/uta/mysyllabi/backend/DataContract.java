package edu.uta.mysyllabi.backend;

import android.provider.BaseColumns;

public final class DataContract {
	// Empty constructor prevents accidental instantiation of the class.
	public DataContract() {}
	
	/* contents of the syllabi table */
	public static abstract class Syllabi implements BaseColumns {
		public static final String TABLE_NAME = "syllabi";
		
		/* course identity information */
		public static final String COLUMN_COURSE_ID = "id"; // Internal use only.
		public static final String COLUMN_COURSE_NAME = "name";
		public static final String COLUMN_COURSE_TITLE = "title";
		
		/* location of class meetings */
		public static final String COLUMN_MEETING_LOCATION = "location";
		
		/* number of minutes from the beginning of the day to the start time of the class */
		public static final String COLUMN_MEETING_START_TIME = "meeting_start";
		
		/* length of class meetings in minutes */
		public static final String COLUMN_MEETING_DURATION = "meeting_duration";
		
		/* days of the week in which the class meets */
		public static final String COLUMN_MEETING_DAYS = "meeting_days";
		
		/* course instructor information */
		public static final String COLUMN_INSTRUCTOR_NAME = "instructor_name";
	}
	
}
