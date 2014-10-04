package edu.uta.mysyllabi;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class DatabaseHelper extends SQLiteOpenHelper {
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
    
    
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
	
	public void addCourse(Syllabus syllabus) {		
		ContentValues values = new ContentValues();
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
		database.insert(DataContract.Syllabi.TABLE_NAME, null, values);
		database.close();
	}
	
	public ArrayList<Bundle> getSyllabi() {
		ArrayList<Bundle> syllabusList = new ArrayList<Bundle>();
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor = database.query(DataContract.Syllabi.TABLE_NAME, null, null, null, null, null, 
											DataContract.Syllabi.COLUMN_COURSE_NAME);
		
		if (!tableCursor.moveToFirst()) {
			return syllabusList; // Return empty list if cursor is empty.
		}
		
		Bundle nextBundle;
		
		do {
			nextBundle = new Bundle();
			int columnCount = tableCursor.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				switch (tableCursor.getType(i)){
				case Cursor.FIELD_TYPE_INTEGER:
					nextBundle.putInt(tableCursor.getColumnName(i), tableCursor.getInt(i));
					break;
				case Cursor.FIELD_TYPE_STRING:
					nextBundle.putString(tableCursor.getColumnName(i), tableCursor.getString(i));
				}
			}
			syllabusList.add(nextBundle);
		} while (tableCursor.moveToNext()); // Keep going until there are no more rows in cursor.
		
		tableCursor.close();
		database.close();
		
		return syllabusList;
	}
	
	public ArrayList<Integer> getSyllabusKeys() {
		ArrayList<Integer> keyList = new ArrayList<Integer>();
		
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		String[] keyColumn = {DataContract.Syllabi.COLUMN_COURSE_ID};
		Cursor tableCursor = database.query(DataContract.Syllabi.TABLE_NAME, keyColumn, null, null, 
											null, null, DataContract.Syllabi.COLUMN_COURSE_NAME);
		
		if (!tableCursor.moveToFirst()) {
			return keyList; // Return empty list if no data was returned.
		}
		
		do {
			keyList.add(tableCursor.getInt(0)); // Add key from only selected column.
		} while (tableCursor.moveToNext());

		return keyList;
	}
	
	public Syllabus getSyllabus(int syllabusKey) {
		Syllabus syllabus = new Syllabus(syllabusKey);
		SQLiteDatabase database = this.getReadableDatabase(); // Instantiate database API.
		
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor = database.query(DataContract.Syllabi.TABLE_NAME, null, DataContract.Syllabi.COLUMN_COURSE_ID + "=" + 
											Integer.toString(syllabusKey), null, null, null, DataContract.Syllabi.COLUMN_COURSE_NAME);
		
		if (!tableCursor.moveToFirst()) {
			return null; // Return null if no syllabus data was returned.
		}
		
		int columnIndex; // Used repeatedly to store index of desired cursor columns.
		
		/* Create new syllabus with id. */
		columnIndex = tableCursor.getColumnIndexOrThrow(DataContract.Syllabi.COLUMN_COURSE_ID);
		syllabus = new Syllabus(tableCursor.getInt(columnIndex));
		
		/* Import course name. */
		columnIndex = tableCursor.getColumnIndexOrThrow(DataContract.Syllabi.COLUMN_COURSE_NAME);
		syllabus.name = tableCursor.getString(columnIndex);
		
		/* Import course title. */
		columnIndex = tableCursor.getColumnIndexOrThrow(DataContract.Syllabi.COLUMN_COURSE_TITLE);
		syllabus.title = tableCursor.getString(columnIndex);
		
		/* Get course meeting information. */
		columnIndex = tableCursor.getColumnIndexOrThrow(DataContract.Syllabi.COLUMN_MEETING_LOCATION);
		String meetingLocation = tableCursor.getString(columnIndex);
		
		columnIndex = tableCursor.getColumnIndexOrThrow(DataContract.Syllabi.COLUMN_MEETING_START_TIME);
		int meetingStart = tableCursor.getInt(columnIndex);
		
		columnIndex = tableCursor.getColumnIndexOrThrow(DataContract.Syllabi.COLUMN_MEETING_DURATION);
		int meetingDuration = tableCursor.getInt(columnIndex);
		
		columnIndex = tableCursor.getColumnIndexOrThrow(DataContract.Syllabi.COLUMN_MEETING_DAYS);
		char[] meetingDays = new String("yyyyynn").toCharArray();// tableCursor.getString(columnIndex).toCharArray();
		
		/* Add course meeting information to syllabus. */
		//syllabus.meeting = new WeeklyMeeting(meetingStart, meetingDuration, meetingDays, meetingLocation);
		
		/* Import instructor name. */
		columnIndex = tableCursor.getColumnIndexOrThrow(DataContract.Syllabi.COLUMN_INSTRUCTOR_NAME);
		syllabus.instructor = new Instructor(tableCursor.getString(columnIndex));
		
		/* Clean up. */
		tableCursor.close();
		database.close();
		
		return syllabus;
	}

}
