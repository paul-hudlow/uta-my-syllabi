package edu.uta.mysyllabi.backend;

import java.util.ArrayList;

import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.core.Course;
import edu.uta.mysyllabi.datatypes.Instructor;
import edu.uta.mysyllabi.datatypes.SchoolSemester;
import edu.uta.mysyllabi.datatypes.TimeOfDay;
import edu.uta.mysyllabi.datatypes.WeeklyMeeting;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDataHelper extends SQLiteOpenHelper {
	// Version must be incremented upon schema change!
	public static final int DATABASE_VERSION = 17;
    public static final String DATABASE_NAME = "MyCourses.db";
	
    /* Query details */
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";
    public static final String DELIMITER = ",";
    
    public static final String CREATE_TABLE_Course = "CREATE TABLE " + DataContract.Course.TABLE_NAME + "(" +
    		DataContract.Course.COLUMN_ID + INT_TYPE + " PRIMARY KEY" + DELIMITER +
    		DataContract.Course.COLUMN_CLOUD_ID + TEXT_TYPE + DELIMITER +
    		DataContract.Course.COLUMN_NAME + TEXT_TYPE + DELIMITER +
    		DataContract.Course.COLUMN_SECTION + TEXT_TYPE + DELIMITER +
    		DataContract.Course.COLUMN_TITLE + TEXT_TYPE + DELIMITER +
    		DataContract.Course.COLUMN_SCHOOL + TEXT_TYPE + DELIMITER +
    		DataContract.Course.COLUMN_SEMESTER + TEXT_TYPE + DELIMITER +
    		DataContract.Meeting.COLUMN_START_TIME + INT_TYPE + DELIMITER +
    		DataContract.Meeting.COLUMN_DURATION + INT_TYPE + DELIMITER +
    		DataContract.Meeting.COLUMN_LOCATION + TEXT_TYPE + DELIMITER +
    		DataContract.Meeting.COLUMN_DAYS + TEXT_TYPE + DELIMITER +
    		DataContract.Instructor.COLUMN_FIRST_NAME + TEXT_TYPE + DELIMITER +
    		DataContract.Instructor.COLUMN_LAST_NAME + TEXT_TYPE + DELIMITER +
    		DataContract.Instructor.COLUMN_PHONE_NUMBER + TEXT_TYPE + DELIMITER +
    		DataContract.Instructor.COLUMN_EMAIL_ADDRESS + TEXT_TYPE + DELIMITER +
    		DataContract.Instructor.COLUMN_OFFICE_ID + TEXT_TYPE +
    		")";
    
    public static final String DROP_TABLE_COURSE = "DROP TABLE IF EXISTS " + DataContract.Course.TABLE_NAME;
    
    
	public LocalDataHelper() {
		super(MySyllabi.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void addCloudId(String localId, String cloudId) {
		ContentValues values = new ContentValues();
	    values.put(DataContract.Course.COLUMN_CLOUD_ID, cloudId);
	    SQLiteDatabase database = this.getWritableDatabase();
	    database.update(DataContract.Course.TABLE_NAME, values, 
	    		DataContract.Course.COLUMN_ID + " = " + localId, null);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_Course);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL(DROP_TABLE_COURSE);
		onCreate(database);
	}
	
	public String saveCourse(Course course) {
		ContentValues values = new ContentValues();
		
	    values.put(DataContract.Course.COLUMN_NAME, course.getName());
	    values.put(DataContract.Course.COLUMN_TITLE, course.getTitle());
	    values.put(DataContract.Course.COLUMN_SECTION, course.getSection());
	    values.put(DataContract.Course.COLUMN_SCHOOL, course.getSchool());
	    values.put(DataContract.Course.COLUMN_SEMESTER, course.getSemester().toString());
	    
	    Instructor instructor = course.getInstructor();
	    if (instructor != null) {
	    	values.put(DataContract.Instructor.COLUMN_FIRST_NAME, instructor.getFirstName());
	    	values.put(DataContract.Instructor.COLUMN_LAST_NAME, instructor.getLastName());
	    	values.put(DataContract.Instructor.COLUMN_OFFICE_ID, instructor.getOfficeId());
	    	values.put(DataContract.Instructor.COLUMN_PHONE_NUMBER, instructor.getPhoneNumber());
	    	values.put(DataContract.Instructor.COLUMN_EMAIL_ADDRESS, instructor.getEmailAddress());
	    }
	    
	    WeeklyMeeting meeting = course.getMeeting();
	    if (meeting != null && meeting.getStartTime() != null) {
		    values.put(DataContract.Meeting.COLUMN_START_TIME, meeting.getStartTime().getTotalMinutes());
		    values.put(DataContract.Meeting.COLUMN_DURATION, meeting.getDuration());
		    values.put(DataContract.Meeting.COLUMN_DAYS, meeting.getDaysData());
		    values.put(DataContract.Meeting.COLUMN_LOCATION, meeting.getLocation());
	    }
	    
	    String courseId = course.getLocalId();
	    SQLiteDatabase database = this.getWritableDatabase();
	    if (courseId == null) {
	    	long id = database.insert(DataContract.Course.TABLE_NAME, null, values);
	    	courseId = Long.toString(id);
	    } else {
	    	int rowsUpdated = database.update(DataContract.Course.TABLE_NAME, values, 
	    			DataContract.Course.COLUMN_ID + " = " + courseId, null);
	    	if (rowsUpdated < 1) {
	    		throw new SQLException("Course could not be updated!");
	    	} else if (rowsUpdated > 1) {
	    		throw new SQLException("Duplicate courses exist in table!");
	    	}
	    }

	    return courseId;
	}
	
	public void deleteCourse(String localId) {
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		database.delete(DataContract.Course.TABLE_NAME, DataContract.Course.COLUMN_ID + " = " + localId, null);
	}
	
	public Course getCourse(String localId) {
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor = database.query(DataContract.Course.TABLE_NAME, null, 
				DataContract.Course.COLUMN_ID + " = " + localId, null, null, null, null);
		
		if (!tableCursor.moveToFirst()) {
			return null; // Return null if cursor is empty.
		}
		
		Course localCourse = new Course(localId, null);
		try {
			localCourse.setCloudId(tableCursor.getString(tableCursor.getColumnIndex(DataContract.Course.COLUMN_CLOUD_ID)));
		} catch (Exception exception) {
			// Forget the cloud id.
		}
		
		/* Add basic course information. */
		localCourse.setName(tableCursor.getString(tableCursor.getColumnIndex(DataContract.Course.COLUMN_NAME)));
		localCourse.setTitle(tableCursor.getString(tableCursor.getColumnIndex(DataContract.Course.COLUMN_TITLE)));
		localCourse.setSection(tableCursor.getString(tableCursor.getColumnIndex(DataContract.Course.COLUMN_SECTION)));
		localCourse.setSchool(tableCursor.getString(tableCursor.getColumnIndex(DataContract.Course.COLUMN_SCHOOL)));
		localCourse.setSemester(new SchoolSemester(tableCursor.getString(tableCursor.getColumnIndex(DataContract.Course.COLUMN_SEMESTER))));
		
		/* Build the course meeting. */
		try {
			if (tableCursor.getString(tableCursor.getColumnIndex(DataContract.Meeting.COLUMN_START_TIME)) != null) {
					WeeklyMeeting meeting = new WeeklyMeeting();
					meeting.setStartTime(new TimeOfDay(tableCursor.getInt(tableCursor.getColumnIndex(DataContract.Meeting.COLUMN_START_TIME))));
					meeting.setDuration(tableCursor.getInt(tableCursor.getColumnIndex(DataContract.Meeting.COLUMN_DURATION)));
					meeting.setDaysOfWeek(tableCursor.getString(tableCursor.getColumnIndex(DataContract.Meeting.COLUMN_DAYS)));
					meeting.setLocation(tableCursor.getString(tableCursor.getColumnIndex(DataContract.Meeting.COLUMN_LOCATION)));
					localCourse.setMeeting(meeting);
			}
		} catch (Exception exception) {
			// Forget meeting.
		}
		
		/* Build the course instructor. */
		try {
			if (tableCursor.getString(tableCursor.getColumnIndex(DataContract.Instructor.COLUMN_LAST_NAME)) != null) {
					String firstName = tableCursor.getString(tableCursor.getColumnIndex(DataContract.Instructor.COLUMN_FIRST_NAME));
					String lastName = tableCursor.getString(tableCursor.getColumnIndex(DataContract.Instructor.COLUMN_LAST_NAME));
					Instructor instructor = new Instructor(firstName, lastName);
					instructor.setOfficeId(tableCursor.getString(tableCursor.getColumnIndex(DataContract.Instructor.COLUMN_OFFICE_ID)));
					instructor.setPhoneNumber(tableCursor.getString(tableCursor.getColumnIndex(DataContract.Instructor.COLUMN_PHONE_NUMBER)));
					instructor.setEmailAddress(tableCursor.getString(tableCursor.getColumnIndex(DataContract.Instructor.COLUMN_EMAIL_ADDRESS)));
					localCourse.setInstructor(instructor);
			}
		} catch (Exception exception) {
			// Forget instructor.
		}
		
		return localCourse;
		
	}
	
	public ArrayList<String> getCourseKeys() {
		SQLiteDatabase database = this.getReadableDatabase();
		
		String[] column = {DataContract.Course.COLUMN_ID};
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor = database.query(DataContract.Course.TABLE_NAME, column, null, null, null, null, null);
		
		ArrayList<String> courseKeys = new ArrayList<String>();
		
		if (!tableCursor.moveToFirst()) {
			return courseKeys; // Return empty array if cursor is empty.
		}
		
		do {
			int nextKey = tableCursor.getInt(0);
			courseKeys.add(Integer.toString(nextKey));
		} while (tableCursor.moveToNext());
		
		return courseKeys;
	}

}
