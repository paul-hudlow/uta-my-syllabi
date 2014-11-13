package edu.uta.mysyllabi.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uta.mysyllabi.MySyllabi;
import edu.uta.mysyllabi.core.Course;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDataHelper extends SQLiteOpenHelper {
	// Version must be incremented upon schema change!
	public static final int DATABASE_VERSION = 45;
    public static final String DATABASE_NAME = "MyCourses.db";
	
    /* Query details */
    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";
    public static final String DELIMITER = ",";
    
    public LocalDataHelper() {
		super(MySyllabi.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
	}
    
	@Override
	public void onCreate(SQLiteDatabase database) {
		createCourseTable(DataContract.Course.TABLE_MAIN, database);
		createCourseTable(DataContract.Course.TABLE_OBSOLETED_BY_LOCAL, database);
		createCourseTable(DataContract.Course.TABLE_OBSOLETED_BY_CLOUD, database);
		createSettingsTable(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		dropTable(DataContract.Course.TABLE_MAIN, database);
		dropTable(DataContract.Course.TABLE_OBSOLETED_BY_LOCAL, database);
		dropTable(DataContract.Course.TABLE_OBSOLETED_BY_CLOUD, database);
		dropTable(DataContract.Settings.TABLE_NAME, database);
		onCreate(database);
	}
    
    /* Uses predefined data in the DataContract class as well as the core Course class to
     * initialize a database table for holding Course object data. */
    public void createCourseTable(String tableName, SQLiteDatabase database) {
    	String firstSegment = "CREATE TABLE " + tableName + "(" +
        		DataContract.Course.COLUMN_ID + INT_TYPE + " PRIMARY KEY" + DELIMITER +
        		DataContract.Course.COLUMN_CLOUD_ID + TEXT_TYPE + DELIMITER;
    	
    	StringBuilder middleSegment = new StringBuilder();
    	String[] middleColumns = new Course(null, null).getContentKeys().toArray(new String[]{});
    	
    	for (int i = 0; i < middleColumns.length; i++) {
    		middleSegment.append(middleColumns[i] + TEXT_TYPE + DELIMITER);
    	}
    	
    	String lastSegment = DataContract.Course.COLUMN_LOCKED + INT_TYPE + DELIMITER +
    						 DataContract.Course.COLUMN_TIME_UPDATED + INT_TYPE + ")";
    	
    	database.execSQL(firstSegment + middleSegment.toString() + lastSegment);
    }
    
    /* Uses predefined data from the DataContract class to create the local settings table. */
    public void createSettingsTable(SQLiteDatabase database) {
    	database.execSQL("CREATE TABLE " + DataContract.Settings.TABLE_NAME + "(" +
        		DataContract.Settings.COLUMN_SETTING + TEXT_TYPE + " PRIMARY KEY" + DELIMITER +
        		DataContract.Settings.COLUMN_VALUE + TEXT_TYPE + ")");
    }
    
    /* Drops the specified table from the specified database. */
    private void dropTable(String tableName, SQLiteDatabase database) {
    	database.execSQL("DROP TABLE IF EXISTS " + tableName);
    }
	
	public String createCourse(Course course) {
		return saveCourse(course, DataContract.Course.TABLE_MAIN);
	}
	
    /* Updates course data (based on the localId of the provided Course object) in the local
     * database to include the cloudId and timeUpdated stored in the provided Course object. */
	public void linkToCloud(Course course) {
		ContentValues values = new ContentValues();
	    values.put(DataContract.Course.COLUMN_CLOUD_ID, course.getCloudId());
	    values.put(DataContract.Course.COLUMN_TIME_UPDATED, course.getUpdateTime());
	    SQLiteDatabase database = this.getWritableDatabase();
	    database.update(DataContract.Course.TABLE_MAIN, values, 
	    		DataContract.Course.COLUMN_ID + " = " + course.getLocalId(), null);
	    database.close();
	    deleteCourse(course.getLocalId(), DataContract.Course.TABLE_OBSOLETED_BY_LOCAL);
	}
	
	/* Saves changes that come from an update to the cloud rather than the local user interface. */
	public void saveFromCloud(Course course) {
		
		/* Store the obsoleted course for the purpose of synchronization, but only if an obsoleted
		 * version of the course does not already exist. */
		Course currentCourse = getCourse(course.getLocalId());
		if (!courseExists(course.getLocalId(), DataContract.Course.TABLE_OBSOLETED_BY_CLOUD)) {
			saveCourse(currentCourse, DataContract.Course.TABLE_OBSOLETED_BY_CLOUD);
		}
		
		/* If changes have been made by the user since the last synchronization, merge the changes
		 * rather than overwriting them. */		
		Course oldCourse = getCourse(course.getLocalId(), DataContract.Course.TABLE_OBSOLETED_BY_LOCAL);
		if (oldCourse != null) {
			Map<String, String> changes = oldCourse.getDifferenceMap(course);
			currentCourse.addContentFromMap(changes);
			currentCourse.setUpdateTime(course.getUpdateTime());
		} else {
			currentCourse = course;
		}
		
		saveCourse(currentCourse, DataContract.Course.TABLE_MAIN);
	}
	
	/* Saves changes that come from the local user interface. */
	public void saveFromLocal(Course course) {
		/* If there were any outstanding changes from updates to the cloud, they should have
		 * been presented to the user before the change. */
		deleteCourse(course.getLocalId(), DataContract.Course.TABLE_OBSOLETED_BY_CLOUD);
		
		/* Only take action if the user has actually changed something. */
		Course oldCourse = getCourse(course.getLocalId());
		if (oldCourse.sharesContents(course)) {
			return;
		} else {
			saveCourse(course, DataContract.Course.TABLE_MAIN);
			
			/* Store the obsoleted course data for the purpose of synchronization, but only if 
			 * an obsoleted version does not already exist. */
			if (!courseExists(course.getLocalId(), DataContract.Course.TABLE_OBSOLETED_BY_LOCAL)) {
				saveCourse(oldCourse, DataContract.Course.TABLE_OBSOLETED_BY_LOCAL);
			}
		}
	}
	
	public String saveCourse(Course course, String tableName) {
		ContentValues values = new ContentValues();
		
		if (course.getCloudId() != null) {
			values.put(DataContract.Course.COLUMN_CLOUD_ID, course.getCloudId());
		}
		
	    if(course.isLocked()) {
	    	values.put(DataContract.Course.COLUMN_LOCKED, DataContract.Course.LOCKED_TRUE);
	    } else {
	    	values.put(DataContract.Course.COLUMN_LOCKED, DataContract.Course.LOCKED_FALSE);
	    }
	    
	    values.put(DataContract.Course.COLUMN_TIME_UPDATED, course.getUpdateTime());
	    
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
	    	long id = database.insert(tableName, null, values);
	    	courseId = Long.toString(id);
	    } else {
	    	values.put(DataContract.Course.COLUMN_ID, courseId);
	    	database.replace(tableName, null, values);
	    }
	    
		values = new ContentValues();
		values.put(DataContract.Settings.COLUMN_SETTING, DataContract.Settings.KEY_SCHOOL);
		values.put(DataContract.Settings.COLUMN_VALUE, course.getSchool());
		database.replace(DataContract.Settings.TABLE_NAME, null, values);

		database.close();
	    return courseId;
	}
	
	public Course getCourse(String localId) {
		return getCourse(localId, DataContract.Course.TABLE_MAIN);
	}
	
	public Course getObsoleteCourse(String localId) {
		return getCourse(localId, DataContract.Course.TABLE_OBSOLETED_BY_CLOUD);
	}
	
	public Course getCourse(String localId, String tableName) {
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor = database.query(tableName, null, 
				DataContract.Course.COLUMN_ID + " = " + localId, null, null, null, null);
		
		if (!tableCursor.moveToFirst()) {
			database.close();
			return null; // Return null if cursor is empty.
		}
		
		Course course = courseFromCursor(tableCursor);
		database.close();
		return course;
	}
	
	public List<Course> getAllUpdatedCourses() {
		List<Course> courseList = getAllCourses();
		LinkedList<Course> updateList = new LinkedList<Course>();
		for (Course nextCourse : courseList) {
			if (getObsoleteCourse(nextCourse.getLocalId()) != null) {
				updateList.add(nextCourse);
			}
		}
		return updateList;
	}
	
	public List<Course> getAllCourses() {
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor = database.query(DataContract.Course.TABLE_MAIN, null, null, null, null, null, null);
		
		LinkedList<Course> courseList = new LinkedList<Course>();
		if (!tableCursor.moveToFirst()) {
			database.close();
			return courseList;
		}
		
		do {
			courseList.add(courseFromCursor(tableCursor));
		} while (tableCursor.moveToNext());
		
		database.close();
		
		return courseList;
	}
	
	public boolean courseExists(String localId, String tableName) {
		Course testCourse = getCourse(localId, tableName);
		if (testCourse == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public void deleteCourse(String localId) {
		deleteCourse(localId, DataContract.Course.TABLE_MAIN);
		deleteCourse(localId, DataContract.Course.TABLE_OBSOLETED_BY_CLOUD);
		deleteCourse(localId, DataContract.Course.TABLE_OBSOLETED_BY_LOCAL);
	}
	
	public void deleteCourse(String localId, String tableName) {
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		database.delete(tableName, DataContract.Course.COLUMN_ID + " = " + localId, null);
		database.close();
	}
	
	/* Checks to see if the any local changes have been made since the last successful synchronization. */
	public boolean hasLocalChanges(String localId) {
		if (courseExists(localId, DataContract.Course.TABLE_OBSOLETED_BY_LOCAL)) {
			return false;
		} else {
			return true;
		}
	}
	
	private Course courseFromCursor(Cursor tableCursor) {
		int localIdIndex = tableCursor.getColumnIndex(DataContract.Course.COLUMN_ID);
		String localId = tableCursor.getString(localIdIndex);
		
		String cloudId = null;
		int cloudIdIndex = tableCursor.getColumnIndex(DataContract.Course.COLUMN_CLOUD_ID);
		if (!tableCursor.isNull(cloudIdIndex)) {
			cloudId = tableCursor.getString(cloudIdIndex);
		}
		
		HashMap<String, String> courseMap = new HashMap<String, String>();
		String[] keys = tableCursor.getColumnNames();
		for (int i = 0; i < keys.length; i++) {
			if (tableCursor.getType(i) == Cursor.FIELD_TYPE_STRING) {
				courseMap.put(keys[i], tableCursor.getString(i));
			}
		}
		
		Course localCourse = new Course(localId, cloudId);
		localCourse.addContentFromMap(courseMap);

		int cloudCheckIndex = tableCursor.getColumnIndex(DataContract.Course.COLUMN_LOCKED);
		if (tableCursor.getInt(cloudCheckIndex) == DataContract.Course.LOCKED_TRUE) {
			localCourse.setLocked(true);
		}
		
		int updateTimeIndex = tableCursor.getColumnIndex(DataContract.Course.COLUMN_TIME_UPDATED);
		if (!tableCursor.isNull(updateTimeIndex)) {
			localCourse.setUpdateTime(tableCursor.getLong(updateTimeIndex));
		}
		
		return localCourse;
	}
	
	public List<String> getCourseKeys() {
		SQLiteDatabase database = this.getReadableDatabase();
		
		String[] column = {DataContract.Course.COLUMN_ID};
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor = database.query(DataContract.Course.TABLE_MAIN, column, null, null, null, null, null);
		
		ArrayList<String> courseKeys = new ArrayList<String>();
		
		if (!tableCursor.moveToFirst()) {
			database.close();
			return courseKeys; // Return empty array if cursor is empty.
		}
		
		do {
			int nextKey = tableCursor.getInt(0);
			courseKeys.add(Integer.toString(nextKey));
		} while (tableCursor.moveToNext());
		
		database.close();
		return courseKeys;
	}
	
	public String getLatestSchool() {
		SQLiteDatabase database = this.getReadableDatabase();
		
		/* Select all data from the entire table and sort by course name. */
		Cursor tableCursor;
		tableCursor = database.query(DataContract.Settings.TABLE_NAME, null, 
				DataContract.Settings.COLUMN_SETTING + " = ?", new String[]{DataContract.Settings.KEY_SCHOOL}, null, null, null);
		
		if (!tableCursor.moveToFirst()) {
			database.close();
			return null; // Return null if cursor is empty.
		}
		
		int schoolIndex = tableCursor.getColumnIndex(DataContract.Settings.COLUMN_VALUE);
		if (tableCursor.isNull(schoolIndex)) {
			database.close();
			return null;
		}
		
		database.close();
		return tableCursor.getString(schoolIndex);
	}

}
