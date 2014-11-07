package edu.uta.mysyllabi.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.core.Course;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDataHelper extends SQLiteOpenHelper {
	// Version must be incremented upon schema change!
	public static final int DATABASE_VERSION = 40;
    public static final String DATABASE_NAME = "MyCourses.db";
	
    /* Query details */
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";
    public static final String DELIMITER = ",";
    
    public void createCourseTable(SQLiteDatabase database) {
    	String firstSegment = "CREATE TABLE " + DataContract.Course.TABLE_NAME + "(" +
        		DataContract.Course.COLUMN_ID + INT_TYPE + " PRIMARY KEY" + DELIMITER +
        		DataContract.Course.COLUMN_CLOUD_ID + TEXT_TYPE + DELIMITER;
    	
    	StringBuilder middleSegment = new StringBuilder();
    	String[] middleColumns = new Course(null, null).getContentKeys().toArray(new String[]{});
    	
    	for (int i = 0; i < middleColumns.length; i++) {
    		middleSegment.append(middleColumns[i] + TEXT_TYPE + DELIMITER);
    	}
    	
    	String lastSegment = DataContract.Course.COLUMN_LOCKED + INT_TYPE + ")";
    	
    	database.execSQL(firstSegment + middleSegment.toString() + lastSegment);
    }
    
    public void createSettingsTable(SQLiteDatabase database) {
    	database.execSQL("CREATE TABLE " + DataContract.Settings.TABLE_NAME + "(" +
        		DataContract.Settings.COLUMN_SETTING + TEXT_TYPE + " PRIMARY KEY" + DELIMITER +
        		DataContract.Settings.COLUMN_VALUE + TEXT_TYPE + ")");
    }
    
    public static final String DROP_TABLE_COURSE = "DROP TABLE IF EXISTS " + DataContract.Course.TABLE_NAME;
    
    public static final String DROP_TABLE_SETTINGS = "DROP TABLE IF EXISTS " + DataContract.Settings.TABLE_NAME;
    
    
	public LocalDataHelper() {
		super(MySyllabi.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void addCloudId(String localId, String cloudId) {
		ContentValues values = new ContentValues();
	    values.put(DataContract.Course.COLUMN_CLOUD_ID, cloudId);
	    SQLiteDatabase database = this.getWritableDatabase();
	    database.update(DataContract.Course.TABLE_NAME, values, 
	    		DataContract.Course.COLUMN_ID + " = " + localId, null);
	    database.close();
	}
	
	public String getCloudId(String localId) {
		SQLiteDatabase database = this.getReadableDatabase();
		
		String[] column = {DataContract.Course.COLUMN_CLOUD_ID};
		
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor = database.query(DataContract.Course.TABLE_NAME, column, 
				DataContract.Course.COLUMN_ID + " = " + localId, null, null, null, null);
		
		if (!tableCursor.moveToFirst()) {
			return null; // Return null if cursor is empty.
		}
		
		int cloudIdIndex = tableCursor.getColumnIndex(DataContract.Course.COLUMN_CLOUD_ID);
		if (tableCursor.isNull(cloudIdIndex)) {
			return null;
		}
		
		database.close();
		return tableCursor.getString(cloudIdIndex);
	}
	
	public String getLatestSchool() {
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor;
		tableCursor = database.query(DataContract.Settings.TABLE_NAME, null, 
				DataContract.Settings.COLUMN_SETTING + " = ?", new String[]{DataContract.Settings.KEY_SCHOOL}, null, null, null);
		
		if (!tableCursor.moveToFirst()) {
			return null; // Return null if cursor is empty.
		}
		
		int schoolIndex = tableCursor.getColumnIndex(DataContract.Settings.COLUMN_VALUE);
		if (tableCursor.isNull(schoolIndex)) {
			return null;
		}
		
		database.close();
		return tableCursor.getString(schoolIndex);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		createCourseTable(database);
		createSettingsTable(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL(DROP_TABLE_COURSE);
		database.execSQL(DROP_TABLE_SETTINGS);
		onCreate(database);
	}
	
	public String saveCourse(Course course) {
		ContentValues values = new ContentValues();
		
		if (course.getCloudId() != null) {
			values.put(DataContract.Course.COLUMN_CLOUD_ID, course.getCloudId());
		}
		
	    if(course.isLocked()) {
	    	values.put(DataContract.Course.COLUMN_LOCKED, DataContract.Course.LOCKED_TRUE);
	    } else {
	    	values.put(DataContract.Course.COLUMN_LOCKED, DataContract.Course.LOCKED_FALSE);
	    }
	    
		Map<String, String> courseMap = course.getContentMap();
		List<String> keys = course.getContentKeys();
		String nextValue;
		for (String nextKey : keys) {
			nextValue = courseMap.get(nextKey);
			if (nextValue != null) {
				values.put(nextKey, nextValue);
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
	    
		values = new ContentValues();
		values.put(DataContract.Settings.COLUMN_SETTING, DataContract.Settings.KEY_SCHOOL);
		values.put(DataContract.Settings.COLUMN_VALUE, course.getSchool());
		database.replace(DataContract.Settings.TABLE_NAME, null, values);

		database.close();
	    return courseId;
	}
	
	public void deleteCourse(String localId) {
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		database.delete(DataContract.Course.TABLE_NAME, DataContract.Course.COLUMN_ID + " = " + localId, null);
		database.close();
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
		
		Course localCourse = new Course(localId, null);
		localCourse.addContentFromMap(courseMap);
		
		int cloudIdIndex = tableCursor.getColumnIndex(DataContract.Course.COLUMN_CLOUD_ID);
		if (!tableCursor.isNull(cloudIdIndex)) {
			localCourse.setCloudId(tableCursor.getString(cloudIdIndex));
		}

		int cloudCheckIndex = tableCursor.getColumnIndex(DataContract.Course.COLUMN_LOCKED);
		if (tableCursor.getInt(cloudCheckIndex) == DataContract.Course.LOCKED_TRUE) {
			localCourse.setLocked(true);
		}
		
		database.close();
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
		
		database.close();
		return courseKeys;
	}

}
