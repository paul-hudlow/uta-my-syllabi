package edu.uta.mysyllabi.backend;

import android.provider.BaseColumns;

public final class DataContract {
	// Empty constructor prevents accidental instantiation of the class.
	public DataContract() {}
	
	public static abstract class Settings implements BaseColumns {
		public static final String TABLE_NAME = "settings";
		
		public static final String COLUMN_SETTING = "setting";
		
		public static final String COLUMN_VALUE = "value";
		
		public static final String KEY_SCHOOL = "last_school";
	}
	
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
	
	
	public static abstract class Course implements BaseColumns {
		public static final String TABLE_NAME = "course_table";
		public static final String UPDATES_TABLE_NAME = "course_updates_table";
		
		public static final String COLUMN_ID = "id"; // Internal use only.
		public static final String COLUMN_CLOUD_ID = "cloud_id";
		public static final String COLUMN_TIME_UPDATED = "time_updated";
		public static final String COLUMN_LOCKED = "locked";
		public static final int LOCKED_TRUE = 1;
		public static final int LOCKED_FALSE = 0;
		
	}
	
	
	
}
