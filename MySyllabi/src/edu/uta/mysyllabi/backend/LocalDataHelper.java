package edu.uta.mysyllabi.backend;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.core.Course;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDataHelper extends SQLiteOpenHelper {
	// Version must be incremented upon schema change!
	public static final int DATABASE_VERSION = 23;
    public static final String DATABASE_NAME = "MyCourses.db";
	
    /* Query details */
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";
    public static final String DELIMITER = ",";
    
    public String getCreationString() {
    	String firstSegment = "CREATE TABLE " + DataContract.Course.TABLE_NAME + "(" +
        		DataContract.Course.COLUMN_ID + INT_TYPE + " PRIMARY KEY" + DELIMITER +
        		DataContract.Course.COLUMN_CLOUD_ID + TEXT_TYPE + DELIMITER;
    	
    	StringBuilder middleSegment = new StringBuilder();
    	String[] middleColumns = Course.getContentKeys();
    	
    	for (int i = 0; i < middleColumns.length; i++) {
    		middleSegment.append(middleColumns[i] + TEXT_TYPE + DELIMITER);
    	}
    	
    	String lastSegment = DataContract.Course.COLUMN_ON_CLOUD + INT_TYPE + ")";
    	
    	return firstSegment + middleSegment.toString() + lastSegment;
    }
    
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
		database.execSQL(getCreationString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL(DROP_TABLE_COURSE);
		onCreate(database);
	}
	
	public String saveCourse(Course course) {
		ContentValues values = new ContentValues();
		
		if (course.getCloudId() != null) {
			values.put(DataContract.Course.COLUMN_CLOUD_ID, course.getCloudId());
		}
		
	    if(course.isOnCloud()) {
	    	values.put(DataContract.Course.COLUMN_ON_CLOUD, 1);
	    } else {
	    	values.put(DataContract.Course.COLUMN_ON_CLOUD, 0);
	    }
	    
		HashMap<String, String> courseMap = course.getContentMap();
		String[] keys = Course.getContentKeys();
		String nextValue;
		for (int i = 0; i < keys.length; i++) {
			nextValue = courseMap.get(keys[i]);
			if (nextValue != null) {
				values.put(keys[i], nextValue);
			}
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
		
		HashMap<String, String> courseMap = new HashMap<String, String>();
		String[] keys = tableCursor.getColumnNames();
		for (int i = 0; i < keys.length; i++) {
			if (tableCursor.getType(i) == Cursor.FIELD_TYPE_STRING) {
				courseMap.put(keys[i], tableCursor.getString(i));
			}
		}
		
		Course localCourse = new Course(courseMap);
		localCourse.setLocalId(localId);
		
		int cloudIdIndex = tableCursor.getColumnIndex(DataContract.Course.COLUMN_CLOUD_ID);
		if (!tableCursor.isNull(cloudIdIndex)) {
			localCourse.setCloudId(tableCursor.getString(cloudIdIndex));
		}

		int cloudCheckIndex = tableCursor.getColumnIndex(DataContract.Course.COLUMN_ON_CLOUD);
		if (tableCursor.getInt(cloudCheckIndex) == 1) {
			localCourse.setOnCloud(true);
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
