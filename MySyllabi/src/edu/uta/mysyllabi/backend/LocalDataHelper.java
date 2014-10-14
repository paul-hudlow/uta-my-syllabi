package edu.uta.mysyllabi.backend;

import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.backend.DataContract.Syllabi;
import edu.uta.mysyllabi.core.Course;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDataHelper extends SQLiteOpenHelper {
	// Version must be incremented upon schema change!
	public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "MyCourses.db";
	
    /* Query details */
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";
    public static final String DELIMITER = ",";
    
    public static final String CREATE_TABLE_SYLLABI = "CREATE TABLE " + DataContract.Syllabi.TABLE_NAME + "(" +
    		DataContract.Syllabi.COLUMN_COURSE_ID + INT_TYPE + " PRIMARY KEY" + DELIMITER +
    		DataContract.Syllabi.COLUMN_COURSE_NAME + TEXT_TYPE + DELIMITER +
    		DataContract.Syllabi.COLUMN_COURSE_TITLE + TEXT_TYPE + DELIMITER +
    		DataContract.Syllabi.COLUMN_MEETING_START_TIME + INT_TYPE + DELIMITER +
    		DataContract.Syllabi.COLUMN_MEETING_DURATION + INT_TYPE + DELIMITER +
    		DataContract.Syllabi.COLUMN_MEETING_LOCATION + TEXT_TYPE + DELIMITER +
    		DataContract.Syllabi.COLUMN_MEETING_DAYS + TEXT_TYPE + DELIMITER +
    		DataContract.Syllabi.COLUMN_INSTRUCTOR_NAME + TEXT_TYPE + 
    		")";
    
    public static final String DROP_TABLE_SYLLABI = "DROP TABLE IF EXISTS " + DataContract.Syllabi.TABLE_NAME;
    
    
	public LocalDataHelper() {
		super(MySyllabi.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_SYLLABI);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL(DROP_TABLE_SYLLABI);
		onCreate(database);
	}
	
	public static void saveCourse(Course syllabus) {		
		/*ContentValues values = new ContentValues();
	    values.put(DataContract.Syllabi.COLUMN_COURSE_NAME, syllabus.name);
	    values.put(DataContract.Syllabi.COLUMN_COURSE_TITLE, syllabus.title);
	    values.put(DataContract.Syllabi.COLUMN_INSTRUCTOR_NAME, syllabus.instructor.getName());
	    
	    if (syllabus.meeting != null) {
		    values.put(DataContract.Syllabi.COLUMN_MEETING_START_TIME, syllabus.meeting.getStartTime());
		    values.put(DataContract.Syllabi.COLUMN_MEETING_DURATION, syllabus.meeting.getDuration());
		    values.put(DataContract.Syllabi.COLUMN_MEETING_DAYS, syllabus.meeting.getDaysOfWeek());
		    values.put(DataContract.Syllabi.COLUMN_MEETING_LOCATION, syllabus.meeting.getLocation());
	    }
	    
	    SQLiteDatabase database = this.getWritableDatabase();
		database.insert(DataContract.Syllabi.TABLE_NAME, null, values); */
	}
	
	public static void saveCourse(Course course, String cloudId) {
		// TODO implement method
	}
	
	public static Course getCourse(String localId) {
		Course localCourse = new Course(localId, null);
		// TODO implement method
		return localCourse;
	}
	
	public int[] getCourseKeys() {
		// TODO implement method
		return null;
	}

}
