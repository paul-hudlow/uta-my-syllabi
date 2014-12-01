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
	
	
	public static abstract class Course implements BaseColumns {
		public static final String TABLE_MAIN = "course_table";
		public static final String TABLE_OBSOLETED_BY_LOCAL = "course_table_local";
		public static final String TABLE_OBSOLETED_BY_CLOUD = "course_table_cloud";
		
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_CLOUD_ID = "cloud_id";
		public static final String COLUMN_TIME_UPDATED = "time_updated";
		public static final String COLUMN_LOCKED = "locked";
		
		public static final String LOCKED_TRUE = "locked";
		public static final String LOCKED_FALSE = "unlocked";
	}
	
	public static abstract class Event implements BaseColumns {
		public static final String TABLE_MAIN = "event_table";
		public static final String TABLE_OBSOLETED_BY_LOCAL = "event_table_local";
		public static final String TABLE_OBSOLETED_BY_CLOUD = "event_table_cloud";
		
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_CLOUD_ID = "cloud_id";
		public static final String COLUMN_COURSE_ID = "course_id";
		public static final String COLUMN_TIME_UPDATED = "time_updated";
		public static final String COLUMN_LOCKED = "locked";
		
		public static final int LOCKED_TRUE = 1;
		public static final int LOCKED_FALSE = 0;
	}
	
	
	
}
