package edu.uta.mysyllabi.backend;

import android.provider.BaseColumns;

public final class DataContract {
	// Empty constructor prevents accidental instantiation of the class.
	public DataContract() {}
	
	public static abstract class Meeting implements BaseColumns {
		
		/* location of class meetings */
		public static final String COLUMN_LOCATION = "location";
		
		/* number of minutes from the beginning of the day to the start time of the class */
		public static final String COLUMN_START_TIME = "meeting_start";
		
		/* length of class meetings in minutes */
		public static final String COLUMN_DURATION = "meeting_duration";
		
		/* days of the week in which the class meets */
		public static final String COLUMN_DAYS = "meeting_days";
		
	}
	
	public static abstract class Instructor implements BaseColumns {
		
		public static final String COLUMN_FIRST_NAME = "first_name";
		public static final String COLUMN_LAST_NAME = "last_name";
		public static final String COLUMN_EMAIL_ADDRESS = "email";
		public static final String COLUMN_PHONE_NUMBER = "phone";
		public static final String COLUMN_OFFICE_ID = "office";
		
	}
	
	public static abstract class Course implements BaseColumns {
		public static final String TABLE_NAME = "course_table";
		
		public static final String COLUMN_ID = "id"; // Internal use only.
		public static final String COLUMN_CLOUD_ID = "cloud_id";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_SECTION = "section";
		public static final String COLUMN_TITLE = "title";
		public static final String COLUMN_SCHOOL = "school";
		public static final String COLUMN_SEMESTER = "semester";
		public static final String COLUMN_ON_CLOUD= "onCloud"; 
		
	}
	
	
	
}
